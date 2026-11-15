import { useState } from 'react';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { clienteConsultas, useAutenticacao } from '../../../d5';
import {
  consultaFaq,
  consultaPerguntas,
  data,
  useAcao,
  type Faq as Entrada,
  type Pergunta,
} from '../api';

export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaFaq()),
    clienteConsultas.ensureQueryData(consultaPerguntas()),
  ]);
  return null;
}

export function Faq() {
  const { autorizacoesDe } = useAutenticacao();
  const gerenciar = autorizacoesDe('faq')?.incluir ?? false;
  const [busca, definirBusca] = useState('');
  const [status, definirStatus] = useState('');
  const [edicao, definirEdicao] = useState<Entrada | null>(null);
  const [origem, definirOrigem] = useState<Pergunta | null>(null);
  const [minhaEdicao, definirMinhaEdicao] = useState<Pergunta | null>(null);
  const { data: entradas } = useSuspenseQuery(consultaFaq(busca));
  const { data: perguntas } = useSuspenseQuery(consultaPerguntas());
  const cadastro = useForm<{ pergunta: string; resposta: string; ativo: string }>({
    defaultValues: { pergunta: '', resposta: '', ativo: 'true' },
  });
  const duvida = useForm<{ texto: string }>({ defaultValues: { texto: '' } });
  function cancelar() {
    definirEdicao(null);
    definirOrigem(null);
    definirMinhaEdicao(null);
    cadastro.reset({ pergunta: '', resposta: '', ativo: 'true' });
    duvida.reset({ texto: '' });
  }
  const acao = useAcao(cancelar);
  const executar = (caminho: string, corpo?: unknown) =>
    acao.mutate({ caminho: `comunicacao/${caminho}`, corpo });
  const lista = entradas.resultado.projecoes.filter(
    (f) => status === '' || String(f.ativo) === status,
  );
  const pendentes = perguntas.resultado.projecoes.filter((p) => p.situacao === 'PENDENTE');
  const editar = (f: Entrada) => {
    definirEdicao(f);
    definirOrigem(null);
    cadastro.reset({ ...f, ativo: String(f.ativo) });
  };
  const responder = (p: Pergunta) => {
    definirOrigem(p);
    definirEdicao(null);
    cadastro.reset({ pergunta: p.texto.slice(0, 200), resposta: '', ativo: 'true' });
  };
  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">COMUNICAÇÃO</span>
          <h1>{gerenciar ? 'Gerenciar FAQ' : 'Ajuda (FAQ)'}</h1>
          <p>
            {gerenciar
              ? 'Crie, edite, inative e exclua perguntas frequentes e responda as dúvidas enviadas pelos alunos.'
              : 'Perguntas frequentes sobre a monitoria. Não achou sua dúvida? Envie uma pergunta para a coordenação.'}
          </p>
        </div>
      </div>
      <div className="content-grid">
        {gerenciar ? (
          <div className="column-stack">
            <section className="panel">
              <h2>
                {edicao
                  ? 'Editar pergunta'
                  : origem
                    ? 'Responder pergunta de aluno'
                    : 'Nova pergunta'}
              </h2>
              <p className="panel-sub">
                {origem
                  ? 'Ao salvar, a resposta é publicada no FAQ e enviada ao autor.'
                  : 'A pergunta não pode se repetir.'}
              </p>
              <form
                className="form-grid"
                onSubmit={cadastro.handleSubmit((v) => {
                  const corpo = { ...v, ativo: v.ativo === 'true' };
                  executar(
                    origem
                      ? `pergunta/${origem.identificador}/responder`
                      : edicao
                        ? `faq/${edicao.identificador}/alterar`
                        : 'faq/incluir',
                    origem ? corpo : { ...corpo, identificador: edicao?.identificador ?? null },
                  );
                })}
              >
                <div className="field full">
                  <label htmlFor="faq-pergunta">Pergunta *</label>
                  <input
                    id="faq-pergunta"
                    maxLength={200}
                    required
                    {...cadastro.register('pergunta', { required: true, maxLength: 200 })}
                  />
                </div>
                <div className="field full">
                  <label htmlFor="faq-resposta">Resposta *</label>
                  <textarea
                    id="faq-resposta"
                    rows={5}
                    maxLength={1000}
                    required
                    {...cadastro.register('resposta', { required: true, maxLength: 1000 })}
                  />
                  <small>{cadastro.watch('resposta').length}/1000 caracteres</small>
                </div>
                <div className="field">
                  <label htmlFor="faq-status">Status</label>
                  <select id="faq-status" {...cadastro.register('ativo')}>
                    <option value="true">Ativa (visível na Ajuda)</option>
                    <option value="false">Inativa</option>
                  </select>
                </div>
                <div className="form-actions full">
                  <button className="button button-primary" disabled={acao.isPending}>
                    {origem ? 'Responder e publicar' : edicao ? 'Salvar' : 'Cadastrar'}
                  </button>
                  {(edicao || origem) && (
                    <button type="button" className="button" onClick={cancelar}>
                      Cancelar
                    </button>
                  )}
                </div>
              </form>
            </section>
            <section className="panel">
              <h2>Perguntas enviadas pelos alunos</h2>
              <p className="panel-sub">{pendentes.length} pendente(s).</p>
              {pendentes.map((p) => (
                <article className="item-card" key={p.identificador}>
                  <strong>{p.texto}</strong>
                  <p className="tiny muted">Enviada em {data(p.criadaEm)}</p>
                  <div className="row-actions">
                    <button className="button button-secondary" onClick={() => responder(p)}>
                      Responder
                    </button>
                    <button
                      className="button button-danger"
                      disabled={acao.isPending}
                      onClick={() => executar(`pergunta/${p.identificador}/descartar`)}
                    >
                      Descartar
                    </button>
                  </div>
                </article>
              ))}
              {!pendentes.length && <p className="empty">Nenhuma pergunta pendente</p>}
            </section>
          </div>
        ) : (
          <section className="panel">
            <h2>Perguntas frequentes</h2>
            <p className="panel-sub">{entradas.resultado.total} pergunta(s).</p>
            <div className="toolbar">
              <div className="field">
                <label htmlFor="faq-busca">Buscar</label>
                <input
                  id="faq-busca"
                  placeholder="Palavra-chave"
                  value={busca}
                  onChange={(e) => definirBusca(e.target.value)}
                />
              </div>
            </div>
            {lista.map((f) => (
              <details className="faq-item" key={f.identificador}>
                <summary>{f.pergunta}</summary>
                <p>{f.resposta}</p>
              </details>
            ))}
            {!lista.length && <p className="empty">Nada encontrado</p>}
          </section>
        )}
        {gerenciar ? (
          <section className="panel">
            <h2>Entradas do FAQ</h2>
            <p className="panel-sub">Inativas não aparecem para alunos.</p>
            <div className="toolbar">
              <div className="field">
                <label htmlFor="faq-busca">Buscar</label>
                <input
                  id="faq-busca"
                  placeholder="Pergunta"
                  value={busca}
                  onChange={(e) => definirBusca(e.target.value)}
                />
              </div>
              <div className="field">
                <label htmlFor="faq-filtro">Status</label>
                <select
                  id="faq-filtro"
                  value={status}
                  onChange={(e) => definirStatus(e.target.value)}
                >
                  <option value="">Todos</option>
                  <option value="true">Ativa</option>
                  <option value="false">Inativa</option>
                </select>
              </div>
            </div>
            <div className="list-table-wrap">
              <table className="list-table">
                <thead>
                  <tr>
                    <th>Pergunta</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {lista.map((f) => (
                    <tr key={f.identificador}>
                      <td>
                        <strong>{f.pergunta}</strong>
                        <div className="subtext">{f.resposta.slice(0, 90)}</div>
                        <div className="row-actions">
                          <button
                            aria-label={`Editar ${f.pergunta}`}
                            className="button button-small"
                            onClick={() => editar(f)}
                          >
                            ✎
                          </button>
                          <button
                            className="button button-small"
                            disabled={acao.isPending}
                            onClick={() =>
                              executar(`faq/${f.identificador}/alterar`, { ...f, ativo: !f.ativo })
                            }
                          >
                            {f.ativo ? 'Inativar' : 'Ativar'}
                          </button>
                          <button
                            className="button button-danger button-small"
                            disabled={acao.isPending}
                            onClick={() => executar(`faq/${f.identificador}/excluir`)}
                          >
                            Excluir
                          </button>
                        </div>
                      </td>
                      <td>
                        <span className={`status-badge ${f.ativo ? 'aprovada' : 'encerrada'}`}>
                          {f.ativo ? 'Ativa' : 'Inativa'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {!lista.length && <p className="empty">Nenhuma entrada</p>}
          </section>
        ) : (
          <div className="column-stack">
            <section className="panel">
              <h2>{minhaEdicao ? 'Editar minha pergunta' : 'Enviar uma pergunta'}</h2>
              <p className="panel-sub">A coordenação responde e pode publicar no FAQ.</p>
              <form
                className="form-grid"
                onSubmit={duvida.handleSubmit((v) =>
                  executar(
                    minhaEdicao
                      ? `pergunta/${minhaEdicao.identificador}/editar`
                      : 'pergunta/enviar',
                    v,
                  ),
                )}
              >
                <div className="field full">
                  <label htmlFor="duvida-texto">Sua pergunta *</label>
                  <textarea
                    id="duvida-texto"
                    required
                    minLength={10}
                    maxLength={300}
                    {...duvida.register('texto', { required: true, minLength: 10, maxLength: 300 })}
                  />
                  <small>Entre 10 e 300 caracteres</small>
                </div>
                <div className="form-actions full">
                  <button className="button button-primary" disabled={acao.isPending}>
                    {minhaEdicao ? 'Salvar' : 'Enviar pergunta'}
                  </button>
                  {minhaEdicao && (
                    <button type="button" className="button" onClick={cancelar}>
                      Cancelar
                    </button>
                  )}
                </div>
              </form>
            </section>
            <section className="panel">
              <h2>Minhas perguntas</h2>
              {perguntas.resultado.projecoes.map((p) => (
                <article className="item-card" key={p.identificador}>
                  <div className="item-card-head">
                    <strong>{p.texto}</strong>
                    <span className="status-badge">
                      {p.situacao === 'PENDENTE'
                        ? 'Pendente'
                        : p.situacao === 'RESPONDIDA'
                          ? 'Respondida'
                          : 'Descartada'}
                    </span>
                  </div>
                  <p className="tiny">Enviada em {data(p.criadaEm)}</p>
                  {p.resposta && (
                    <p className="preservar-texto">
                      <b>Resposta:</b> {p.resposta}
                    </p>
                  )}
                  {p.situacao === 'PENDENTE' && (
                    <div className="row-actions">
                      <button
                        className="button button-small"
                        onClick={() => {
                          definirMinhaEdicao(p);
                          duvida.reset({ texto: p.texto });
                        }}
                      >
                        Editar
                      </button>
                      <button
                        className="button button-danger button-small"
                        disabled={acao.isPending}
                        onClick={() => executar(`pergunta/${p.identificador}/excluir`)}
                      >
                        Excluir
                      </button>
                    </div>
                  )}
                </article>
              ))}
              {!perguntas.resultado.total && <p className="empty">Nenhuma pergunta enviada</p>}
            </section>
          </div>
        )}
      </div>
    </>
  );
}
