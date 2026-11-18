import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router';
import { chamarApi, clienteConsultas, mensagemErro, useAutenticacao, useNotificacoes } from '../../../d5';
import {
  type AcompanhamentoProjecao,
  type AutorizacoesAcompanhamento,
  consultaAcompanhamentos,
  consultaOpcoesAcompanhamento,
} from '../api';

interface FormAbrirAcompanhamento {
  alvo: string;
  metaNota: number;
  metaFrequencia: number;
}

function formatarData(iso: string | null) {
  if (!iso) return '—';
  const partes = iso.slice(0, 10).split('-');
  if (partes.length !== 3) return iso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function rotuloSituacaoDesempenho(situacao: AcompanhamentoProjecao['situacaoDesempenho']) {
  switch (situacao) {
    case 'META_ATINGIDA':
      return { texto: 'Meta atingida', classe: 'aprovada' };
    case 'EM_PROGRESSO':
      return { texto: 'Em progresso', classe: 'aprovada' };
    case 'ATENCAO':
      return { texto: 'Atenção', classe: 'reprovada' };
    case 'ESTAVEL':
      return { texto: 'Estável', classe: '' };
    default:
      return { texto: 'Dados insuficientes', classe: 'pendente' };
  }
}

function rotuloTendencia(tendencia: AcompanhamentoProjecao['tendencia']) {
  switch (tendencia) {
    case 'EM_EVOLUCAO':
      return 'Em evolução';
    case 'EM_QUEDA':
      return 'Em queda';
    case 'ESTAVEL':
      return 'Estável';
    default:
      return 'Dados insuficientes';
  }
}

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaAcompanhamentos());
  return null;
}

function FormularioAbrir({ aoConcluir }: { aoConcluir: () => void }) {
  const { data: opcoes } = useSuspenseQuery(consultaOpcoesAcompanhamento);
  const { publicar } = useNotificacoes();
  const { register, handleSubmit, reset, formState } = useForm<FormAbrirAcompanhamento>({
    mode: 'onChange',
    values: {
      alvo: opcoes[0] ? `${opcoes[0].aluno}::${opcoes[0].disciplina}` : '',
      metaNota: 7.5,
      metaFrequencia: 75,
    },
  });

  const abrir = useMutation({
    mutationFn: (dados: FormAbrirAcompanhamento) => {
      const [aluno, disciplina] = dados.alvo.split('::');
      return chamarApi('/backend/monitoria/acompanhamento/abrir', {
        method: 'POST',
        corpo: {
          aluno,
          disciplina,
          metaNota: Number(dados.metaNota),
          metaFrequencia: Number(dados.metaFrequencia),
        },
      });
    },
    onSuccess: () => {
      reset();
      aoConcluir();
      publicar('sucesso', 'Acompanhamento aberto.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <section className="panel">
      <h2>Abrir acompanhamento</h2>
      <p className="panel-sub">
        Selecione um aluno ativo do mesmo curso da disciplina em que você atua como monitor.
      </p>
      {!opcoes.length ? (
        <p className="empty">
          Não há novos pares de aluno e disciplina disponíveis para abertura de acompanhamento.
        </p>
      ) : (
        <form
          className="form-grid"
          onSubmit={handleSubmit((dados) => abrir.mutate(dados))}
          noValidate
        >
          <div className="field full">
            <label htmlFor="acomp-alvo">Aluno e disciplina *</label>
            <select id="acomp-alvo" {...register('alvo', { required: true })}>
              <option value="">Selecione...</option>
              {opcoes.map((o) => (
                <option key={`${o.aluno}::${o.disciplina}`} value={`${o.aluno}::${o.disciplina}`}>
                  {o.alunoNome} · {o.disciplinaNome} ({o.cursoNome})
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label htmlFor="acomp-meta-nota">Meta de nota (0 a 10) *</label>
            <input
              id="acomp-meta-nota"
              type="number"
              step="0.1"
              min={0}
              max={10}
              {...register('metaNota', { required: true, min: 0, max: 10, valueAsNumber: true })}
            />
          </div>
          <div className="field">
            <label htmlFor="acomp-meta-freq">Meta de frequência (50% a 100%) *</label>
            <input
              id="acomp-meta-freq"
              type="number"
              min={50}
              max={100}
              {...register('metaFrequencia', {
                required: true,
                min: 50,
                max: 100,
                valueAsNumber: true,
              })}
            />
          </div>
          <div className="form-actions">
            <button
              className="button button-primary"
              type="submit"
              disabled={!formState.isValid || abrir.isPending}
            >
              Abrir acompanhamento
            </button>
          </div>
        </form>
      )}
    </section>
  );
}

function CartaoAcompanhamento({
  acomp,
  podeEditar,
  podeRelatorio,
  aoAtualizar,
}: {
  acomp: AcompanhamentoProjecao;
  podeEditar: boolean;
  podeRelatorio: boolean;
  aoAtualizar: () => void;
}) {
  const { publicar } = useNotificacoes();
  const [editandoMetas, setEditandoMetas] = useState(false);
  const [encerrando, setEncerrando] = useState(false);
  const [metaNota, setMetaNota] = useState(String(acomp.metaNota));
  const [metaFrequencia, setMetaFrequencia] = useState(String(acomp.metaFrequencia));
  const [parecerFinal, setParecerFinal] = useState('');

  const salvarMetas = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/acompanhamento/${acomp.identificador}/metas`, {
        method: 'POST',
        corpo: { metaNota: Number(metaNota), metaFrequencia: Number(metaFrequencia) },
      }),
    onSuccess: () => {
      setEditandoMetas(false);
      aoAtualizar();
      publicar('sucesso', 'Metas atualizadas.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const encerrar = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/acompanhamento/${acomp.identificador}/encerrar`, {
        method: 'POST',
        corpo: { parecerFinal },
      }),
    onSuccess: () => {
      setEncerrando(false);
      setParecerFinal('');
      aoAtualizar();
      publicar('sucesso', 'Acompanhamento encerrado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const excluir = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/acompanhamento/${acomp.identificador}/excluir`, {
        method: 'POST',
      }),
    onSuccess: () => {
      aoAtualizar();
      publicar('sucesso', 'Acompanhamento excluído.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const situacao = rotuloSituacaoDesempenho(acomp.situacaoDesempenho);
  const ativo = acomp.situacao === 'ATIVO';

  return (
    <article className="item-card">
      <div className="item-card-head">
        <div>
          <strong>
            {acomp.alunoNome} · {acomp.disciplinaNome}
          </strong>
          <div className="subtext">
            Monitor: {acomp.monitorNome} · Prof. {acomp.professorNome} · Semestre {acomp.semestre} ·
            Aberto em {formatarData(acomp.abertoEm)}
          </div>
        </div>
        <div className="row-actions">
          <span className={`status-badge ${situacao.classe}`}>{situacao.texto}</span>
          <span className={`status-badge ${ativo ? 'aprovada' : 'encerrada'}`}>
            {ativo ? 'Ativo' : 'Encerrado'}
          </span>
        </div>
      </div>

      <div className="subtext">
        Meta de nota: <strong>{acomp.metaNota.toFixed(1)}</strong> · Meta de frequência:{' '}
        <strong>{acomp.metaFrequencia}%</strong> · Nota inicial:{' '}
        <strong>{acomp.notaInicial !== null ? acomp.notaInicial.toFixed(2) : '—'}</strong> · Nota
        atual: <strong>{acomp.notaAtual !== null ? acomp.notaAtual.toFixed(2) : '—'}</strong> ·
        Frequência:{' '}
        <strong>
          {acomp.frequenciaPercentual !== null ? `${acomp.frequenciaPercentual.toFixed(1)}%` : '—'}
        </strong>{' '}
        · Tendência: <strong>{rotuloTendencia(acomp.tendencia)}</strong>
      </div>

      {acomp.parecerFinal && (
        <div className="subtext preservar-texto" style={{ marginTop: 6 }}>
          Parecer final ({formatarData(acomp.encerradoEm)}): {acomp.parecerFinal}
        </div>
      )}

      {editandoMetas && ativo && (
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            salvarMetas.mutate();
          }}
        >
          <input
            aria-label="Nova meta de nota"
            type="number"
            step="0.1"
            min={0}
            max={10}
            value={metaNota}
            onChange={(e) => setMetaNota(e.target.value)}
          />
          <input
            aria-label="Nova meta de frequência"
            type="number"
            min={50}
            max={100}
            value={metaFrequencia}
            onChange={(e) => setMetaFrequencia(e.target.value)}
          />
          <button
            className="button button-primary button-small"
            type="submit"
            disabled={salvarMetas.isPending}
          >
            Salvar metas
          </button>
          <button
            className="button button-ghost button-small"
            type="button"
            onClick={() => setEditandoMetas(false)}
          >
            Cancelar
          </button>
        </form>
      )}

      {encerrando && ativo && (
        <form
          className="form-grid"
          style={{ marginTop: 10 }}
          onSubmit={(e) => {
            e.preventDefault();
            encerrar.mutate();
          }}
        >
          <div className="field full">
            <label htmlFor={`parecer-${acomp.identificador}`}>
              Parecer final (mínimo 20 caracteres) *
            </label>
            <textarea
              id={`parecer-${acomp.identificador}`}
              rows={2}
              value={parecerFinal}
              onChange={(e) => setParecerFinal(e.target.value)}
              placeholder="Descreva a evolução e o fechamento do acompanhamento..."
            />
          </div>
          <div className="form-actions">
            <button
              className="button button-primary button-small"
              type="submit"
              disabled={parecerFinal.trim().length < 20 || encerrar.isPending}
            >
              Confirmar encerramento
            </button>
            <button
              className="button button-ghost button-small"
              type="button"
              onClick={() => setEncerrando(false)}
            >
              Cancelar
            </button>
          </div>
        </form>
      )}

      <div className="row-actions" style={{ marginTop: 12 }}>
        {podeRelatorio && (
          <Link
            className="button button-secondary button-small"
            to={`/monitoria/acompanhamento/relatorios?id=${acomp.identificador}`}
          >
            Ver relatório
          </Link>
        )}
        {podeEditar && ativo && (
          <>
            <button
              type="button"
              className="button button-ghost button-small"
              onClick={() => setEditandoMetas((v) => !v)}
            >
              Editar metas
            </button>
            <button
              type="button"
              className="button button-ghost button-small"
              onClick={() => setEncerrando((v) => !v)}
            >
              Encerrar
            </button>
            <button
              type="button"
              className="button button-danger button-small"
              disabled={acomp.possuiFeedback || excluir.isPending}
              title={
                acomp.possuiFeedback
                  ? 'Acompanhamentos com feedback na disciplina não podem ser excluídos; encerre-o.'
                  : 'Excluir acompanhamento'
              }
              onClick={() => excluir.mutate()}
            >
              Excluir
            </button>
          </>
        )}
      </div>
    </article>
  );
}

export function Acompanhamentos() {
  const { autorizacoesDe } = useAutenticacao();
  const [busca, setBusca] = useState('');
  const { data } = useSuspenseQuery(consultaAcompanhamentos(busca));
  const aut = autorizacoesDe('acompanhamento') as AutorizacoesAcompanhamento | undefined;

  const atualizar = () =>
    clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · F013</span>
          <h1>Acompanhamentos de progresso</h1>
          <p>
            Defina metas de nota e frequência por aluno e acompanhe os resultados consolidados ao
            longo das sessões.
          </p>
        </div>
      </div>

      <div className={aut?.abrir ? 'content-grid' : ''}>
        {aut?.abrir && <FormularioAbrir aoConcluir={atualizar} />}

        <section className="panel">
          <h2>Acompanhamentos registrados</h2>
          <p className="panel-sub">{data.resultado.total} acompanhamento(s).</p>

          <div className="toolbar">
            <div className="field">
              <label htmlFor="busca-acomp">Filtrar</label>
              <input
                id="busca-acomp"
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
                placeholder="Aluno, disciplina ou monitor"
              />
            </div>
          </div>

          {!data.resultado.projecoes.length && (
            <p className="empty">Nenhum acompanhamento registrado.</p>
          )}

          {data.resultado.projecoes.map((a) => (
            <CartaoAcompanhamento
              key={a.identificador}
              acomp={a}
              podeEditar={Boolean(aut?.editarMetas)}
              podeRelatorio={Boolean(aut?.relatorio)}
              aoAtualizar={atualizar}
            />
          ))}
        </section>
      </div>
    </>
  );
}
