import { useState } from 'react';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { clienteConsultas } from '../../../d5';
import { data, useAcao } from '../../comunicacao/api';
import {
  consultaContexto,
  consultaFaltas,
  consultaSessoes,
  consultaSessao,
  consultaSugestoes,
  type Falta,
} from '../api';

export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaFaltas()),
    clienteConsultas.ensureQueryData(consultaSessoes()),
    clienteConsultas.ensureQueryData(consultaContexto),
  ]);
  return null;
}
export function FaltasEReposicao() {
  const { data: contexto } = useSuspenseQuery(consultaContexto);
  const { data: faltas } = useSuspenseQuery(consultaFaltas());
  const { data: sessoes } = useSuspenseQuery(consultaSessoes());
  const [sessaoId, definirSessao] = useState('');
  const [edicao, definirEdicao] = useState<Falta | null>(null);
  const [sugestaoId, definirSugestao] = useState('');
  const realizadas = sessoes.resultado.projecoes.filter(
    (s) =>
      !s.cancelada &&
      new Date(s.fim) <= new Date() &&
      (s.monitor === contexto.usuario || s.professor === contexto.usuario),
  );
  const selecionada = sessaoId || realizadas[0]?.identificador || '';
  const { data: detalhe, isError: erroSessao } = useQuery(consultaSessao(selecionada));
  const {
    data: sugestoes,
    isFetching: buscando,
    error: erroSugestao,
  } = useQuery(consultaSugestoes(sugestaoId));
  const formulario = useForm<{ pessoa: string; observacao: string }>({
    defaultValues: { pessoa: '', observacao: '' },
  });
  const acao = useAcao(() => {
    definirEdicao(null);
    definirSugestao('');
    formulario.reset({ pessoa: '', observacao: '' });
  });
  const nome = (id: string) => contexto.pessoas.find((p) => p.identificador === id)?.nome ?? id;
  const obterSessao = (id: string) =>
    sessoes.resultado.projecoes.find((s) => s.identificador === id);
  const lista = faltas.resultado.projecoes.filter((f) => {
    const s = obterSessao(f.sessao);
    return s && (s.monitor === contexto.usuario || s.professor === contexto.usuario);
  });
  const totais = new Map<
    string,
    { pessoa: string; papel: string; disciplina: string; total: number }
  >();
  lista.forEach((f) => {
    const disciplina = obterSessao(f.sessao)?.disciplina ?? '';
    const chave = `${f.pessoa}|${f.papel}|${disciplina}`;
    const atual = totais.get(chave);
    totais.set(chave, {
      pessoa: f.pessoa,
      papel: f.papel,
      disciplina,
      total: (atual?.total ?? 0) + 1,
    });
  });
  const executar = (caminho: string, corpo?: unknown) =>
    acao.mutate({ caminho: `monitoria/${caminho}`, corpo });
  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">MONITORIA</span>
          <h1>Faltas e reposição</h1>
          <p>
            Registre, edite e exclua faltas de alunos e do monitor. Para faltas de aluno, o sistema
            sugere horários de reposição; ao chegar a 3 faltas na disciplina, o coordenador recebe
            um alerta.
          </p>
        </div>
      </div>
      <div className="content-grid">
        <section className="panel">
          <h2>{edicao ? 'Editar falta' : 'Registrar falta'}</h2>
          <p className="panel-sub">
            {edicao
              ? `${nome(edicao.pessoa)} · ${edicao.papel.toLowerCase()}`
              : 'Informe quem faltou na sessão.'}
          </p>
          {realizadas.length || edicao ? (
            <form
              className="form-grid"
              onSubmit={formulario.handleSubmit((v) => {
                if (edicao)
                  executar(`falta/${edicao.identificador}/editar`, { observacao: v.observacao });
                else {
                  const [papel, pessoa] = v.pessoa.split('|');
                  executar('falta/registrar', {
                    sessao: selecionada,
                    pessoa,
                    papel,
                    observacao: v.observacao,
                  });
                }
              })}
            >
              {!edicao && (
                <>
                  <div className="field full">
                    <label htmlFor="falta-sessao">Sessão realizada *</label>
                    <select
                      id="falta-sessao"
                      value={selecionada}
                      onChange={(e) => {
                        definirSessao(e.target.value);
                        formulario.setValue('pessoa', '');
                      }}
                    >
                      {realizadas.map((s) => (
                        <option key={s.identificador} value={s.identificador}>
                          {data(s.inicio)} · {s.disciplina} · {s.inicio.slice(11, 16)}
                        </option>
                      ))}
                    </select>
                    <small>
                      Só sessões já realizadas de que você é monitor ou professor da disciplina.
                    </small>
                  </div>
                  <div className="field full">
                    <label htmlFor="falta-pessoa">Quem faltou? *</label>
                    <select
                      id="falta-pessoa"
                      required
                      {...formulario.register('pessoa', { required: !edicao })}
                    >
                      <option value="">Selecione</option>
                      {detalhe && (
                        <>
                          <option value={`MONITOR|${detalhe.agregado.monitor}`}>
                            Monitor: {nome(detalhe.agregado.monitor)}
                          </option>
                          {detalhe.agregado.alunos.map((id) => (
                            <option value={`ALUNO|${id}`} key={id}>
                              Aluno: {nome(id)}
                            </option>
                          ))}
                        </>
                      )}
                    </select>
                    {erroSessao && (
                      <small role="alert">Não foi possível carregar os participantes.</small>
                    )}
                  </div>
                </>
              )}
              <div className="field full">
                <label htmlFor="falta-observacao">Observação</label>
                <textarea
                  id="falta-observacao"
                  placeholder="Opcional"
                  maxLength={1000}
                  {...formulario.register('observacao', { maxLength: 1000 })}
                />
              </div>
              <div className="form-actions full">
                <button className="button button-primary" disabled={acao.isPending}>
                  {edicao ? 'Salvar' : 'Registrar falta'}
                </button>
                {edicao && (
                  <button
                    className="button"
                    type="button"
                    onClick={() => {
                      definirEdicao(null);
                      formulario.reset({ pessoa: '', observacao: '' });
                    }}
                  >
                    Cancelar
                  </button>
                )}
              </div>
            </form>
          ) : (
            <p className="empty">Nenhuma sessão realizada</p>
          )}
        </section>
        <section className="panel">
          <h2>Total de faltas por pessoa</h2>
          <p className="panel-sub">Limite de tolerância: 3 faltas por disciplina.</p>
          <div className="list-table-wrap">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Pessoa</th>
                  <th>Papel</th>
                  <th>Disciplina</th>
                  <th>Faltas</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {[...totais.entries()].map(([chave, t]) => (
                  <tr key={chave}>
                    <td>{nome(t.pessoa)}</td>
                    <td>{t.papel.toLowerCase()}</td>
                    <td>{t.disciplina}</td>
                    <td>
                      <b>{t.total}</b>
                    </td>
                    <td>
                      <span className={`status-badge ${t.total >= 3 ? 'reprovada' : 'encerrada'}`}>
                        {t.total >= 3 ? 'Alerta enviado' : `${3 - t.total} até o alerta`}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {!totais.size && <p className="empty">Nenhuma falta</p>}
        </section>
      </div>
      <section className="panel">
        <h2>Faltas registradas</h2>
        <p className="panel-sub">
          Quem registrou pode editar ou excluir; o professor da disciplina também.
        </p>
        <div className="list-table-wrap">
          <table className="list-table">
            <thead>
              <tr>
                <th>Sessão</th>
                <th>Quem faltou</th>
                <th>Registrada por</th>
                <th>Observação</th>
                <th>Reposição</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {lista.map((f) => {
                const s = obterSessao(f.sessao);
                const reposicao = sessoes.resultado.projecoes.find(
                  (s) => !s.cancelada && s.identificador === f.reposicao,
                );
                const gerenciar = f.autor === contexto.usuario || s?.professor === contexto.usuario;
                return (
                  <tr key={f.identificador}>
                    <td>
                      <strong>{data(s?.inicio ?? f.registradaEm)}</strong>
                      <div className="subtext">{s?.disciplina}</div>
                    </td>
                    <td>
                      {nome(f.pessoa)}
                      <div className="subtext">{f.papel.toLowerCase()}</div>
                    </td>
                    <td>{nome(f.autor)}</td>
                    <td className="preservar-texto">{f.observacao || '—'}</td>
                    <td>
                      {f.papel === 'ALUNO' ? (
                        reposicao ? (
                          <span className="tiny">
                            Reposição em {data(reposicao.inicio)} {reposicao.inicio.slice(11, 16)}
                          </span>
                        ) : (
                          <button
                            className="button button-secondary button-small"
                            onClick={() => definirSugestao(f.identificador)}
                          >
                            Sugerir reposição
                          </button>
                        )
                      ) : (
                        '—'
                      )}
                    </td>
                    <td>
                      {gerenciar && (
                        <div className="row-actions">
                          <button
                            className="button button-small"
                            aria-label={`Editar falta de ${nome(f.pessoa)}`}
                            onClick={() => {
                              definirEdicao(f);
                              formulario.reset({ pessoa: '', observacao: f.observacao ?? '' });
                            }}
                          >
                            ✎
                          </button>
                          <button
                            className="button button-danger button-small"
                            disabled={acao.isPending || !!reposicao}
                            onClick={() => executar(`falta/${f.identificador}/excluir`)}
                          >
                            Excluir
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        {!lista.length && <p className="empty">Nenhuma falta registrada</p>}
        {sugestaoId && (
          <div className="absence-box">
            <b>Sugestões de reposição</b>
            <p className="tiny muted">
              Pela disponibilidade do monitor, sem conflito com a agenda do monitor e do aluno.
            </p>
            {buscando ? (
              <p>Buscando horários…</p>
            ) : erroSugestao ? (
              <p role="alert">Não foi possível consultar os horários.</p>
            ) : sugestoes?.length ? (
              <div className="chips">
                {sugestoes.map((g) => (
                  <button
                    className="button button-ghost button-small"
                    disabled={acao.isPending}
                    key={`${g.inicio}-${g.campus}`}
                    onClick={() => executar(`falta/${sugestaoId}/agendar`, g)}
                  >
                    {data(g.inicio)} · {g.inicio.slice(11, 16)}–{g.fim.slice(11, 16)} → Agendar
                  </button>
                ))}
              </div>
            ) : (
              <p>Nenhum horário compatível nos próximos 14 dias.</p>
            )}
            <button className="button button-small" onClick={() => definirSugestao('')}>
              Fechar
            </button>
          </div>
        )}
      </section>
    </>
  );
}
