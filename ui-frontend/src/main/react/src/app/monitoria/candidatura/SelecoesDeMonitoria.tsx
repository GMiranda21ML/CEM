import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { chamarApi, clienteConsultas, mensagemErro, useAutenticacao, useNotificacoes } from '../../../d5';
import {
  type AutorizacoesCandidatura,
  consultaCandidaturas,
  consultaSelecoesAbertas,
  type SelecaoAberta,
} from '../api';

function formatarData(iso: string | null) {
  if (!iso) return '—';
  const partes = iso.slice(0, 10).split('-');
  if (partes.length !== 3) return iso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function rotuloSituacao(situacao: string) {
  switch (situacao) {
    case 'APROVADA':
      return { texto: 'Aprovada', classe: 'aprovada' };
    case 'REPROVADA':
      return { texto: 'Reprovada', classe: 'reprovada' };
    case 'CANCELADA':
      return { texto: 'Cancelada', classe: 'encerrada' };
    default:
      return { texto: 'Pendente', classe: 'pendente' };
  }
}

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaCandidaturas());
  return null;
}

function CartaoSelecaoAberta({ item }: { item: SelecaoAberta }) {
  const { publicar } = useNotificacoes();
  const [cr, setCr] = useState(item.selecao.crMinimo.toFixed(1));
  const candidatar = useMutation({
    mutationFn: () =>
      chamarApi('/backend/monitoria/candidatura/candidatar', {
        method: 'POST',
        corpo: { selecao: item.selecao.identificador, cr: Number(cr) },
      }),
    onSuccess: () => {
      clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });
      publicar('sucesso', `Candidatura enviada para ${item.selecao.disciplinaNome}.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const s = item.selecao;
  const crNumero = Number(cr);
  const crValido = !Number.isNaN(crNumero) && crNumero >= s.crMinimo && crNumero <= 10;

  return (
    <article className="item-card">
      <div className="item-card-head">
        <div>
          <strong>
            {s.disciplinaCodigo} · {s.disciplinaNome}
          </strong>
          <div className="subtext">
            {s.cursoNome} · Prof. {s.professorNome} · Semestre {s.semestre}
          </div>
        </div>
        <span className={`status-badge ${item.elegivel ? 'aprovada' : 'pendente'}`}>
          {item.elegivel
            ? 'Elegível'
            : item.situacaoCandidaturaAtiva
              ? `Status: ${item.situacaoCandidaturaAtiva}`
              : 'Inelegível'}
        </span>
      </div>

      <div className="subtext">
        Prazo: <strong>{formatarData(s.prazoInscricao)}</strong> · Vagas:{' '}
        <strong>{s.vagas}</strong> · CR mínimo: <strong>{s.crMinimo.toFixed(1)}</strong> · Pesos:{' '}
        <strong>
          {s.pesoCr}% CR + {s.pesoEntrevista}% Entrevista
        </strong>{' '}
        · Patamar: <strong>{s.patamarMinimo.toFixed(1)}</strong>
      </div>

      <ul className="checklist">
        <li className={item.cursoCompativel ? 'ok' : 'falha'}>
          {item.cursoCompativel ? '✓' : '✕'} Curso compatível ({s.cursoNome})
        </li>
        <li className={item.limiteDisponivel ? 'ok' : 'falha'}>
          {item.limiteDisponivel ? '✓' : '✕'} Limite no semestre ({item.monitoriasNoSemestre}/2
          monitorias)
        </li>
        <li className={item.semCandidaturaAtiva ? 'ok' : 'falha'}>
          {item.semCandidaturaAtiva ? '✓' : '✕'} Sem candidatura pendente ou aprovada nesta
          disciplina
        </li>
        <li className={item.dentroDoPrazo ? 'ok' : 'falha'}>
          {item.dentroDoPrazo ? '✓' : '✕'} Inscrições abertas até {formatarData(s.prazoInscricao)}
        </li>
      </ul>

      {item.elegivel && (
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            if (crValido) candidatar.mutate();
          }}
        >
          <div className="field" style={{ maxWidth: 180 }}>
            <label htmlFor={`cr-${s.identificador}`}>Seu CR (0 a 10) *</label>
            <input
              id={`cr-${s.identificador}`}
              type="number"
              step="0.01"
              min={0}
              max={10}
              value={cr}
              onChange={(e) => setCr(e.target.value)}
            />
          </div>
          <button
            className="button button-primary"
            type="submit"
            disabled={!crValido || candidatar.isPending}
          >
            Candidatar-se
          </button>
        </form>
      )}
    </article>
  );
}

function PainelSelecoesAbertas() {
  const { data: selecoesAbertas } = useSuspenseQuery(consultaSelecoesAbertas);
  return (
    <section className="panel">
      <h2>Seleções abertas no seu curso</h2>
      <p className="panel-sub">
        Confira os critérios e sua elegibilidade antes de enviar a candidatura.
      </p>
      {!selecoesAbertas.length && (
        <p className="empty">Nenhuma seleção aberta dentro do prazo para o seu curso.</p>
      )}
      {selecoesAbertas.map((item) => (
        <CartaoSelecaoAberta key={item.selecao.identificador} item={item} />
      ))}
    </section>
  );
}

export function SelecoesDeMonitoria() {
  const { autorizacoesDe } = useAutenticacao();
  const { publicar } = useNotificacoes();
  const [busca, setBusca] = useState('');
  const { data: candidaturas } = useSuspenseQuery(consultaCandidaturas(busca));
  const podeCandidatar = Boolean(
    (autorizacoesDe('candidatura') as AutorizacoesCandidatura | undefined)?.candidatar,
  );

  const cancelar = useMutation({
    mutationFn: (id: string) =>
      chamarApi(`/backend/monitoria/candidatura/${id}/cancelar`, { method: 'POST' }),
    onSuccess: () => {
      clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });
      publicar('sucesso', 'Candidatura cancelada.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · F006</span>
          <h1>Candidaturas à monitoria</h1>
          <p>
            Inscreva-se nas seleções abertas do seu curso e acompanhe o resultado de cada etapa.
          </p>
        </div>
      </div>

      <div className={podeCandidatar ? 'content-grid' : ''}>
        {podeCandidatar && <PainelSelecoesAbertas />}

        <section className="panel">
          <h2>Minhas candidaturas</h2>
          <p className="panel-sub">{candidaturas.resultado.total} candidatura(s).</p>

          <div className="toolbar">
            <div className="field">
              <label htmlFor="busca-cand">Filtrar</label>
              <input
                id="busca-cand"
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
                placeholder="Disciplina"
              />
            </div>
          </div>

          <div className="list-table-wrap">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Disciplina</th>
                  <th>CR</th>
                  <th>Entrevista</th>
                  <th>Final</th>
                  <th>Situação</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {candidaturas.resultado.projecoes.map((c) => {
                  const rotulo = rotuloSituacao(c.situacao);
                  return (
                    <tr key={c.identificador}>
                      <td>
                        <strong>{c.disciplinaNome}</strong>
                        <div className="subtext">Semestre {c.semestre}</div>
                        {c.motivoDecisao && <div className="subtext">{c.motivoDecisao}</div>}
                      </td>
                      <td>{c.cr.toFixed(2)}</td>
                      <td>{c.notaEntrevista !== null ? c.notaEntrevista.toFixed(2) : '—'}</td>
                      <td>{c.resultadoFinal !== null ? c.resultadoFinal.toFixed(2) : '—'}</td>
                      <td>
                        <span className={`status-badge ${rotulo.classe}`}>{rotulo.texto}</span>
                      </td>
                      <td>
                        {c.situacao === 'PENDENTE' && podeCandidatar && (
                          <button
                            type="button"
                            className="button button-danger button-small"
                            disabled={cancelar.isPending}
                            onClick={() => cancelar.mutate(c.identificador)}
                          >
                            Cancelar
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          {!candidaturas.resultado.projecoes.length && (
            <p className="empty">Nenhuma candidatura registrada até o momento.</p>
          )}
        </section>
      </div>
    </>
  );
}
