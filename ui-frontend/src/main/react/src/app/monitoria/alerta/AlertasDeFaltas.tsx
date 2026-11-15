import { useSuspenseQuery } from '@tanstack/react-query';
import { clienteConsultas } from '../../../d5';
import { data, useAcao } from '../../comunicacao/api';
import { consultaAlertas, consultaContexto } from '../api';
import { ContagemAlerta } from './ContagemAlerta';
export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaAlertas()),
    clienteConsultas.ensureQueryData(consultaContexto),
  ]);
  return null;
}
export function AlertasDeFaltas() {
  const { data: alertas } = useSuspenseQuery(consultaAlertas());
  const { data: contexto } = useSuspenseQuery(consultaContexto);
  const acao = useAcao();
  const lista = [...alertas.resultado.projecoes].sort(
    (a, b) => Number(a.acompanhado) - Number(b.acompanhado) || b.criadoEm.localeCompare(a.criadoEm),
  );
  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">COORDENAÇÃO</span>
          <h1>Alertas de faltas</h1>
          <p>
            Alertas gerados automaticamente quando um aluno ou monitor chega a 3 faltas na mesma
            disciplina.
          </p>
        </div>
      </div>
      <section className="panel">
        <h2>Alertas</h2>
        <div className="list-table-wrap">
          <table className="list-table">
            <thead>
              <tr>
                <th>Pessoa</th>
                <th>Disciplina</th>
                <th>Faltas</th>
                <th>Gerado em</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {lista.map((a) => (
                <tr key={a.identificador}>
                  <td>
                    <strong>
                      {contexto.pessoas.find((p) => p.identificador === a.pessoa)?.nome ?? a.pessoa}
                    </strong>
                    <div className="subtext">{a.papel.toLowerCase()}</div>
                  </td>
                  <td>{a.disciplina}</td>
                  <td>
                    <ContagemAlerta alerta={a} />
                  </td>
                  <td>{data(a.criadoEm)}</td>
                  <td>
                    <span className={`status-badge ${a.acompanhado ? 'aprovada' : 'pendente'}`}>
                      {a.acompanhado ? 'Acompanhado' : 'Novo'}
                    </span>
                  </td>
                  <td>
                    {!a.acompanhado && (
                      <button
                        className="button button-secondary button-small"
                        disabled={acao.isPending}
                        onClick={() =>
                          acao.mutate({ caminho: `monitoria/alerta/${a.identificador}/acompanhar` })
                        }
                      >
                        Marcar como acompanhado
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {!lista.length && <p className="empty">Nenhum alerta</p>}
      </section>
    </>
  );
}
