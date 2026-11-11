import { useState } from 'react';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import {
  cancelarConvite,
  consultaConvites,
  consultaDominiosEmail,
  consultaUsuarios,
  convidar,
  excluirDominioEmail,
  excluirUsuario,
  inativarUsuario,
  incluirDominioEmail,
  reativarUsuario,
  reenviarConvite,
  type UsuarioProjecao,
} from '../api';
import { clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';
import { EditarUsuarioModal } from './EditarUsuarioModal';

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaUsuarios());
  await clienteConsultas.ensureQueryData(consultaConvites());
  await clienteConsultas.ensureQueryData(consultaDominiosEmail);
  return null;
}

interface DadosConvitePapel {
  email: string;
  curso: string;
}

interface DadosDominio {
  dominio: string;
}

function textoSituacaoConvite(situacao: string): string {
  if (situacao === 'AGUARDANDO_ACEITE') {
    return 'Aguardando aceite';
  }
  if (situacao === 'ACEITO') {
    return 'Aceito';
  }
  return 'Cancelado';
}

function classeSituacaoConvite(situacao: string): string {
  if (situacao === 'AGUARDANDO_ACEITE') {
    return 'pendente';
  }
  if (situacao === 'ACEITO') {
    return 'aprovada';
  }
  return 'encerrada';
}

export function UsuariosEAcessos() {
  const { publicar } = useNotificacoes();
  const [busca, definirBusca] = useState('');
  const [emEdicao, definirEmEdicao] = useState<UsuarioProjecao | null>(null);

  const { data: dominios } = useSuspenseQuery(consultaDominiosEmail);
  const { data: convitesTodos } = useQuery(consultaConvites());
  const { data: usuariosTodos } = useQuery(consultaUsuarios({ nome: busca || undefined }));
  // O coordenador só mantém professores e alunos (RN004): administrador e outros
  // coordenadores não aparecem aqui, mesmo que a pesquisa os devolva.
  const usuariosGerenciaveis = (usuariosTodos?.resultado.projecoes ?? []).filter(
    (usuario) => usuario.papel === 'PROFESSOR' || usuario.papel === 'ALUNO',
  );

  const convitesProfessores = (convitesTodos ?? { resultado: { projecoes: [], total: 0 } }).resultado.projecoes.filter(
    (convite) => convite.papel === 'PROFESSOR',
  );

  function invalidarUsuarios() {
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'usuarios'] });
  }

  function invalidarConvites() {
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'convites'] });
  }

  function invalidarDominios() {
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'dominios-email'] });
  }

  const formConvite = useForm<DadosConvitePapel>({ mode: 'onChange' });
  const convidarProfessor = useMutation({
    mutationFn: (dados: DadosConvitePapel) => convidar({ ...dados, papel: 'PROFESSOR' }),
    onSuccess: (_resultado, dados) => {
      formConvite.reset();
      invalidarConvites();
      publicar('sucesso', `Convite enviado por e-mail para ${dados.email}.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const reenviar = useMutation({
    mutationFn: (identificador: string) => reenviarConvite(identificador),
    onSuccess: () => {
      invalidarConvites();
      publicar('sucesso', 'Código reenviado por e-mail.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const cancelar = useMutation({
    mutationFn: (identificador: string) => cancelarConvite(identificador),
    onSuccess: () => {
      invalidarConvites();
      publicar('sucesso', 'Convite cancelado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const formDominio = useForm<DadosDominio>({ mode: 'onChange' });
  const liberarDominio = useMutation({
    mutationFn: (dados: DadosDominio) => incluirDominioEmail(dados.dominio),
    onSuccess: (_resultado, dados) => {
      formDominio.reset();
      invalidarDominios();
      publicar('sucesso', `Domínio ${dados.dominio} liberado para cadastro de alunos.`);
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const removerDominio = useMutation({
    mutationFn: (dominio: string) => excluirDominioEmail(dominio),
    onSuccess: () => {
      invalidarDominios();
      publicar('sucesso', 'Domínio removido.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const inativar = useMutation({
    mutationFn: (identificador: string) => inativarUsuario(identificador),
    onSuccess: () => {
      invalidarUsuarios();
      publicar('sucesso', 'Usuário inativado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const reativar = useMutation({
    mutationFn: (identificador: string) => reativarUsuario(identificador),
    onSuccess: () => {
      invalidarUsuarios();
      publicar('sucesso', 'Usuário reativado.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const excluir = useMutation({
    mutationFn: (identificador: string) => excluirUsuario(identificador),
    onSuccess: () => {
      invalidarUsuarios();
      publicar('sucesso', 'Usuário excluído.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <>
      <div className="page-header">
        <div>
          <div className="eyebrow">Área do coordenador</div>
          <h1>Usuários e acessos</h1>
          <p>
            Convide professores, defina quais domínios de e-mail os alunos podem usar no cadastro e
            mantenha os usuários: pesquisar, editar e inativar.
          </p>
        </div>
      </div>

      <div className="content-grid">
        <section className="panel">
          <h2>Convidar professor</h2>
          <p className="panel-sub">
            O professor recebe um e-mail com código de verificação válido por 7 dias e cria a
            própria conta.
          </p>
          <form
            className="form-grid"
            onSubmit={formConvite.handleSubmit((dados) => convidarProfessor.mutate(dados))}
            noValidate
          >
            <div className="field full">
              <label htmlFor="email-professor">E-mail institucional do professor *</label>
              <input id="email-professor" type="email" {...formConvite.register('email', { required: true })} />
            </div>
            <div className="field full">
              <label htmlFor="curso-professor">Curso *</label>
              <input id="curso-professor" {...formConvite.register('curso', { required: true })} />
            </div>
            <div className="form-actions">
              <button
                className="button button-primary"
                type="submit"
                disabled={!formConvite.formState.isValid || convidarProfessor.isPending}
              >
                Enviar convite →
              </button>
            </div>
          </form>
        </section>

        <section className="panel">
          <h2>Domínios de e-mail dos alunos</h2>
          <p className="panel-sub">
            Só e-mails desses domínios conseguem criar conta de aluno. Contas já criadas não são
            afetadas.
          </p>
          <div className="chips">
            {dominios.resultado.projecoes.length === 0 ? (
              <span className="tiny muted">Nenhum domínio: nenhum aluno consegue se cadastrar.</span>
            ) : (
              dominios.resultado.projecoes.map((dominio) => (
                <span className="chip" key={dominio.identificador}>
                  @{dominio.identificador}
                  <button
                    type="button"
                    className="chip-x"
                    title="Remover domínio"
                    aria-label={`Remover ${dominio.identificador}`}
                    disabled={removerDominio.isPending}
                    onClick={() => removerDominio.mutate(dominio.identificador)}
                  >
                    ×
                  </button>
                </span>
              ))
            )}
          </div>
          <form
            className="inline-form"
            onSubmit={formDominio.handleSubmit((dados) => liberarDominio.mutate(dados))}
            noValidate
          >
            <input
              className="grow-input"
              placeholder="ex.: aluno.cem.edu.br"
              aria-label="Novo domínio"
              {...formDominio.register('dominio', { required: true })}
            />
            <button
              className="button button-ghost button-small"
              type="submit"
              disabled={!formDominio.formState.isValid || liberarDominio.isPending}
            >
              Adicionar domínio
            </button>
          </form>
        </section>
      </div>

      <section className="panel">
        <h2>Convites de professores</h2>
        {convitesProfessores.length === 0 ? (
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
                {convitesProfessores.map((convite) => (
                  <tr key={convite.identificador}>
                    <td>
                      <strong>{convite.email}</strong>
                      <div className="subtext">professor · {convite.curso}</div>
                    </td>
                    <td>{new Date(convite.enviadoEm).toLocaleDateString('pt-BR')}</td>
                    <td>
                      <span className={`status ${classeSituacaoConvite(convite.situacao)}`}>
                        {textoSituacaoConvite(convite.situacao)}
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

      <section className="panel">
        <h2>Usuários</h2>
        <div className="toolbar">
          <div className="field">
            <label htmlFor="busca-usuario">Buscar</label>
            <input
              id="busca-usuario"
              placeholder="Nome ou e-mail"
              value={busca}
              onChange={(evento) => definirBusca(evento.target.value)}
            />
          </div>
        </div>
        {usuariosGerenciaveis.length === 0 ? (
          <div className="empty">
            <span className="empty-icon" aria-hidden="true">
              ◇
            </span>
            <strong>Nenhum usuário</strong>
          </div>
        ) : (
          <div className="list-table-wrap">
            <table className="list-table">
              <thead>
                <tr>
                  <th>Usuário</th>
                  <th>Perfil</th>
                  <th>Curso</th>
                  <th>Situação</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {usuariosGerenciaveis.map((usuario) => (
                  <tr key={usuario.identificador}>
                    <td>
                      <strong>{usuario.nome}</strong>
                      <div className="subtext">{usuario.email}</div>
                    </td>
                    <td>{usuario.papel}</td>
                    <td>{usuario.curso}</td>
                    <td>
                      <span className={`status ${usuario.situacao === 'ATIVO' ? 'aprovada' : 'encerrada'}`}>
                        {usuario.situacao === 'ATIVO' ? 'Ativo' : 'Inativo'}
                      </span>
                    </td>
                    <td>
                      <div className="row-actions">
                        <button
                          type="button"
                          className="icon-btn"
                          title="Editar"
                          aria-label="Editar"
                          onClick={() => definirEmEdicao(usuario)}
                        >
                          ✎
                        </button>
                        {usuario.situacao === 'ATIVO' ? (
                          <button
                            className="button button-ghost button-small"
                            type="button"
                            disabled={inativar.isPending}
                            onClick={() => inativar.mutate(usuario.identificador)}
                          >
                            Inativar
                          </button>
                        ) : (
                          <button
                            className="button button-ghost button-small"
                            type="button"
                            disabled={reativar.isPending}
                            onClick={() => reativar.mutate(usuario.identificador)}
                          >
                            Ativar
                          </button>
                        )}
                        <button
                          className="button button-danger button-small"
                          type="button"
                          disabled={excluir.isPending}
                          onClick={() => excluir.mutate(usuario.identificador)}
                        >
                          Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {emEdicao && <EditarUsuarioModal usuario={emEdicao} fechar={() => definirEmEdicao(null)} />}
    </>
  );
}
