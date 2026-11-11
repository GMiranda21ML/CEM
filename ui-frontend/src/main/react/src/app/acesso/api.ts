import { queryOptions } from '@tanstack/react-query';
import { chamarApi, type IAgregadoAutorizacoes } from '../../d5';

export interface UsuarioAutorizacoes extends IAgregadoAutorizacoes {
  inativar: boolean;
  reativar: boolean;
}

export interface ConviteAutorizacoes extends IAgregadoAutorizacoes {
  reenviar: boolean;
  cancelar: boolean;
}

export type Papel = 'ADMINISTRADOR' | 'COORDENADOR' | 'PROFESSOR' | 'ALUNO';
export type SituacaoUsuario = 'ATIVO' | 'INATIVO';
export type SituacaoConvite = 'AGUARDANDO_ACEITE' | 'ACEITO' | 'CANCELADO';

export interface UsuarioProjecao {
  identificador: string;
  nome: string;
  email: string;
  papel: Papel;
  curso: string | null;
  campus: string | null;
  situacao: SituacaoUsuario;
}

export interface ConviteProjecao {
  identificador: string;
  email: string;
  papel: Papel;
  curso: string;
  enviadoEm: string;
  validoAte: string;
  situacao: SituacaoConvite;
}

export interface DominioEmailProjecao {
  identificador: string;
}

export interface ResultadoPesquisa<T> {
  resultado: {
    projecoes: T[];
    total: number;
  };
}

function paraQuery(parametros: Record<string, string | undefined>): string {
  const busca = new URLSearchParams();
  for (const chave in parametros) {
    const valor = parametros[chave];
    if (valor) {
      busca.set(chave, valor);
    }
  }
  const texto = busca.toString();
  return texto ? `?${texto}` : '';
}

export interface FiltroUsuarios {
  nome?: string;
  papel?: Papel;
  situacao?: SituacaoUsuario;
}

export function consultaUsuarios(filtro: FiltroUsuarios = {}) {
  return queryOptions({
    queryKey: ['acesso', 'usuarios', filtro],
    queryFn: () =>
      chamarApi<ResultadoPesquisa<UsuarioProjecao>>(
        `/backend/acesso/usuario/pesquisa${paraQuery({ ...filtro, tamanhoPagina: '100' })}`,
      ),
  });
}

export interface FiltroConvites {
  situacao?: SituacaoConvite;
}

export function consultaConvites(filtro: FiltroConvites = {}) {
  return queryOptions({
    queryKey: ['acesso', 'convites', filtro],
    queryFn: () =>
      chamarApi<ResultadoPesquisa<ConviteProjecao>>(
        `/backend/acesso/convite/pesquisa${paraQuery({ ...filtro, tamanhoPagina: '100' })}`,
      ),
  });
}

export const consultaDominiosEmail = queryOptions({
  queryKey: ['acesso', 'dominios-email'],
  queryFn: () =>
    chamarApi<ResultadoPesquisa<DominioEmailProjecao>>(
      `/backend/acesso/dominio-email/pesquisa${paraQuery({ tamanhoPagina: '100' })}`,
    ),
});

export function criarContaAluno(dados: { nome: string; email: string; curso: string; senha: string }) {
  return chamarApi('/backend/acesso/usuario/criar-conta', {
    method: 'POST',
    corpo: {
      nome: dados.nome,
      emailInstitucional: dados.email,
      senha: dados.senha,
      papel: 'ALUNO',
      curso: dados.curso,
      campus: null,
      situacao: 'ATIVO',
    },
  });
}

export function verificarConvite(email: string, codigo: string) {
  return chamarApi<ConviteProjecao>(
    `/backend/acesso/convite/verificacao${paraQuery({ email, codigo })}`,
  );
}

export function aceitarConvite(dados: { email: string; codigo: string; nome: string; senha: string }) {
  return chamarApi<UsuarioProjecao>('/backend/acesso/convite/aceitar', { method: 'POST', corpo: dados });
}

export function convidar(dados: { email: string; papel: 'COORDENADOR' | 'PROFESSOR'; curso: string }) {
  return chamarApi<ConviteProjecao>('/backend/acesso/convite/convidar', { method: 'POST', corpo: dados });
}

export function reenviarConvite(identificador: string) {
  return chamarApi(`/backend/acesso/convite/${identificador}/reenviar`, { method: 'POST' });
}

export function cancelarConvite(identificador: string) {
  return chamarApi(`/backend/acesso/convite/${identificador}/cancelar`, { method: 'POST' });
}

export function incluirDominioEmail(dominio: string) {
  const normalizado = dominio.trim().toLowerCase().replace(/^@/, '');
  return chamarApi('/backend/acesso/dominio-email/incluir', {
    method: 'POST',
    corpo: { dominio: normalizado },
  });
}

export function excluirDominioEmail(dominio: string) {
  return chamarApi(`/backend/acesso/dominio-email/${encodeURIComponent(dominio)}/excluir`, {
    method: 'POST',
  });
}

export function atualizarDadosUsuario(dados: {
  identificador: string;
  nome: string;
  curso: string | null;
  campus: string | null;
}) {
  return chamarApi(`/backend/acesso/usuario/${dados.identificador}/atualizar-dados`, {
    method: 'POST',
    corpo: { nome: dados.nome, curso: dados.curso, campus: dados.campus },
  });
}

export function inativarUsuario(identificador: string) {
  return chamarApi(`/backend/acesso/usuario/${identificador}/inativar`, { method: 'POST' });
}

export function reativarUsuario(identificador: string) {
  return chamarApi(`/backend/acesso/usuario/${identificador}/reativar`, { method: 'POST' });
}

export function excluirUsuario(identificador: string) {
  return chamarApi(`/backend/acesso/usuario/${identificador}/excluir`, { method: 'POST' });
}
