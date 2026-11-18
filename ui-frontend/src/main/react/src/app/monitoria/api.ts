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


export interface RespostaPesquisaMonitoria<T> {
  resultado: {
    projecoes: T[];
    total: number;
  };
}

export interface DisciplinaProfessor {
  identificador: string;
  codigo: string;
  nome: string;
  cursoNome: string;
}

export type SelecaoSituacao = 'ABERTA' | 'ENCERRADA';

export interface SelecaoProjecao {
  identificador: string;
  disciplina: string;
  disciplinaNome: string;
  disciplinaCodigo: string;
  cursoNome: string;
  professor: string;
  professorNome: string;
  semestre: string;
  prazoInscricao: string;
  vagas: number;
  crMinimo: number;
  pesoCr: number;
  pesoEntrevista: number;
  patamarMinimo: number;
  situacao: SelecaoSituacao;
  motivoEncerramento: string | null;
  abertaEm: string;
  encerradaEm: string | null;
  totalCandidaturas: number;
  pendentes: number;
  aprovadas: number;
  monitoresSelecionados: string[];
}

export type CandidaturaSituacao = 'PENDENTE' | 'APROVADA' | 'REPROVADA' | 'CANCELADA';

export interface CandidaturaProjecao {
  identificador: string;
  selecao: string;
  disciplina: string;
  disciplinaNome: string;
  semestre: string;
  aluno: string;
  alunoNome: string;
  cr: number;
  situacao: CandidaturaSituacao;
  notaEntrevista: number | null;
  comentarioEntrevista: string | null;
  resultadoFinal: number | null;
  motivoDecisao: string | null;
  criadaEm: string;
  decididaEm: string | null;
}

export interface SelecaoAberta {
  selecao: SelecaoProjecao;
  elegivel: boolean;
  cursoCompativel: boolean;
  limiteDisponivel: boolean;
  monitoriasNoSemestre: number;
  semCandidaturaAtiva: boolean;
  situacaoCandidaturaAtiva: string | null;
  dentroDoPrazo: boolean;
  minhaCandidatura: CandidaturaProjecao | null;
}

export type AcompanhamentoSituacao = 'ATIVO' | 'ENCERRADO';
export type TendenciaEvolucao = 'EM_EVOLUCAO' | 'EM_QUEDA' | 'ESTAVEL' | 'DADOS_INSUFICIENTES';
export type SituacaoDesempenho =
  | 'META_ATINGIDA'
  | 'EM_PROGRESSO'
  | 'ESTAVEL'
  | 'ATENCAO'
  | 'DADOS_INSUFICIENTES';

export interface AcompanhamentoProjecao {
  identificador: string;
  aluno: string;
  alunoNome: string;
  disciplina: string;
  disciplinaNome: string;
  monitor: string;
  monitorNome: string;
  professor: string;
  professorNome: string;
  semestre: string;
  metaNota: number;
  metaFrequencia: number;
  situacao: AcompanhamentoSituacao;
  abertoEm: string;
  encerradoEm: string | null;
  parecerFinal: string | null;
  possuiFeedback: boolean;
  frequenciaPercentual: number | null;
  notaInicial: number | null;
  notaAtual: number | null;
  tendencia: TendenciaEvolucao;
  situacaoDesempenho: SituacaoDesempenho;
}

export interface AlunoDisciplinaOpcao {
  aluno: string;
  alunoNome: string;
  disciplina: string;
  disciplinaNome: string;
  cursoNome: string;
}

export interface SessaoRelatorioItem {
  sessao: string;
  data: string;
  situacao: string;
  valida: boolean;
  presente: boolean;
  nota: number | null;
  comentario: string | null;
}

export interface RelatorioDesempenho {
  acompanhamento: string;
  de: string | null;
  ate: string | null;
  filtroIgnorado: boolean;
  totalSessoesValidas: number;
  totalPresencas: number;
  frequenciaPercentual: number | null;
  quantidadeNotas: number;
  mediaNotas: number | null;
  notaInicial: number | null;
  notaAtual: number | null;
  variacaoNotas: number | null;
  inclinacaoTendencia: number | null;
  tendencia: TendenciaEvolucao;
  situacao: SituacaoDesempenho;
  sessoes: SessaoRelatorioItem[];
}

export interface FeedbackProjecao {
  identificador: string;
  sessao: string;
  sessaoInicio: string;
  disciplinaNome: string;
  aluno: string;
  alunoNome: string;
  autor: string;
  nota: number;
  comentario: string | null;
  criadoEm: string;
  atualizadoEm: string | null;
  bloqueado: boolean;
}

function queryString(params: Record<string, string | number | undefined>) {
  const sp = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== '') sp.set(k, String(v));
  }
  const q = sp.toString();
  return q ? `?${q}` : '';
}

export const consultaSelecoes = (texto = '', pagina = 0) =>
  queryOptions({
    queryKey: ['funcionalidades', 'monitoria', 'selecao', 'pesquisa', texto, pagina],
    queryFn: () =>
      chamarApi<RespostaPesquisaMonitoria<SelecaoProjecao>>(
        `/backend/monitoria/selecao/pesquisa${queryString({
          texto: texto.trim() || undefined,
          indicePagina: pagina,
          tamanhoPagina: 100,
        })}`,
      ),
  });

export const consultaDisciplinasProfessor = queryOptions({
  queryKey: ['funcionalidades', 'monitoria', 'selecao', 'disciplinas'],
  queryFn: () => chamarApi<DisciplinaProfessor[]>('/backend/monitoria/selecao/disciplinas'),
});

export const consultaCandidaturas = (texto = '', selecao = '', pagina = 0) =>
  queryOptions({
    queryKey: ['funcionalidades', 'monitoria', 'candidatura', 'pesquisa', texto, selecao, pagina],
    queryFn: () =>
      chamarApi<RespostaPesquisaMonitoria<CandidaturaProjecao>>(
        `/backend/monitoria/candidatura/pesquisa${queryString({
          texto: texto.trim() || undefined,
          selecao: selecao || undefined,
          indicePagina: pagina,
          tamanhoPagina: 100,
        })}`,
      ),
  });

export const consultaSelecoesAbertas = queryOptions({
  queryKey: ['funcionalidades', 'monitoria', 'candidatura', 'abertas'],
  queryFn: () => chamarApi<SelecaoAberta[]>('/backend/monitoria/candidatura/abertas'),
});

export const consultaAcompanhamentos = (texto = '', pagina = 0) =>
  queryOptions({
    queryKey: ['funcionalidades', 'monitoria', 'acompanhamento', 'pesquisa', texto, pagina],
    queryFn: () =>
      chamarApi<RespostaPesquisaMonitoria<AcompanhamentoProjecao>>(
        `/backend/monitoria/acompanhamento/pesquisa${queryString({
          texto: texto.trim() || undefined,
          indicePagina: pagina,
          tamanhoPagina: 100,
        })}`,
      ),
  });

export const consultaOpcoesAcompanhamento = queryOptions({
  queryKey: ['funcionalidades', 'monitoria', 'acompanhamento', 'opcoes'],
  queryFn: () =>
    chamarApi<AlunoDisciplinaOpcao[]>('/backend/monitoria/acompanhamento/opcoes'),
});

export const consultaRelatorio = (identificador: string, de = '', ate = '') =>
  queryOptions({
    queryKey: ['funcionalidades', 'monitoria', 'acompanhamento', 'relatorio', identificador, de, ate],
    queryFn: () =>
      chamarApi<RelatorioDesempenho>(
        `/backend/monitoria/acompanhamento/${identificador}/relatorio${queryString({ de, ate })}`,
      ),
  });

export const consultaFeedbacks = (texto = '', pagina = 0) =>
  queryOptions({
    queryKey: ['funcionalidades', 'monitoria', 'feedback', 'pesquisa', texto, pagina],
    queryFn: () =>
      chamarApi<RespostaPesquisaMonitoria<FeedbackProjecao>>(
        `/backend/monitoria/feedback/pesquisa${queryString({
          texto: texto.trim() || undefined,
          indicePagina: pagina,
          tamanhoPagina: 100,
        })}`,
      ),
  });


export interface AutorizacoesSelecao {
  incluir?: boolean;
  buscar: boolean;
  alterar?: boolean;
  excluir?: boolean;
  abrir: boolean;
  editar: boolean;
  encerrar: boolean;
}

export interface AutorizacoesCandidatura {
  incluir?: boolean;
  buscar: boolean;
  alterar?: boolean;
  excluir?: boolean;
  candidatar: boolean;
  cancelar: boolean;
  registrarEntrevista: boolean;
  aprovar: boolean;
  reprovar: boolean;
}

export interface AutorizacoesAcompanhamento {
  incluir?: boolean;
  buscar: boolean;
  alterar?: boolean;
  excluir?: boolean;
  abrir: boolean;
  editarMetas: boolean;
  encerrar: boolean;
  relatorio: boolean;
}

export interface AutorizacoesFeedback {
  incluir?: boolean;
  buscar: boolean;
  alterar?: boolean;
  excluir?: boolean;
  registrar: boolean;
  editar: boolean;
}
