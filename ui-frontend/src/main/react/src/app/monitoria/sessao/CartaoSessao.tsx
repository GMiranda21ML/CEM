import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { data, useAcao } from '../../comunicacao/api';
import { consultaSessao, type Contexto, type Falta, type Sessao } from '../api';
export function CartaoSessao({
  sessao,
  contexto,
  faltas,
}: {
  sessao: Sessao;
  contexto: Contexto;
  faltas: Falta[];
}) {
  const { data: detalhe } = useQuery(consultaSessao(sessao.identificador));
  const formulario = useForm<{ observacao: string }>({ defaultValues: { observacao: '' } });
  const avaliacao = useForm<{ nota: string; comentario: string }>({
    defaultValues: { nota: '5', comentario: '' },
  });
  const [aberto, definirAberto] = useState(false);
  const [editando, definirEditando] = useState(false);
  const acao = useAcao(() => {
    definirAberto(false);
    definirEditando(false);
  });
  const ausencia = faltas.find((f) => f.sessao === sessao.identificador && f.papel === 'MONITOR');
  const aluno = detalhe?.agregado.alunos.includes(contexto.usuario) ?? false;
  const encerrada = !sessao.cancelada && new Date(sessao.fim) <= new Date();
  const prazo = new Date(sessao.fim).getTime() + 30 * 86400000 >= Date.now();
  const podeCancelar =
    !sessao.cancelada &&
    (sessao.monitor === contexto.usuario || sessao.professor === contexto.usuario);
  const nome = (id: string) => contexto.pessoas.find((p) => p.identificador === id)?.nome ?? id;
  return (
    <article className="item-card">
      <div className="item-card-head">
        <div>
          <strong>{sessao.disciplina}</strong>
          <p>
            {data(sessao.inicio)} · {sessao.inicio.slice(11, 16)}–{sessao.fim.slice(11, 16)}
          </p>
          <p className="tiny">
            Monitor: {nome(sessao.monitor)} ·{' '}
            {contexto.campus.find((c) => c.identificador === sessao.campus)?.nome ?? 'Campus'}
            {faltas.some((f) => f.reposicao === sessao.identificador) && ' · Reposição'}
          </p>
        </div>
        <span className="status-badge">
          {sessao.cancelada ? 'Cancelada' : encerrada ? 'Realizada' : 'Agendada'}
        </span>
      </div>
      {podeCancelar && (
        <button
          className="button button-danger button-small"
          disabled={acao.isPending}
          onClick={() =>
            acao.mutate({ caminho: `monitoria/sessao/${sessao.identificador}/cancelar` })
          }
        >
          Cancelar sessão
        </button>
      )}
      {aluno &&
        encerrada &&
        prazo &&
        (ausencia ? (
          <div className="absence-box">
            <b>⚑ Ausência do monitor registrada</b>
            <p>{ausencia.observacao}</p>
            {ausencia.autor === contexto.usuario && (
              <div className="row-actions">
                <button
                  className="button button-small"
                  onClick={() => {
                    definirEditando(true);
                    definirAberto(true);
                    formulario.reset({ observacao: ausencia.observacao ?? '' });
                  }}
                >
                  Editar
                </button>
                <button
                  className="button button-danger button-small"
                  disabled={acao.isPending}
                  onClick={() =>
                    acao.mutate({ caminho: `monitoria/falta/${ausencia.identificador}/excluir` })
                  }
                >
                  Excluir
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="absence-link">
            <button className="text-action" onClick={() => definirAberto(!aberto)}>
              O monitor não compareceu? Avise aqui
            </button>
            <small> (conta para o alerta de faltas)</small>
          </div>
        ))}
      {aberto && (!ausencia || editando) && (
        <form
          className="form-grid absence-box"
          onSubmit={formulario.handleSubmit((v) =>
            acao.mutate({
              caminho:
                editando && ausencia
                  ? `monitoria/falta/${ausencia.identificador}/editar`
                  : 'monitoria/falta/registrar',
              corpo: editando
                ? v
                : { ...v, sessao: sessao.identificador, pessoa: sessao.monitor, papel: 'MONITOR' },
            }),
          )}
        >
          <div className="field full">
            <label htmlFor={`relato-${sessao.identificador}`}>O que aconteceu?</label>
            <textarea
              id={`relato-${sessao.identificador}`}
              maxLength={1000}
              {...formulario.register('observacao')}
            />
          </div>
          <div className="form-actions full">
            <button className="button button-danger button-small" disabled={acao.isPending}>
              {editando ? 'Salvar' : 'Confirmar ausência do monitor'}
            </button>
            <button
              type="button"
              className="button button-small"
              onClick={() => definirAberto(false)}
            >
              Cancelar
            </button>
          </div>
        </form>
      )}
      {aluno &&
        encerrada &&
        !ausencia &&
        new Date(sessao.fim).getTime() + 7 * 86400000 >= Date.now() && (
          <details className="faq-item">
            <summary>Avaliar sessão</summary>
            <form
              className="form-grid"
              onSubmit={avaliacao.handleSubmit((v) =>
                acao.mutate({
                  caminho: 'monitoria/avaliacao/registrar',
                  corpo: {
                    sessao: sessao.identificador,
                    nota: Number(v.nota),
                    comentario: v.comentario,
                  },
                }),
              )}
            >
              <div className="field">
                <label htmlFor={`nota-${sessao.identificador}`}>Nota</label>
                <select id={`nota-${sessao.identificador}`} {...avaliacao.register('nota')}>
                  {[5, 4, 3, 2, 1].map((n) => (
                    <option key={n}>{n}</option>
                  ))}
                </select>
              </div>
              <div className="field full">
                <label htmlFor={`comentario-${sessao.identificador}`}>Comentário</label>
                <textarea
                  id={`comentario-${sessao.identificador}`}
                  maxLength={1000}
                  {...avaliacao.register('comentario')}
                />
              </div>
              <button className="button button-primary" disabled={acao.isPending}>
                Enviar avaliação
              </button>
            </form>
          </details>
        )}
    </article>
  );
}
