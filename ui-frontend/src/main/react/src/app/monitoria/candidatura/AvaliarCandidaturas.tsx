import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { Link, useSearchParams } from 'react-router';
import { chamarApi, clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';
import {
  type CandidaturaProjecao,
  consultaCandidaturas,
  consultaSelecoes,
  type SelecaoProjecao,
} from '../api';

function formatarData(iso: string) {
  const partes = iso.slice(0, 10).split('-');
  if (partes.length !== 3) return iso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaSelecoes()),
    clienteConsultas.ensureQueryData(consultaCandidaturas()),
  ]);
  return null;
}

function LinhaCandidato({
  candidatura,
  selecao,
  posicao,
}: {
  candidatura: CandidaturaProjecao;
  selecao: SelecaoProjecao | undefined;
  posicao: number;
}) {
  const { publicar } = useNotificacoes();
  const [nota, setNota] = useState(
    candidatura.notaEntrevista !== null ? String(candidatura.notaEntrevista) : '8.0',
  );
  const [comentario, setComentario] = useState(candidatura.comentarioEntrevista ?? '');

  const atualizar = () =>
    clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades', 'monitoria'] });

  const salvarEntrevista = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/candidatura/${candidatura.identificador}/entrevista`, {
        method: 'POST',
        corpo: { nota: Number(nota), comentario: comentario.trim() || null },
      }),
    onSuccess: () => {
      atualizar();
      publicar('sucesso', `Entrevista de ${candidatura.alunoNome} registrada.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const aprovar = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/candidatura/${candidatura.identificador}/aprovar`, {
        method: 'POST',
      }),
    onSuccess: () => {
      atualizar();
      publicar('sucesso', `${candidatura.alunoNome} aprovado(a) como monitor(a).`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const reprovar = useMutation({
    mutationFn: () =>
      chamarApi(`/backend/monitoria/candidatura/${candidatura.identificador}/reprovar`, {
        method: 'POST',
      }),
    onSuccess: () => {
      atualizar();
      publicar('sucesso', `Candidatura de ${candidatura.alunoNome} reprovada.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const pendente = candidatura.situacao === 'PENDENTE' && selecao?.situacao !== 'ENCERRADA';
  const previaFinal =
    selecao && !Number.isNaN(Number(nota))
      ? ((selecao.pesoCr * candidatura.cr + selecao.pesoEntrevista * Number(nota)) / 100).toFixed(2)
      : '—';

  return (
    <tr>
      <td>
        <strong>{candidatura.situacao === 'CANCELADA' ? '—' : `${posicao}º`}</strong>
      </td>
      <td>
        <strong>{candidatura.alunoNome}</strong>
        <div className="subtext">{candidatura.disciplinaNome}</div>
        {candidatura.comentarioEntrevista && (
          <div className="subtext">Obs.: {candidatura.comentarioEntrevista}</div>
        )}
        {candidatura.motivoDecisao && (
          <div className="subtext">{candidatura.motivoDecisao}</div>
        )}
      </td>
      <td>{candidatura.cr.toFixed(2)}</td>
      <td>
        {pendente ? (
          <div style={{ display: 'grid', gap: 6, minWidth: 210 }}>
            <input
              aria-label={`Nota entrevista ${candidatura.alunoNome}`}
              type="number"
              min={0}
              max={10}
              step="0.1"
              value={nota}
              onChange={(e) => setNota(e.target.value)}
            />
            <input
              aria-label={`Comentário entrevista ${candidatura.alunoNome}`}
              placeholder="Comentário da entrevista (opcional)"
              value={comentario}
              onChange={(e) => setComentario(e.target.value)}
            />
            <button
              type="button"
              className="button button-secondary button-small"
              disabled={salvarEntrevista.isPending}
              onClick={() => salvarEntrevista.mutate()}
            >
              Salvar entrevista (prévia {previaFinal})
            </button>
          </div>
        ) : candidatura.notaEntrevista !== null ? (
          candidatura.notaEntrevista.toFixed(2)
        ) : (
          '—'
        )}
      </td>
      <td>
        <strong>
          {candidatura.resultadoFinal !== null ? candidatura.resultadoFinal.toFixed(2) : '—'}
        </strong>
      </td>
      <td>
        <span
          className={`status-badge ${
            candidatura.situacao === 'APROVADA'
              ? 'aprovada'
              : candidatura.situacao === 'REPROVADA'
                ? 'reprovada'
                : 'pendente'
          }`}
        >
          {candidatura.situacao}
        </span>
      </td>
      <td>
        {pendente && (
          <div className="row-actions">
            <button
              type="button"
              className="button button-primary button-small"
              disabled={aprovar.isPending || candidatura.notaEntrevista === null}
              onClick={() => aprovar.mutate()}
            >
              Aprovar
            </button>
            <button
              type="button"
              className="button button-danger button-small"
              disabled={reprovar.isPending || candidatura.notaEntrevista === null}
              onClick={() => reprovar.mutate()}
            >
              Reprovar
            </button>
          </div>
        )}
      </td>
    </tr>
  );
}

export function AvaliarCandidaturas() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [busca, setBusca] = useState('');
  const { data: selecoesData } = useSuspenseQuery(consultaSelecoes());
  const selecoes = selecoesData.resultado.projecoes;
  const selecaoAtualId = searchParams.get('selecao') ?? selecoes[0]?.identificador ?? '';
  const { data: candData } = useSuspenseQuery(consultaCandidaturas(busca, selecaoAtualId));

  const selecaoAtual = selecoes.find((s) => s.identificador === selecaoAtualId);
  const ordenadas = [...candData.resultado.projecoes].sort((a, b) => {
    if (a.situacao === 'CANCELADA' && b.situacao !== 'CANCELADA') return 1;
    if (b.situacao === 'CANCELADA' && a.situacao !== 'CANCELADA') return -1;
    const resDiff = (b.resultadoFinal ?? -1) - (a.resultadoFinal ?? -1);
    if (Math.abs(resDiff) > 1e-9) return resDiff;
    const crDiff = b.cr - a.cr;
    if (Math.abs(crDiff) > 1e-9) return crDiff;
    return a.criadaEm.localeCompare(b.criadaEm);
  });

  let rank = 0;

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · Professor responsável</span>
          <h1>Avaliação e classificação de candidatos</h1>
          <p>
            Registre notas de entrevista e aprove ou reprove candidatos seguindo a classificação e
            o limite de vagas após o prazo de inscrição.
          </p>
        </div>
        <Link className="button button-ghost" to="/monitoria/selecao/pesquisa">
          Voltar para seleções
        </Link>
      </div>

      <section className="panel">
        <div className="toolbar">
          <div className="field">
            <label htmlFor="select-selecao">Seleção</label>
            <select
              id="select-selecao"
              value={selecaoAtualId}
              onChange={(e) => {
                const next = new URLSearchParams(searchParams);
                if (e.target.value) next.set('selecao', e.target.value);
                else next.delete('selecao');
                setSearchParams(next);
              }}
            >
              <option value="">Todas</option>
              {selecoes.map((s) => (
                <option key={s.identificador} value={s.identificador}>
                  {s.disciplinaCodigo} · {s.disciplinaNome} ({s.semestre})
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label htmlFor="busca-candidato">Buscar candidato</label>
            <input
              id="busca-candidato"
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              placeholder="Nome do aluno ou disciplina"
            />
          </div>
        </div>

        {selecaoAtual && (
          <div className={`notice ${selecaoAtual.situacao === 'ABERTA' ? 'green' : ''}`}>
            <div>
              <strong>
                {selecaoAtual.disciplinaNome} ({selecaoAtual.semestre})
              </strong>{' '}
              · Vagas preenchidas: {selecaoAtual.aprovadas}/{selecaoAtual.vagas} · Prazo de
              inscrição: {formatarData(selecaoAtual.prazoInscricao)} · Pesos:{' '}
              {selecaoAtual.pesoCr}% CR / {selecaoAtual.pesoEntrevista}% Entrevista · Patamar
              mínimo: {selecaoAtual.patamarMinimo.toFixed(1)}.
            </div>
          </div>
        )}

        <div className="list-table-wrap">
          <table className="list-table">
            <thead>
              <tr>
                <th>Pos.</th>
                <th>Candidato</th>
                <th>CR</th>
                <th>Entrevista</th>
                <th>Resultado</th>
                <th>Situação</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {ordenadas.map((c) => {
                if (c.situacao !== 'CANCELADA') rank += 1;
                return (
                  <LinhaCandidato
                    key={c.identificador}
                    candidatura={c}
                    selecao={selecoes.find((s) => s.identificador === c.selecao) ?? selecaoAtual}
                    posicao={rank}
                  />
                );
              })}
            </tbody>
          </table>
        </div>

        {!ordenadas.length && (
          <p className="empty">Nenhuma candidatura encontrada para a seleção escolhida.</p>
        )}
      </section>
    </>
  );
}
