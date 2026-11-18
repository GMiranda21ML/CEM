import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router';
import { chamarApi, clienteConsultas, mensagemErro, useAutenticacao, useNotificacoes } from '../../../d5';
import {
  type AutorizacoesCandidatura,
  type AutorizacoesSelecao,
  consultaDisciplinasProfessor,
  consultaSelecoes,
  type SelecaoProjecao,
} from '../api';

interface FormSelecao {
  disciplina: string;
  prazoInscricao: string;
  vagas: number;
  crMinimo: number;
  pesoCr: number;
  pesoEntrevista: number;
  patamarMinimo: number;
}

function formatarData(iso: string | null) {
  if (!iso) return '—';
  const partes = iso.slice(0, 10).split('-');
  if (partes.length !== 3) return iso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaSelecoes());
  return null;
}

function FormularioSelecao({
  emEdicao,
  aoCancelarEdicao,
  aoConcluir,
}: {
  emEdicao: SelecaoProjecao | null;
  aoCancelarEdicao: () => void;
  aoConcluir: () => void;
}) {
  const { data: disciplinas } = useSuspenseQuery(consultaDisciplinasProfessor);
  const { publicar } = useNotificacoes();
  const { register, handleSubmit, reset, watch, setValue, formState } = useForm<FormSelecao>({
    mode: 'onChange',
    values: emEdicao
      ? {
          disciplina: emEdicao.disciplina,
          prazoInscricao: emEdicao.prazoInscricao,
          vagas: emEdicao.vagas,
          crMinimo: emEdicao.crMinimo,
          pesoCr: emEdicao.pesoCr,
          pesoEntrevista: emEdicao.pesoEntrevista,
          patamarMinimo: emEdicao.patamarMinimo,
        }
      : {
          disciplina: disciplinas[0]?.identificador ?? '',
          prazoInscricao: new Date(Date.now() + 14 * 86400000).toISOString().slice(0, 10),
          vagas: 2,
          crMinimo: 7,
          pesoCr: 40,
          pesoEntrevista: 60,
          patamarMinimo: 7,
        },
  });

  const pesoCr = Number(watch('pesoCr'));
  const pesoEntrevista = Number(watch('pesoEntrevista'));
  const somaValida = pesoCr >= 0 && pesoEntrevista >= 0 && pesoCr + pesoEntrevista === 100;

  const salvar = useMutation({
    mutationFn: (dados: FormSelecao) => {
      if (emEdicao) {
        return chamarApi(`/backend/monitoria/selecao/${emEdicao.identificador}/editar`, {
          method: 'POST',
          corpo: {
            prazoInscricao: dados.prazoInscricao,
            vagas: Number(dados.vagas),
            crMinimo: Number(dados.crMinimo),
            pesoCr: Number(dados.pesoCr),
            pesoEntrevista: Number(dados.pesoEntrevista),
            patamarMinimo: Number(dados.patamarMinimo),
          },
        });
      }
      return chamarApi('/backend/monitoria/selecao/abrir', {
        method: 'POST',
        corpo: {
          disciplina: dados.disciplina,
          prazoInscricao: dados.prazoInscricao,
          vagas: Number(dados.vagas),
          crMinimo: Number(dados.crMinimo),
          pesoCr: Number(dados.pesoCr),
          pesoEntrevista: Number(dados.pesoEntrevista),
          patamarMinimo: Number(dados.patamarMinimo),
        },
      });
    },
    onSuccess: () => {
      publicar(
        'sucesso',
        emEdicao ? 'Critérios da seleção atualizados.' : 'Seleção de monitoria aberta.',
      );
      reset();
      aoCancelarEdicao();
      aoConcluir();
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  if (!disciplinas.length && !emEdicao) {
    return (
      <section className="panel">
        <h2>Abrir seleção</h2>
        <p className="empty">
          Nenhuma disciplina está vinculada a você como professor responsável no momento.
        </p>
      </section>
    );
  }

  return (
    <section className="panel">
      <h2>{emEdicao ? `Editar seleção · ${emEdicao.disciplinaNome}` : 'Abrir seleção de monitor'}</h2>
      <p className="panel-sub">
        Defina vagas, prazo de inscrição, CR mínimo, pesos (soma igual a 100%) e patamar mínimo de
        aprovação.
      </p>
      <form
        className="form-grid"
        onSubmit={handleSubmit((dados) => salvar.mutate(dados))}
        noValidate
      >
        <div className="field full">
          <label htmlFor="selecao-disciplina">Disciplina *</label>
          <select
            id="selecao-disciplina"
            disabled={Boolean(emEdicao)}
            {...register('disciplina', { required: true })}
          >
            <option value="">Selecione...</option>
            {disciplinas.map((d) => (
              <option key={d.identificador} value={d.identificador}>
                {d.codigo} · {d.nome} ({d.cursoNome})
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="selecao-prazo">Prazo de inscrição *</label>
          <input
            id="selecao-prazo"
            type="date"
            {...register('prazoInscricao', { required: true })}
          />
        </div>

        <div className="field">
          <label htmlFor="selecao-vagas">Vagas *</label>
          <input
            id="selecao-vagas"
            type="number"
            min={1}
            {...register('vagas', { required: true, min: 1, valueAsNumber: true })}
          />
        </div>

        <div className="field">
          <label htmlFor="selecao-cr-minimo">CR mínimo (0 a 10) *</label>
          <input
            id="selecao-cr-minimo"
            type="number"
            step="0.1"
            min={0}
            max={10}
            {...register('crMinimo', { required: true, min: 0, max: 10, valueAsNumber: true })}
          />
        </div>

        <div className="field">
          <label htmlFor="selecao-patamar">Patamar mínimo final *</label>
          <input
            id="selecao-patamar"
            type="number"
            step="0.1"
            min={0}
            max={10}
            {...register('patamarMinimo', { required: true, min: 0, max: 10, valueAsNumber: true })}
          />
        </div>

        <div className="field">
          <label htmlFor="selecao-peso-cr">Peso CR (%) *</label>
          <input
            id="selecao-peso-cr"
            type="number"
            min={0}
            max={100}
            {...register('pesoCr', {
              required: true,
              min: 0,
              max: 100,
              valueAsNumber: true,
              onChange: (e) => {
                const v = Number(e.target.value);
                if (!Number.isNaN(v) && v >= 0 && v <= 100) {
                  setValue('pesoEntrevista', 100 - v, { shouldValidate: true });
                }
              },
            })}
          />
        </div>

        <div className="field">
          <label htmlFor="selecao-peso-entrevista">Peso entrevista (%) *</label>
          <input
            id="selecao-peso-entrevista"
            type="number"
            min={0}
            max={100}
            {...register('pesoEntrevista', {
              required: true,
              min: 0,
              max: 100,
              valueAsNumber: true,
            })}
          />
          <small>
            Soma atual: {Number.isFinite(pesoCr + pesoEntrevista) ? pesoCr + pesoEntrevista : 0}%
            {!somaValida ? ' (os pesos devem somar exatamente 100%)' : ''}
          </small>
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={!formState.isValid || !somaValida || salvar.isPending}
          >
            {emEdicao ? 'Salvar critérios' : 'Abrir seleção'}
          </button>
          {emEdicao && (
            <button
              className="button button-ghost"
              type="button"
              onClick={aoCancelarEdicao}
            >
              Cancelar edição
            </button>
          )}
        </div>
      </form>
    </section>
  );
}

export function MinhasSelecoes() {
  const { autorizacoesDe } = useAutenticacao();
  const { publicar } = useNotificacoes();
  const [busca, setBusca] = useState('');
  const [emEdicao, setEmEdicao] = useState<SelecaoProjecao | null>(null);
  const { data } = useSuspenseQuery(consultaSelecoes(busca));
  const autSelecao = autorizacoesDe('selecao') as AutorizacoesSelecao | undefined;
  const autCandidatura = autorizacoesDe('candidatura') as AutorizacoesCandidatura | undefined;

  const atualizar = () =>
    clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });

  const encerrar = useMutation({
    mutationFn: (id: string) =>
      chamarApi(`/backend/monitoria/selecao/${id}/encerrar`, { method: 'POST' }),
    onSuccess: () => {
      atualizar();
      publicar('sucesso', 'Seleção encerrada e candidaturas pendentes reprovadas.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const selecoes = data.resultado.projecoes;

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · F006</span>
          <h1>Seleções de monitoria</h1>
          <p>
            Gerencie editais por disciplina, acompanhe inscrições no semestre e libere a avaliação
            após o término do prazo.
          </p>
        </div>
      </div>

      <div className={autSelecao?.abrir ? 'content-grid' : ''}>
        {autSelecao?.abrir && (
          <FormularioSelecao
            emEdicao={emEdicao}
            aoCancelarEdicao={() => setEmEdicao(null)}
            aoConcluir={atualizar}
          />
        )}

        <section className="panel">
          <h2>Seleções registradas</h2>
          <p className="panel-sub">{data.resultado.total} seleção(ões) encontradas.</p>

          <div className="toolbar">
            <div className="field">
              <label htmlFor="busca-selecao">Filtrar por disciplina</label>
              <input
                id="busca-selecao"
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
                placeholder="Nome ou código da disciplina"
              />
            </div>
          </div>

          {!selecoes.length && (
            <p className="empty">Nenhuma seleção de monitoria encontrada.</p>
          )}

          {selecoes.map((s) => {
            const podeEditar =
              autSelecao?.abrir && s.situacao === 'ABERTA' && s.totalCandidaturas === 0;
            const podeEncerrar = autSelecao?.encerrar && s.situacao === 'ABERTA';

            return (
              <article className="item-card" key={s.identificador}>
                <div className="item-card-head">
                  <div>
                    <strong>
                      {s.disciplinaCodigo} · {s.disciplinaNome}
                    </strong>
                    <div className="subtext">
                      {s.cursoNome} · Prof. {s.professorNome} · Semestre {s.semestre}
                    </div>
                  </div>
                  <span
                    className={`status-badge ${s.situacao === 'ABERTA' ? 'aprovada' : 'encerrada'}`}
                  >
                    {s.situacao === 'ABERTA' ? 'Aberta' : 'Encerrada'}
                  </span>
                </div>

                <div className="subtext">
                  Prazo: <strong>{formatarData(s.prazoInscricao)}</strong> · Vagas:{' '}
                  <strong>
                    {s.aprovadas}/{s.vagas}
                  </strong>{' '}
                  · CR mínimo: <strong>{s.crMinimo.toFixed(1)}</strong> · Pesos:{' '}
                  <strong>
                    {s.pesoCr}% CR / {s.pesoEntrevista}% Entrevista
                  </strong>{' '}
                  · Patamar: <strong>{s.patamarMinimo.toFixed(1)}</strong>
                </div>

                <div className="subtext" style={{ marginTop: 4 }}>
                  Candidaturas: {s.totalCandidaturas} total ({s.pendentes} pendente(s),{' '}
                  {s.aprovadas} aprovada(s))
                  {s.motivoEncerramento ? ` · Encerrada: ${s.motivoEncerramento}` : ''}
                </div>

                {s.monitoresSelecionados.length > 0 && (
                  <div className="chips">
                    {s.monitoresSelecionados.map((nome) => (
                      <span className="chip" key={nome}>
                        Monitor: {nome}
                      </span>
                    ))}
                  </div>
                )}

                <div className="row-actions" style={{ marginTop: 12 }}>
                  {autCandidatura?.aprovar && (
                    <Link
                      className="button button-secondary button-small"
                      to={`/monitoria/candidatura/avaliacao?selecao=${s.identificador}`}
                    >
                      Avaliar candidatos
                    </Link>
                  )}
                  {autSelecao?.abrir && s.situacao === 'ABERTA' && (
                    <button
                      type="button"
                      className="button button-ghost button-small"
                      disabled={!podeEditar}
                      title={
                        s.totalCandidaturas > 0
                          ? 'Critérios bloqueados após a primeira candidatura.'
                          : 'Editar critérios'
                      }
                      onClick={() => setEmEdicao(s)}
                    >
                      Editar critérios
                    </button>
                  )}
                  {podeEncerrar && (
                    <button
                      type="button"
                      className="button button-danger button-small"
                      disabled={encerrar.isPending}
                      onClick={() => encerrar.mutate(s.identificador)}
                    >
                      Encerrar seleção
                    </button>
                  )}
                </div>
              </article>
            );
          })}
        </section>
      </div>
    </>
  );
}
