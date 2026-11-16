import { queryOptions } from '@tanstack/react-query';
import { chamarApi } from '../../../d5';

export interface Pagina<T> { resultado: { projecoes: T[]; total: number } }
export interface CursoProjecao {
  identificador: string;
  nome: string;
  sigla: string;
  quantidadeDisciplinas: number;
  quantidadeUsuarios: number;
}
export interface DisciplinaProjecao {
  identificador: string;
  curso: string;
  cursoNome: string;
  professor: string;
  professorNome: string;
  nome: string;
  codigo: string;
  area: string;
  exclusaoPermitida: boolean;
}
export interface ProfessorProjecao { identificador: string; nome: string }

const parametros = (itens: Record<string, string | undefined>) => {
  const busca = new URLSearchParams();
  Object.entries(itens).forEach(([chave, valor]) => valor && busca.set(chave, valor));
  busca.set('tamanhoPagina', '100');
  return busca.toString();
};

export const consultaCursos = (texto = '') => queryOptions({
  queryKey: ['acesso', 'cursos', texto],
  queryFn: () => chamarApi<Pagina<CursoProjecao>>(`/backend/acesso/curso/pesquisa?${parametros({ texto })}`),
});
export const consultaDisciplinas = (texto = '', curso = '') => queryOptions({
  queryKey: ['acesso', 'disciplinas', texto, curso],
  queryFn: () => chamarApi<Pagina<DisciplinaProjecao>>(
    `/backend/acesso/disciplina/pesquisa?${parametros({ texto, curso })}`,
  ),
});
export const consultaProfessores = (curso: string) => queryOptions({
  queryKey: ['acesso', 'professores', curso],
  enabled: Boolean(curso),
  queryFn: () => chamarApi<ProfessorProjecao[]>(
    `/backend/acesso/disciplina/professores?curso=${encodeURIComponent(curso)}`,
  ),
});
export const cadastrarCurso = (dados: { nome: string; sigla: string }) =>
  chamarApi('/backend/acesso/curso/cadastrar', { method: 'POST', corpo: dados });
export const excluirCurso = (id: string) =>
  chamarApi(`/backend/acesso/curso/${id}/excluir`, { method: 'POST' });
export const cadastrarDisciplina = (dados: {
  nome: string; codigo: string; area: string; curso: string; professor: string;
}) => chamarApi('/backend/acesso/disciplina/cadastrar', { method: 'POST', corpo: dados });
export const editarDisciplina = (id: string, dados: { nome: string; area: string; professor: string }) =>
  chamarApi(`/backend/acesso/disciplina/${id}/editar`, { method: 'POST', corpo: dados });
export const excluirDisciplina = (id: string) =>
  chamarApi(`/backend/acesso/disciplina/${id}/excluir`, { method: 'POST' });
