import { useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { Link, useSearchParams } from 'react-router';
import { clienteConsultas } from '../../../d5';
import {
  type AcompanhamentoProjecao,
  consultaAcompanhamentos,
  consultaRelatorio,
  type RelatorioDesempenho,
  type SessaoRelatorioItem,
} from '../api';

function formatarData(iso: string | null) {
  if (!iso) return '—';
  const partes = iso.slice(0, 10).split('-');
  if (partes.length !== 3) return iso;
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function rotuloSituacao(situacao: RelatorioDesempenho['situacao']) {
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

function rotuloTendencia(tendencia: RelatorioDesempenho['tendencia']) {
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

function rotuloSituacaoSessao(situacao: string) {
  switch (situacao) {
    case 'PRESENTE':
      return 'Presente';
    case 'REPOSTA':
      return 'Falta reposta';
    case 'FALTA_ALUNO':
      return 'Falta do aluno';
    case 'FALTA_MONITOR':
      return 'Falta do monitor';
    case 'CANCELADA':
      return 'Cancelada';
    default:
      return situacao;
  }
}

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaAcompanhamentos());
  return null;
}

function GraficoEvolucaoNotas({
  sessoes,
  metaNota,
  inclinacao,
}: {
  sessoes: SessaoRelatorioItem[];
  metaNota: number;
  inclinacao: number | null;
}) {
  const comNota = sessoes.filter((s) => s.nota !== null);
  const w = 440;
  const h = 190;
  const padL = 36;
  const padR = 16;
  const padT = 16;
  const padB = 30;
  const areaW = w - padL - padR;
  const areaH = h - padT - padB;

  const yPos = (valor: number) => padT + areaH - (Math.max(0, Math.min(10, valor)) / 10) * areaH;
  const xPos = (i: number) =>
    comNota.length <= 1 ? padL + areaW / 2 : padL + (i / (comNota.length - 1)) * areaW;

  const mediaX =
    comNota.length > 0 ? (comNota.length - 1) / 2 : 0;
  const mediaY =
    comNota.length > 0
      ? comNota.reduce((acc, s) => acc + Number(s.nota ?? 0), 0) / comNota.length
      : 0;
  const mostrarReta = comNota.length >= 3 && inclinacao !== null;
  const intercepto = mostrarReta ? mediaY - Number(inclinacao) * mediaX : 0;
  const regY0 = yPos(intercepto);
  const regY1 = yPos(intercepto + Number(inclinacao ?? 0) * Math.max(1, comNota.length - 1));

  return (
    <div className="chart-box">
      <h3>Evolução das notas e regressão linear</h3>
      <div className="subtext">
        Notas registradas por sessão comparadas à meta de {metaNota.toFixed(1)}.
      </div>
      {comNota.length === 0 ? (
        <p className="empty" style={{ marginTop: 12 }}>
          Sem notas registradas no período.
        </p>
      ) : (
        <svg viewBox={`0 0 ${w} ${h}`} role="img" aria-label="Gráfico de evolução das notas">
          {[0, 2.5, 5, 7.5, 10].map((tick) => {
            const y = yPos(tick);
            return (
              <g key={tick}>
                <line x1={padL} y1={y} x2={w - padR} y2={y} stroke="#eaf0f4" strokeWidth={1} />
                <text x={padL - 6} y={y + 3} fontSize="9.5" textAnchor="end" fill="#708295">
                  {tick}
                </text>
              </g>
            );
          })}
          <line
            x1={padL}
            y1={yPos(metaNota)}
            x2={w - padR}
            y2={yPos(metaNota)}
            stroke="#c05621"
            strokeDasharray="4 4"
            strokeWidth={1.5}
          />
          {mostrarReta && (
            <line
              x1={xPos(0)}
              y1={regY0}
              x2={xPos(comNota.length - 1)}
              y2={regY1}
              stroke="#132238"
              strokeDasharray="6 3"
              strokeWidth={1.5}
            />
          )}
          <polyline
            fill="none"
            stroke="#0b978a"
            strokeWidth={2.5}
            points={comNota.map((s, i) => `${xPos(i)},${yPos(Number(s.nota))}`).join(' ')}
          />
          {comNota.map((s, i) => (
            <g key={s.sessao}>
              <circle cx={xPos(i)} cy={yPos(Number(s.nota))} r={4} fill="#0b978a" />
              <text
                x={xPos(i)}
                y={h - 8}
                fontSize="9.5"
                textAnchor="middle"
                fill="#708295"
              >
                {formatarData(s.data).slice(0, 5)}
              </text>
            </g>
          ))}
        </svg>
      )}
      <div className="chart-legend">
        <span>
          <i className="chart-dot" style={{ background: '#0b978a' }} />
          Nota da sessão
        </span>
        <span>
          <i className="chart-dot" style={{ background: '#132238' }} />
          Tendência OLS {inclinacao !== null ? `(${inclinacao.toFixed(2)} pt/sessão)` : ''}
        </span>
        <span>
          <i className="chart-dot" style={{ background: '#c05621' }} />
          Meta ({metaNota.toFixed(1)})
        </span>
      </div>
    </div>
  );
}

function GraficoComparativoNotas({
  notaInicial,
  notaAtual,
  metaNota,
}: {
  notaInicial: number | null;
  notaAtual: number | null;
  metaNota: number;
}) {
  const w = 440;
  const h = 150;
  const padL = 95;
  const padR = 36;
  const maxW = w - padL - padR;
  const xScale = (v: number) => padL + (Math.max(0, Math.min(10, v)) / 10) * maxW;

  const barras = [
    { rotulo: 'Nota inicial', valor: notaInicial, cor: '#5c7287' },
    { rotulo: 'Nota atual', valor: notaAtual, cor: '#0b978a' },
    { rotulo: 'Meta', valor: metaNota, cor: '#c05621' },
  ];

  return (
    <div className="chart-box">
      <h3>Nota inicial vs. Nota atual</h3>
      <div className="subtext">
        Média das 2 primeiras notas comparada às 2 últimas (mínimo de 4 notas no período).
      </div>
      <svg viewBox={`0 0 ${w} ${h}`} role="img" aria-label="Comparativo nota inicial e atual">
        {barras.map((b, idx) => {
          const y = 20 + idx * 40;
          const largura = b.valor !== null ? (Math.max(0, Math.min(10, b.valor)) / 10) * maxW : 0;
          return (
            <g key={b.rotulo}>
              <text x={padL - 8} y={y + 15} fontSize="11" textAnchor="end" fill="#394e63">
                {b.rotulo}
              </text>
              <rect x={padL} y={y} width={maxW} height={22} rx={6} fill="#edf3f6" />
              {b.valor !== null && (
                <rect x={padL} y={y} width={largura} height={22} rx={6} fill={b.cor} />
              )}
              <text x={w - padR + 6} y={y + 15} fontSize="11" fontWeight="700" fill="#132238">
                {b.valor !== null ? b.valor.toFixed(2) : 'N/C'}
              </text>
            </g>
          );
        })}
        <line
          x1={xScale(metaNota)}
          y1={10}
          x2={xScale(metaNota)}
          y2={135}
          stroke="#c05621"
          strokeDasharray="4 4"
          strokeWidth={1.5}
        />
      </svg>
    </div>
  );
}

function GraficoFrequenciaAcumulada({
  sessoes,
  metaFrequencia,
}: {
  sessoes: SessaoRelatorioItem[];
  metaFrequencia: number;
}) {
  const validas = sessoes.filter((s) => s.valida);
  let presencas = 0;
  const pontos = validas.map((s, idx) => {
    if (s.presente) presencas += 1;
    return {
      data: s.data,
      pct: (presencas / (idx + 1)) * 100,
    };
  });

  const w = 440;
  const h = 175;
  const padL = 38;
  const padR = 16;
  const padT = 16;
  const padB = 28;
  const areaW = w - padL - padR;
  const areaH = h - padT - padB;
  const yPos = (pct: number) => padT + areaH - (Math.max(0, Math.min(100, pct)) / 100) * areaH;
  const xPos = (i: number) =>
    pontos.length <= 1 ? padL + areaW / 2 : padL + (i / (pontos.length - 1)) * areaW;

  return (
    <div className="chart-box">
      <h3>Frequência acumulada (%)</h3>
      <div className="subtext">
        Sessões válidas considerando reposições realizadas vs. meta de {metaFrequencia}%.
      </div>
      {!pontos.length ? (
        <p className="empty" style={{ marginTop: 12 }}>
          Sem sessões válidas registradas no período.
        </p>
      ) : (
        <svg viewBox={`0 0 ${w} ${h}`} role="img" aria-label="Gráfico de frequência acumulada">
          {[0, 25, 50, 75, 100].map((t) => (
            <g key={t}>
              <line
                x1={padL}
                y1={yPos(t)}
                x2={w - padR}
                y2={yPos(t)}
                stroke="#eaf0f4"
                strokeWidth={1}
              />
              <text x={padL - 6} y={yPos(t) + 3} fontSize="9.5" textAnchor="end" fill="#708295">
                {t}%
              </text>
            </g>
          ))}
          <line
            x1={padL}
            y1={yPos(metaFrequencia)}
            x2={w - padR}
            y2={yPos(metaFrequencia)}
            stroke="#c05621"
            strokeDasharray="4 4"
            strokeWidth={1.5}
          />
          <polyline
            fill="none"
            stroke="#0b978a"
            strokeWidth={2.5}
            points={pontos.map((p, i) => `${xPos(i)},${yPos(p.pct)}`).join(' ')}
          />
          {pontos.map((p, i) => (
            <circle key={`${p.data}-${i}`} cx={xPos(i)} cy={yPos(p.pct)} r={3.5} fill="#0b978a" />
          ))}
        </svg>
      )}
    </div>
  );
}

function PainelRelatorio({
  acomp,
  de,
  ate,
}: {
  acomp: AcompanhamentoProjecao;
  de: string;
  ate: string;
}) {
  const { data: relatorio } = useSuspenseQuery(consultaRelatorio(acomp.identificador, de, ate));
  const sit = rotuloSituacao(relatorio.situacao);
  const freq = relatorio.frequenciaPercentual;

  return (
    <>
      {relatorio.filtroIgnorado && (
        <div className="notice">
          <div>
            A data inicial informada é posterior à data final; o filtro de período foi ignorado e
            todo o histórico aplicável foi exibido.
          </div>
        </div>
      )}

      {acomp.situacao === 'ENCERRADO' && (
        <div className="notice green">
          <div>
            <strong>Acompanhamento encerrado em {formatarData(acomp.encerradoEm)}</strong>
            {acomp.parecerFinal ? ` — Parecer final: ${acomp.parecerFinal}` : ''}
          </div>
        </div>
      )}

      <div className="kpi-grid">
        <div className="kpi-card">
          <small>Situação atual</small>
          <b>
            <span className={`status-badge ${sit.classe}`} style={{ fontSize: 13, padding: '6px 12px' }}>
              {sit.texto}
            </span>
          </b>
          <span>
            {acomp.alunoNome} · {acomp.disciplinaNome} ({acomp.semestre})
          </span>
        </div>

        <div className="kpi-card">
          <small>Frequência</small>
          <b>{freq !== null ? `${freq.toFixed(1)}%` : '—'}</b>
          <span>
            {relatorio.totalPresencas}/{relatorio.totalSessoesValidas} sessões válidas (meta{' '}
            {acomp.metaFrequencia}%)
          </span>
          <div className="progress-track">
            <div
              className={`progress-fill ${freq !== null && freq < acomp.metaFrequencia ? 'alerta' : ''}`}
              style={{ width: `${Math.min(100, Math.max(0, freq ?? 0))}%` }}
            />
            <span className="progress-meta" style={{ left: `${acomp.metaFrequencia}%` }} />
          </div>
        </div>

        <div className="kpi-card">
          <small>Nota inicial / atual</small>
          <b>
            {relatorio.notaInicial !== null ? relatorio.notaInicial.toFixed(2) : '—'} →{' '}
            {relatorio.notaAtual !== null ? relatorio.notaAtual.toFixed(2) : '—'}
          </b>
          <span>
            {relatorio.quantidadeNotas < 4
              ? `Mínimo 4 notas para comparar (${relatorio.quantidadeNotas} registrada(s))`
              : `Meta de nota: ${acomp.metaNota.toFixed(1)}`}
          </span>
          <div className="progress-track">
            <div
              className={`progress-fill ${
                relatorio.notaAtual !== null && relatorio.notaAtual < acomp.metaNota ? 'alerta' : ''
              }`}
              style={{
                width: `${Math.min(100, Math.max(0, ((relatorio.notaAtual ?? 0) / 10) * 100))}%`,
              }}
            />
            <span
              className="progress-meta"
              style={{ left: `${(acomp.metaNota / 10) * 100}%` }}
            />
          </div>
        </div>

        <div className="kpi-card">
          <small>Variação e tendência</small>
          <b>
            {relatorio.variacaoNotas !== null
              ? `${relatorio.variacaoNotas >= 0 ? '+' : ''}${relatorio.variacaoNotas.toFixed(2)}`
              : '—'}
          </b>
          <span>
            {rotuloTendencia(relatorio.tendencia)}
            {relatorio.inclinacaoTendencia !== null
              ? ` (${relatorio.inclinacaoTendencia.toFixed(2)} pts/sessão)`
              : ''}
          </span>
        </div>
      </div>

      <div className="chart-grid">
        <GraficoEvolucaoNotas
          sessoes={relatorio.sessoes}
          metaNota={acomp.metaNota}
          inclinacao={relatorio.inclinacaoTendencia}
        />
        <GraficoComparativoNotas
          notaInicial={relatorio.notaInicial}
          notaAtual={relatorio.notaAtual}
          metaNota={acomp.metaNota}
        />
        <GraficoFrequenciaAcumulada
          sessoes={relatorio.sessoes}
          metaFrequencia={acomp.metaFrequencia}
        />
      </div>

      <section className="panel">
        <h2>Sessões consideradas no relatório</h2>
        <p className="panel-sub">
          {relatorio.sessoes.length} sessão(ões) no período analisado · Média geral:{' '}
          {relatorio.mediaNotas !== null ? relatorio.mediaNotas.toFixed(2) : '—'}.
        </p>

        <div className="list-table-wrap">
          <table className="list-table">
            <thead>
              <tr>
                <th>Data</th>
                <th>Situação</th>
                <th>Conta na freq.</th>
                <th>Presença</th>
                <th>Nota</th>
                <th>Comentário</th>
              </tr>
            </thead>
            <tbody>
              {relatorio.sessoes.map((s) => (
                <tr key={s.sessao}>
                  <td>{formatarData(s.data)}</td>
                  <td>{rotuloSituacaoSessao(s.situacao)}</td>
                  <td>{s.valida ? 'Sim' : 'Ignorada'}</td>
                  <td>{s.valida ? (s.presente ? 'Presente' : 'Falta') : '—'}</td>
                  <td>{s.nota !== null ? s.nota.toFixed(1) : '—'}</td>
                  <td className="preservar-texto">{s.comentario ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {!relatorio.sessoes.length && (
          <p className="empty">Nenhuma sessão realizada encontrada no período selecionado.</p>
        )}
      </section>
    </>
  );
}

export function RelatoriosDeDesempenho() {
  const [searchParams, setSearchParams] = useSearchParams();
  const { data } = useSuspenseQuery(consultaAcompanhamentos());
  const lista = data.resultado.projecoes;
  const idAtual = searchParams.get('id') ?? lista[0]?.identificador ?? '';
  const acompAtual = lista.find((a) => a.identificador === idAtual) ?? lista[0];
  const [de, setDe] = useState('');
  const [ate, setAte] = useState('');

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Monitoria · F013</span>
          <h1>Relatório de desempenho</h1>
          <p>
            Indicadores de frequência, comparação inicial vs. atual, tendência por regressão linear
            e histórico detalhado das sessões.
          </p>
        </div>
        <div className="row-actions no-print">
          <Link className="button button-ghost" to="/monitoria/acompanhamento/pesquisa">
            Acompanhamentos
          </Link>
          <button
            type="button"
            className="button button-primary"
            disabled={!acompAtual}
            onClick={() => window.print()}
          >
            Exportar PDF
          </button>
        </div>
      </div>

      {!acompAtual ? (
        <section className="panel">
          <p className="empty">Nenhum acompanhamento disponível para emissão de relatório.</p>
        </section>
      ) : (
        <>
          <section className="panel no-print" style={{ marginBottom: 18 }}>
            <div className="toolbar" style={{ marginBottom: 0 }}>
              <div className="field">
                <label htmlFor="rel-acomp">Acompanhamento</label>
                <select
                  id="rel-acomp"
                  value={acompAtual.identificador}
                  onChange={(e) => setSearchParams({ id: e.target.value })}
                >
                  {lista.map((a) => (
                    <option key={a.identificador} value={a.identificador}>
                      {a.alunoNome} · {a.disciplinaNome} ({a.semestre})
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label htmlFor="rel-de">De</label>
                <input
                  id="rel-de"
                  type="date"
                  value={de}
                  onChange={(e) => setDe(e.target.value)}
                />
              </div>
              <div className="field">
                <label htmlFor="rel-ate">Até</label>
                <input
                  id="rel-ate"
                  type="date"
                  value={ate}
                  onChange={(e) => setAte(e.target.value)}
                />
              </div>
              <button
                type="button"
                className="button button-ghost"
                onClick={() => {
                  setDe('');
                  setAte('');
                }}
              >
                Limpar período
              </button>
            </div>
          </section>

          <PainelRelatorio acomp={acompAtual} de={de} ate={ate} />
        </>
      )}
    </>
  );
}
