import { queryOptions, useMutation } from '@tanstack/react-query';
import { chamarApi, clienteConsultas, mensagemErro, useNotificacoes } from '../../d5';
import type { IAgregadoAutorizacoes } from '../../d5';

export interface AvisoAutorizacoes extends IAgregadoAutorizacoes {
  publicar: boolean;
  alternar: boolean;
}

export interface Pesquisa<T> {
  resultado: { projecoes: T[]; total: number };
}
export interface Faq {
  identificador: string;
  pergunta: string;
  resposta: string;
  ativo: boolean;
}
export interface Pergunta {
  identificador: string;
  autor: string;
  texto: string;
  situacao: string;
  resposta: string | null;
  criadaEm: string;
}
export interface Aviso {
  identificador: string;
  titulo: string;
  texto: string;
  validade: string;
  ativo: boolean;
  publicadoEm: string;
}
export const pesquisa = <T>(sub: string, agregado: string, texto = '', pagina = 0) =>
  queryOptions({
    queryKey: ['funcionalidades', sub, agregado, texto, pagina],
    queryFn: async () => {
      const carregar = (indice: number) =>
        chamarApi<Pesquisa<T>>(
          `/backend/${sub}/${agregado}/pesquisa?texto=${encodeURIComponent(texto)}&indicePagina=${indice}&tamanhoPagina=100`,
        );
      const primeira = await carregar(pagina);
      if (pagina !== 0) return primeira;
      const projecoes = [...primeira.resultado.projecoes];
      for (let indice = 1; projecoes.length < primeira.resultado.total; indice++) {
        const proxima = await carregar(indice);
        if (!proxima.resultado.projecoes.length) break;
        projecoes.push(...proxima.resultado.projecoes);
      }
      return { resultado: { ...primeira.resultado, projecoes } };
    },
  });
export const consultaFaq = (texto = '') => pesquisa<Faq>('comunicacao', 'faq', texto);
export const consultaPerguntas = () => pesquisa<Pergunta>('comunicacao', 'pergunta');
export const consultaAvisos = (texto = '') => pesquisa<Aviso>('comunicacao', 'aviso', texto);
export function useAcao(aoConcluir?: () => void) {
  const { publicar } = useNotificacoes();
  return useMutation({
    mutationFn: ({ caminho, corpo }: { caminho: string; corpo?: unknown }) =>
      chamarApi(`/backend/${caminho}`, { method: 'POST', corpo }),
    onSuccess: async () => {
      await clienteConsultas.invalidateQueries({ queryKey: ['funcionalidades'] });
      aoConcluir?.();
      publicar('sucesso', 'Operação realizada.');
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });
}
export const data = (valor: string) =>
  new Date(valor.length === 10 ? `${valor}T12:00:00` : valor).toLocaleDateString('pt-BR');
export const hoje = () => {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};
