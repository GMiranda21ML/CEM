import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import {
  cancelarConvite,
  consultaConvites,
  consultaUsuarios,
  convidar,
  reenviarConvite,
} from '../api';
import { clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaUsuarios({ papel: 'COORDENADOR' }));
  await clienteConsultas.ensureQueryData(consultaConvites());
  return null;
}

interface DadosConvite {
  email: string;
  curso: string;
}

function textoSituacao(situacao: string): string {
  if (situacao === 'AGUARDANDO_ACEITE') {
    return 'Aguardando aceite';
  }
  if (situacao === 'ACEITO') {
    return 'Aceito';
  }
  return 'Cancelado';
}

function classeSituacao(situacao: string): string {
  if (situacao === 'AGUARDANDO_ACEITE') {
    return 'pendente';
  }
  if (situacao === 'ACEITO') {
    return 'aprovada';
  }
  return 'encerrada';
}

export function Coordenadores() {
  const { publicar } = useNotificacoes();
  const { register, handleSubmit, reset, formState } = useForm<DadosConvite>({ mode: 'onChange' });

  const { data: coordenadores } = useSuspenseQuery(consultaUsuarios({ papel: 'COORDENADOR' }));
  const { data: convitesTodos } = useQuery(consultaConvites());
  const convites = (convitesTodos ?? { resultado: { projecoes: [], total: 0 } }).resultado.projecoes.filter(
    (convite) => convite.papel === 'COORDENADOR',
  );

  function invalidar() {
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'usuarios'] });
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'convites'] });
  }

  const convidarCoordenador = useMutation({
    mutationFn: (dados: DadosConvite) => convidar({ ...dados, papel: 'COORDENADOR' }),
    onSuccess: (_resultado, dados) => {
      reset();
      invalidar();
      publicar('sucesso', `Convite enviado por e-mail para ${dados.email}.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const reenviar = useMutation({
    mutationFn: (identificador: string) => reenviarConvite(identificador),
    onSuccess: () => {
      invalidar();
      publicar('sucesso', 'Código reenviado por e-mail.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const cancelar = useMutation({
    mutationFn: (identificador: string) => cancelarConvite(identificador),
    onSuccess: () => {
      invalidar();
      publicar('sucesso', 'Convite cancelado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <>
      <div className="page-header">
        <div>
          <div className="eyebrow">Área do administrador</div>
          <h1>Coordenadores</h1>
          <p>
            O super administrador convida os coordenadores. Cada convite manda um e-mail com código
            de verificação; o coordenador cria a própria conta com ele.
          </p>
        </div>
      </div>

      <div className="content-grid">
        <section className="panel">
          <h2>Convidar coordenador</h2>
          <p className="panel-sub">O código vale 7 dias.</p>
          <form className="form-grid" onSubmit={handleSubmit((dados) => convidarCoordenador.mutate(dados))} noValidate>
            <div className="field full">
              <label htmlFor="email">E-mail institucional do coordenador *</label>
              <input id="email" type="email" {...register('email', { required: true })} />
            </div>
            <div className="field full">
              <label htmlFor="curso">Curso que vai coordenar *</label>
              <input id="curso" {...register('curso', { required: true })} />
            </div>
            <div className="form-actions">
              <button
                className="button button-primary"
                type="submit"
                disabled={!formState.isValid || convidarCoordenador.isPending}
              >
                Enviar convite →
              </button>
            </div>
          </form>
        </section>

        <section className="panel">
          <h2>Coordenadores ativos</h2>
          <p className="panel-sub">{coordenadores.resultado.total} coordenador(es).</p>
          {coordenadores.resultado.projecoes.length === 0 ? (
            <div className="empty">
              <span className="empty-icon" aria-hidden="true">
                ◇
              </span>
              <strong>Nenhum coordenador</strong>
            </div>
          ) : (
            <div className="list-table-wrap">
              <table className="list-table">
                <thead>
                  <tr>
                    <th>Coordenador</th>
                    <th>Curso</th>
                    <th>Situação</th>
                  </tr>
                </thead>
                <tbody>
                  {coordenadores.resultado.projecoes.map((coordenador) => (
                    <tr key={coordenador.identificador}>
                      <td>
                        <strong>{coordenador.nome}</strong>
                        <div className="subtext">{coordenador.email}</div>
                      </td>
                      <td>{coordenador.curso}</td>
                      <td>
                        <span className={`status ${coordenador.situacao === 'ATIVO' ? 'aprovada' : 'encerrada'}`}>
                          {coordenador.situacao === 'ATIVO' ? 'Ativo' : 'Inativo'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>

      <section className="panel">
        <h2>Convites enviados</h2>
        {convites.length === 0 ? (
          <div className="empty">
            <span className="empty-icon" aria-hidden="true">
              ◇
            </span>
            <strong>Nenhum convite</strong>
          </div>
        ) : (
          <div className="list-table-wrap">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Convidado</th>
                  <th>Enviado</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {convites.map((convite) => (
                  <tr key={convite.identificador}>
                    <td>
                      <strong>{convite.email}</strong>
                      <div className="subtext">coordenador · {convite.curso}</div>
                    </td>
                    <td>{new Date(convite.enviadoEm).toLocaleDateString('pt-BR')}</td>
                    <td>
                      <span className={`status ${classeSituacao(convite.situacao)}`}>
                        {textoSituacao(convite.situacao)}
                      </span>
                    </td>
                    <td>
                      {convite.situacao === 'AGUARDANDO_ACEITE' && (
                        <div className="row-actions">
                          <button
                            className="button button-ghost button-small"
                            type="button"
                            disabled={reenviar.isPending}
                            onClick={() => reenviar.mutate(convite.identificador)}
                          >
                            Reenviar código
                          </button>
                          <button
                            className="button button-danger button-small"
                            type="button"
                            disabled={cancelar.isPending}
                            onClick={() => cancelar.mutate(convite.identificador)}
                          >
                            Cancelar
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  );
}
