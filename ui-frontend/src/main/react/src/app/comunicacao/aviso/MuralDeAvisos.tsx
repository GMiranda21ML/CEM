import { useState } from 'react';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { clienteConsultas, useAutenticacao } from '../../../d5';
import { consultaAvisos, data, hoje, useAcao, type Aviso, type AvisoAutorizacoes } from '../api';

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaAvisos());
  return null;
}
export function MuralDeAvisos() {
  const autorizacoes = useAutenticacao().autorizacoesDe<AvisoAutorizacoes>('aviso');
  const gerenciar = autorizacoes?.alterar ?? false;
  const publicar = autorizacoes?.publicar ?? false;
  const [edicao, definirEdicao] = useState<Aviso | 'novo' | null>(null);
  const [busca, definirBusca] = useState('');
  const [status, definirStatus] = useState('');
  const { data: avisos } = useSuspenseQuery(consultaAvisos(busca));
  const formulario = useForm<{ titulo: string; texto: string; validade: string }>({
    defaultValues: { titulo: '', texto: '', validade: hoje() },
  });
  const acao = useAcao(() => definirEdicao(null));
  const lista = avisos.resultado.projecoes.filter(
    (a) => status === '' || String(a.ativo) === status,
  );
  function editar(a: Aviso | 'novo') {
    definirEdicao(a);
    formulario.reset(a === 'novo' ? { titulo: '', texto: '', validade: hoje() } : a);
  }
  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">COMUNICAÇÃO</span>
          <h1>Mural de avisos</h1>
          <p>
            Publique comunicados para alunos, monitores e professores. Avisos expirados saem do
            mural automaticamente; a remoção é lógica (inativar).
          </p>
        </div>
        {publicar && !edicao && (
          <button className="button button-primary" onClick={() => editar('novo')}>
            + Novo aviso
          </button>
        )}
      </div>
      {edicao && (edicao === 'novo' ? publicar : gerenciar) && (
        <section className="panel">
          <h2>{edicao === 'novo' ? 'Novo aviso' : 'Editar aviso'}</h2>
          <form
            className="form-grid"
            onSubmit={formulario.handleSubmit((v) =>
              acao.mutate({
                caminho:
                  edicao === 'novo'
                    ? 'comunicacao/aviso/publicar'
                    : `comunicacao/aviso/${edicao.identificador}/alterar`,
                corpo: edicao === 'novo' ? v : { ...edicao, ...v },
              }),
            )}
          >
            <div className="field full">
              <label htmlFor="aviso-titulo">Título *</label>
              <input
                id="aviso-titulo"
                required
                maxLength={100}
                {...formulario.register('titulo', { required: true, maxLength: 100 })}
              />
              <small>{formulario.watch('titulo').length}/100</small>
            </div>
            <div className="field full">
              <label htmlFor="aviso-texto">Texto *</label>
              <textarea
                id="aviso-texto"
                rows={5}
                required
                maxLength={1000}
                {...formulario.register('texto', { required: true, maxLength: 1000 })}
              />
              <small>{formulario.watch('texto').length}/1000</small>
            </div>
            <div className="field">
              <label htmlFor="aviso-validade">Data de validade *</label>
              <input
                id="aviso-validade"
                type="date"
                required
                min={hoje()}
                {...formulario.register('validade', { required: true })}
              />
              <small>Igual ou posterior a hoje.</small>
            </div>
            <div className="form-actions full">
              <button className="button button-primary" disabled={acao.isPending}>
                Publicar
              </button>
              <button className="button" type="button" onClick={() => definirEdicao(null)}>
                Cancelar
              </button>
            </div>
          </form>
        </section>
      )}
      <section className="panel">
        <h2>Avisos</h2>
        <p className="panel-sub">{avisos.resultado.total} aviso(s).</p>
        <div className="toolbar">
          <div className="field">
            <label htmlFor="aviso-busca">Buscar por título</label>
            <input
              id="aviso-busca"
              placeholder="Título"
              value={busca}
              onChange={(e) => definirBusca(e.target.value)}
            />
          </div>
          {gerenciar && (
            <div className="field">
              <label htmlFor="aviso-status">Status</label>
              <select
                id="aviso-status"
                value={status}
                onChange={(e) => definirStatus(e.target.value)}
              >
                <option value="">Todos</option>
                <option value="true">Ativo</option>
                <option value="false">Inativo</option>
              </select>
            </div>
          )}
        </div>
        {gerenciar ? (
          <div className="list-table-wrap">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Título</th>
                  <th>Texto</th>
                  <th>Publicado em</th>
                  <th>Validade</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {lista.map((a) => (
                  <tr key={a.identificador}>
                    <td>
                      <strong>{a.titulo}</strong>
                    </td>
                    <td className="subtext">{a.texto.slice(0, 70)}</td>
                    <td>{data(a.publicadoEm)}</td>
                    <td>
                      {data(a.validade)}
                      {a.validade < hoje() && <div className="subtext">expirado</div>}
                    </td>
                    <td>
                      <span className={`status-badge ${a.ativo ? 'aprovada' : 'encerrada'}`}>
                        {a.ativo ? 'Ativo' : 'Inativo'}
                      </span>
                    </td>
                    <td>
                      <div className="row-actions">
                        <button
                          className="button button-small"
                          aria-label={`Editar ${a.titulo}`}
                          onClick={() => editar(a)}
                        >
                          ✎
                        </button>
                        <button
                          className="button button-small"
                          disabled={acao.isPending}
                          onClick={() =>
                            acao.mutate({
                              caminho: `comunicacao/aviso/${a.identificador}/alternar`,
                            })
                          }
                        >
                          {a.ativo ? 'Inativar' : 'Ativar'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          lista.map((a) => (
            <article className="notice-card" key={a.identificador}>
              <h3>{a.titulo}</h3>
              <p className="preservar-texto">{a.texto}</p>
              <small>
                Publicado em {data(a.publicadoEm)} · válido até {data(a.validade)}
              </small>
            </article>
          ))
        )}
        {!lista.length && <p className="empty">Nenhum aviso</p>}
      </section>
    </>
  );
}
