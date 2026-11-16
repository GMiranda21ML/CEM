import { queryOptions } from '@tanstack/react-query';
import { chamarApi } from '../../d5';
import { pesquisa } from '../comunicacao/api';
export interface Sessao {
  identificador: string;
  disciplina: string;
  professor: string;
  monitor: string;
  campus: string;
  sala: string;
  alunos: string[];
  inicio: string;
  fim: string;
  cancelada: boolean;
  respostas?: RespostaSessao[];
  inicioAnterior?: string | null;
}
export type SituacaoRespostaSessao = 'SEM_RESPOSTA' | 'VAI_PARTICIPAR' | 'NAO_VAI_PARTICIPAR';
export interface RespostaSessao {
  aluno: string;
  situacao: SituacaoRespostaSessao;
  motivo?: string | null;
  inicioSugerido?: string | null;
}
export interface Falta {
  reposicao: string | null;
  identificador: string;
  sessao: string;
  pessoa: string;
  papel: string;
  autor: string;
  observacao: string | null;
  registradaEm: string;
}
export interface Disponibilidade {
  identificador: string;
  disciplina: string;
  monitor: string;
  campus: string;
  diaSemana: number;
  inicio: string;
  fim: string;
}
export interface Alerta {
  identificador: string;
  pessoa: string;
  papel: string;
  disciplina: string;
  acompanhado: boolean;
  criadoEm: string;
}
export interface Sugestao {
  inicio: string;
  fim: string;
  campus: string;
}
export interface Contexto {
  usuario: string;
  pessoas: { identificador: string; nome: string; papel: string; curso?: string | null }[];
  campus: { identificador: string; nome: string }[];
  cursoUsuario?: string | null;
}
export const consultaSessoes = () => pesquisa<Sessao>('monitoria', 'sessao');
export const consultaFaltas = () => pesquisa<Falta>('monitoria', 'falta');
export const consultaDisponibilidades = () =>
  pesquisa<Disponibilidade>('monitoria', 'disponibilidade');
export const consultaAlertas = () => pesquisa<Alerta>('monitoria', 'alerta');
export const consultaContexto = queryOptions({
  queryKey: ['funcionalidades', 'agenda', 'contexto'],
  queryFn: () => chamarApi<Contexto>('/backend/monitoria/sessao/contexto'),
});
export const consultaSessao = (id: string) =>
  queryOptions({
    queryKey: ['funcionalidades', 'sessao', id],
    queryFn: () => chamarApi<{ agregado: Sessao }>(`/backend/monitoria/sessao/${id}/alteracao`),
    enabled: !!id,
  });
export const consultaSugestoes = (id: string) =>
  queryOptions({
    queryKey: ['funcionalidades', 'sugestoes', id],
    queryFn: () => chamarApi<Sugestao[]>(`/backend/monitoria/falta/${id}/sugestao`),
    enabled: !!id,
    staleTime: 0,
  });
export const consultaContagem = (id: string) =>
  queryOptions({
    queryKey: ['funcionalidades', 'contagem', id],
    queryFn: () => chamarApi<number>(`/backend/monitoria/alerta/${id}/contagem`),
  });
