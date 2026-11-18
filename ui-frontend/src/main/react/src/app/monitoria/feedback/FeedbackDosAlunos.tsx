import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { chamarApi, clienteConsultas, mensagemErro, useAutenticacao, useNotificacoes } from '../../../d5';
import {
  type AutorizacoesFeedback,
  consultaAcompanhamentos,
  consultaContexto,
  consultaFeedbacks,
  consultaSessoes,
  type FeedbackProjecao,
} from '../api';

interface FormFeedback {
  sessao: string;
  aluno: string;
  nota: number;
  comentario: string;
}

function formatarDataHora(iso: string) {
  const data = iso.slice(0, 10).split('-');
  const hora = iso.slice(11, 16);
  if (data.length !== 3) return iso;
  return `${data[2]}/${data[1]}/${data[0]}${hora ? ` ${hora}` : ''}`;
}

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaFeedbacks());
  return null;
}

function FormularioRegistrarFeedback({ aoConcluir }: { aoConcluir: () => void }) {
  const { data: sessoesData } = useSuspenseQuery(consultaSessoes());
  const { data: contexto } = useSuspenseQuery(consultaContexto);
  const { data: acompData } = useSuspenseQuery(consultaAcompanhamentos());
  const { publicar } = useNotificacoes();

  const sessoesRealizadas = sessoesData.resultado.projecoes.filter((s) => !s.cancelada);
  const alunosPessoas = contexto.pessoas.filter((p) => p.papel === 'ALUNO');
  const alunosAcomp = acompData.resultado.projecoes;

  const { register, handleSubmit, reset, formState } = useForm<FormFeedback>({
    mode: 'onChange',
    values: {
      sessao: sessoesRealizadas[0]?.identificador ?? '',
      aluno: alunosAcomp[0]?.aluno ?? alunosPessoas[0]?.identificador ?? '',
      nota: 8.0,
      comentario: '',
    },
  });

  const registrar = useMutation({
    mutationFn: (dados: FormFeedback) =>
      chamarApi('/backend/monitoria/feedback/registrar', {
        method: 'POST',
        corpo: {
          sessao: dados.sessao,
          aluno: dados.aluno,
          nota: Number(dados.nota),
          comentario: dados.comentario.trim() || null,
        },
      }),
    onSuccess: () => {
      reset({ nota: 8.0, comentario: '', sessao: '', aluno: '' });
      aoConcluir();
      publicar('sucesso', 'Feedback registrado para a sessão.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <section className="panel">
      <h2>Registrar feedback da sessão</h2>
      <p className="panel-sub">
        Disponível para sessões já realizadas pelo monitor sem falta do monitor nem do aluno.
      </p>

      <form
        className="form-grid"
        onSubmit={handleSubmit((dados) => registrar.mutate(dados))}
        noValidate
      >
        <div className="field full">
          <label htmlFor="fb-sessao">Sessão *</label>
          <select id="fb-sessao" {...register('sessao', { required: true })}>
            <option value="">Selecione uma sessão...</option>
            {sessoesRealizadas.map((s) => (
              <option key={s.identificador} value={s.identificador}>
                {s.disciplina} · {formatarDataHora(s.inicio)} ({s.sala})
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="fb-aluno">Aluno *</label>
          <select id="fb-aluno" {...register('aluno', { required: true })}>
            <option value="">Selecione um aluno...</option>
            {alunosPessoas.map((p) => (
              <option key={p.identificador} value={p.identificador}>
                {p.nome}
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="fb-nota">Nota (0 a 10) *</label>
          <input
            id="fb-nota"
            type="number"
            step="0.1"
            min={0}
            max={10}
            {...register('nota', { required: true, min: 0, max: 10, valueAsNumber: true })}
          />
        </div>

        <div className="field full">
          <label htmlFor="fb-comentario">Comentário (opcional)</label>
          <textarea
            id="fb-comentario"
            rows={3}
            maxLength={1000}
            {...register('comentario', { maxLength: 1000 })}
            placeholder="Observações sobre evolução, dúvidas e participação na sessão..."
          />
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={!formState.isValid || registrar.isPending}
          >
            Registrar feedback
          </button>
        </div>
      </form>
    </section>
  );
}

function CartaoFeedback({
  feedback,
  podeGerenciar,
  aoAtualizar,
}: {
  feedback: FeedbackProjecao;
  podeGerenciar: boolean;
  aoAtualizar: () => void;
}) {
  const { publicar } = useNotificacoes();
  const [editando, setEditando] = useState(false);
  const [nota, setNota] = useState(String(feedback.nota));
  const [comentario, setComentario] = useState(feedback.comentario ?? '');

  const salvar = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/feedback/${feedback.identificador}/editar`, {
        method: 'POST',
        corpo: { nota: Number(nota), comentario: comentario.trim() || null },
      }),
    onSuccess: () => {
      setEditando(false);
      aoAtualizar();
      publicar('sucesso', 'Feedback atualizado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const excluir = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/feedback/${feedback.identificador}/excluir`, {
        method: 'POST',
      }),
    onSuccess: () => {
      aoAtualizar();
      publicar('sucesso', 'Feedback excluído.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <article className="item-card">
      <div className="item-card-head">
        <div>
          <strong>
            {feedback.alunoNome} · {feedback.disciplinaNome}
          </strong>
          <div className="subtext">Sessão de {formatarDataHora(feedback.sessaoInicio)}</div>
        </div>
        <div className="row-actions">
          <span className="status-badge aprovada">Nota {feedback.nota.toFixed(1)}</span>
          {feedback.bloqueado && (
            <span className="status-badge encerrada">Acompanhamento encerrado</span>
          )}
        </div>
      </div>

      {feedback.comentario && (
        <p className="preservar-texto" style={{ margin: '6px 0' }}>
          {feedback.comentario}
        </p>
      )}

      <div className="subtext">
        Registrado em {formatarDataHora(feedback.criadoEm)}
        {feedback.atualizadoEm ? ` · Editado em ${formatarDataHora(feedback.atualizadoEm)}` : ''}
      </div>

      {editando && !feedback.bloqueado && (
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            salvar.mutate();
          }}
        >
          <input
            aria-label="Editar nota"
            type="number"
            step="0.1"
            min={0}
            max={10}
            value={nota}
            onChange={(e) => setNota(e.target.value)}
          />
          <input
            className="grow-input"
            aria-label="Editar comentário"
            value={comentario}
            onChange={(e) => setComentario(e.target.value)}
            placeholder="Comentário"
          />
          <button
            className="button button-primary button-small"
            type="submit"
            disabled={salvar.isPending}
          >
            Salvar
          </button>
          <button
            className="button button-ghost button-small"
            type="button"
            onClick={() => setEditando(false)}
          >
            Cancelar
          </button>
        </form>
      )}

      {podeGerenciar && (
        <div className="row-actions" style={{ marginTop: 10 }}>
          <button
            type="button"
            className="button button-ghost button-small"
            disabled={feedback.bloqueado}
            onClick={() => setEditando((v) => !v)}
          >
            Editar
          </button>
          <button
            type="button"
            className="button button-danger button-small"
            disabled={feedback.bloqueado || excluir.isPending}
            onClick={() => excluir.mutate()}
          >
            Excluir
          </button>
        </div>
      )}
    </article>
  );
}

export function FeedbackDosAlunos() {
  const { autorizacoesDe } = useAutenticacao();
  const [busca, setBusca] = useState('');
  const { data } = useSuspenseQuery(consultaFeedbacks(busca));
  const aut = autorizacoesDe('feedback') as AutorizacoesFeedback | undefined;

  const atualizar = () =>
    clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · F013</span>
          <h1>Feedback dos alunos por sessão</h1>
          <p>
            Consulte e registre avaliações de progresso após cada sessão realizada sem falta do
            monitor.
          </p>
        </div>
      </div>

      <div className={aut?.registrar ? 'content-grid' : ''}>
        {aut?.registrar && <FormularioRegistrarFeedback aoConcluir={atualizar} />}

        <section className="panel">
          <h2>Feedbacks registrados</h2>
          <p className="panel-sub">{data.resultado.total} feedback(s).</p>

          <div className="toolbar">
            <div className="field">
              <label htmlFor="busca-fb">Filtrar</label>
              <input
                id="busca-fb"
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
                placeholder="Aluno, disciplina ou comentário"
              />
            </div>
          </div>

          {!data.resultado.projecoes.length && (
            <p className="empty">Nenhum feedback registrado até o momento.</p>
          )}

          {data.resultado.projecoes.map((f) => (
            <CartaoFeedback
              key={f.identificador}
              feedback={f}
              podeGerenciar={Boolean(aut?.registrar)}
              aoAtualizar={atualizar}
            />
          ))}
        </section>
      </div>
    </>
  );
}
