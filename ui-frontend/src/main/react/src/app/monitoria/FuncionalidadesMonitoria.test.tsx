import { QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Suspense } from 'react';
import { MemoryRouter } from 'react-router';
import { clienteConsultas, NotificacoesProvider } from '../../d5';
import { consultaAutenticacao, type Usuario } from '../../d5/autenticacao';
import { Acompanhamentos } from './acompanhamento/Acompanhamentos';
import { RelatoriosDeDesempenho } from './acompanhamento/RelatoriosDeDesempenho';
import { AvaliarCandidaturas } from './candidatura/AvaliarCandidaturas';
import { SelecoesDeMonitoria } from './candidatura/SelecoesDeMonitoria';
import { FeedbackDosAlunos } from './feedback/FeedbackDosAlunos';
import { MinhasSelecoes } from './selecao/MinhasSelecoes';

function configurarAuth(papel: 'PROFESSOR' | 'ALUNO' | 'COORDENADOR', monitor = false) {
  clienteConsultas.setQueryData<Usuario | null>(
    consultaAutenticacao.queryKey,
    () =>
      ({
        usuario: 'usuario@cem.edu.br',
        autorizacoes: [papel],
        selecao: {
          incluir: false,
          buscar: true,
          alterar: false,
          excluir: false,
          abrir: papel === 'PROFESSOR',
          editar: papel === 'PROFESSOR',
          encerrar: papel === 'PROFESSOR',
        },
        candidatura: {
          incluir: false,
          buscar: papel !== 'COORDENADOR',
          alterar: false,
          excluir: false,
          candidatar: papel === 'ALUNO',
          cancelar: papel === 'ALUNO',
          registrarEntrevista: papel === 'PROFESSOR',
          aprovar: papel === 'PROFESSOR',
          reprovar: papel === 'PROFESSOR',
        },
        acompanhamento: {
          incluir: false,
          buscar: true,
          alterar: false,
          excluir: papel === 'PROFESSOR' || monitor,
          abrir: monitor,
          editarMetas: papel === 'PROFESSOR' || monitor,
          encerrar: papel === 'PROFESSOR' || monitor,
          relatorio: true,
        },
        feedback: {
          incluir: false,
          buscar: true,
          alterar: false,
          excluir: monitor,
          registrar: monitor,
          editar: monitor,
        },
      }) as Usuario,
  );
}

function montar(componente: React.ReactNode, rota = '/') {
  render(
    <QueryClientProvider client={clienteConsultas}>
      <MemoryRouter initialEntries={[rota]}>
        <NotificacoesProvider>
          <Suspense fallback="Carregando">{componente}</Suspense>
        </NotificacoesProvider>
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

afterEach(() => {
  clienteConsultas.clear();
  vi.unstubAllGlobals();
});

it('permite ao professor abrir seleção e exibe seleções existentes', async () => {
  configurarAuth('PROFESSOR');
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    if (url.includes('/selecao/disciplinas')) {
      return Response.json([
        {
          identificador: '12',
          codigo: 'MAT1001',
          nome: 'Cálculo I',
          cursoNome: 'Ciência da Computação',
        },
      ]);
    }
    if (url.includes('/selecao/pesquisa')) {
      return Response.json({
        resultado: {
          projecoes: [
            {
              identificador: '1',
              disciplina: '12',
              disciplinaNome: 'Cálculo I',
              disciplinaCodigo: 'MAT1001',
              cursoNome: 'Ciência da Computação',
              professor: '3',
              professorNome: 'Helena',
              semestre: '2026.1',
              prazoInscricao: '2026-04-30',
              vagas: 2,
              crMinimo: 7.0,
              pesoCr: 40,
              pesoEntrevista: 60,
              patamarMinimo: 7.0,
              situacao: 'ABERTA',
              motivoEncerramento: null,
              abertaEm: '2026-03-01',
              encerradaEm: null,
              totalCandidaturas: 1,
              pendentes: 1,
              aprovadas: 0,
              monitoresSelecionados: [],
            },
          ],
          total: 1,
        },
      });
    }
    return Response.json({});
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<MinhasSelecoes />);

  expect(await screen.findByText('MAT1001 · Cálculo I')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Editar critérios' })).toBeDisabled();

  await userEvent.click(screen.getByRole('button', { name: 'Abrir seleção' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/selecao/abrir',
      expect.objectContaining({ method: 'POST' }),
    ),
  );
});

it('permite ao aluno verificar elegibilidade e candidatar-se ou cancelar', async () => {
  configurarAuth('ALUNO');
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    if (url.includes('/candidatura/abertas')) {
      return Response.json([
        {
          selecao: {
            identificador: '5',
            disciplina: '12',
            disciplinaNome: 'Cálculo I',
            disciplinaCodigo: 'MAT1001',
            cursoNome: 'Computação',
            professor: '3',
            professorNome: 'Helena',
            semestre: '2026.1',
            prazoInscricao: '2026-04-30',
            vagas: 2,
            crMinimo: 7.0,
            pesoCr: 40,
            pesoEntrevista: 60,
            patamarMinimo: 7.0,
            situacao: 'ABERTA',
            motivoEncerramento: null,
            abertaEm: '2026-03-01',
            encerradaEm: null,
            totalCandidaturas: 0,
            pendentes: 0,
            aprovadas: 0,
            monitoresSelecionados: [],
          },
          elegivel: true,
          cursoCompativel: true,
          limiteDisponivel: true,
          monitoriasNoSemestre: 0,
          semCandidaturaAtiva: true,
          situacaoCandidaturaAtiva: null,
          dentroDoPrazo: true,
          minhaCandidatura: null,
        },
      ]);
    }
    return Response.json({
      resultado: {
        projecoes: [
          {
            identificador: '10',
            selecao: '4',
            disciplina: '11',
            disciplinaNome: 'Algoritmos',
            semestre: '2026.1',
            aluno: '20',
            alunoNome: 'Aluno Teste',
            cr: 8.5,
            situacao: 'PENDENTE',
            notaEntrevista: null,
            comentarioEntrevista: null,
            resultadoFinal: null,
            motivoDecisao: null,
            criadaEm: '2026-03-02T10:00:00',
            decididaEm: null,
          },
        ],
        total: 1,
      },
    });
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<SelecoesDeMonitoria />);

  expect(await screen.findByText('MAT1001 · Cálculo I')).toBeInTheDocument();
  expect(screen.getByText('✓ Curso compatível (Computação)')).toBeInTheDocument();

  await userEvent.clear(screen.getByLabelText('Seu CR (0 a 10) *'));
  await userEvent.type(screen.getByLabelText('Seu CR (0 a 10) *'), '8.4');
  await userEvent.click(screen.getByRole('button', { name: 'Candidatar-se' }));

  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/candidatura/candidatar',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ selecao: '5', cr: 8.4 }),
      }),
    ),
  );

  await userEvent.click(screen.getByRole('button', { name: 'Cancelar' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/candidatura/10/cancelar',
      expect.objectContaining({ method: 'POST' }),
    ),
  );
});

it('permite ao professor registrar entrevista e aprovar candidato', async () => {
  configurarAuth('PROFESSOR');
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    if (url.includes('/selecao/pesquisa')) {
      return Response.json({
        resultado: {
          projecoes: [
            {
              identificador: '5',
              disciplina: '12',
              disciplinaNome: 'Cálculo I',
              disciplinaCodigo: 'MAT1001',
              cursoNome: 'Computação',
              professor: '3',
              professorNome: 'Helena',
              semestre: '2026.1',
              prazoInscricao: '2026-03-20',
              vagas: 2,
              crMinimo: 7.0,
              pesoCr: 40,
              pesoEntrevista: 60,
              patamarMinimo: 7.0,
              situacao: 'ABERTA',
              motivoEncerramento: null,
              abertaEm: '2026-03-01',
              encerradaEm: null,
              totalCandidaturas: 1,
              pendentes: 1,
              aprovadas: 0,
              monitoresSelecionados: [],
            },
          ],
          total: 1,
        },
      });
    }
    return Response.json({
      resultado: {
        projecoes: [
          {
            identificador: '10',
            selecao: '5',
            disciplina: '12',
            disciplinaNome: 'Cálculo I',
            semestre: '2026.1',
            aluno: '20',
            alunoNome: 'Mariana',
            cr: 8.5,
            situacao: 'PENDENTE',
            notaEntrevista: 9.0,
            comentarioEntrevista: null,
            resultadoFinal: 8.8,
            motivoDecisao: null,
            criadaEm: '2026-03-02T10:00:00',
            decididaEm: null,
          },
        ],
        total: 1,
      },
    });
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<AvaliarCandidaturas />, '/monitoria/candidatura/avaliacao?selecao=5');

  expect(await screen.findByText('Mariana')).toBeInTheDocument();
  await userEvent.click(screen.getByRole('button', { name: 'Aprovar' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/candidatura/10/aprovar',
      expect.objectContaining({ method: 'POST' }),
    ),
  );
});

it('gerencia acompanhamentos e feedbacks bloqueando ações inválidas', async () => {
  configurarAuth('ALUNO', true);
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    if (url.includes('/acompanhamento/opcoes')) {
      return Response.json([
        {
          aluno: '21',
          alunoNome: 'Lucas Almeida',
          disciplina: '12',
          disciplinaNome: 'Cálculo I',
          cursoNome: 'Computação',
        },
      ]);
    }
    if (url.includes('/acompanhamento/pesquisa')) {
      return Response.json({
        resultado: {
          projecoes: [
            {
              identificador: '1',
              aluno: '20',
              alunoNome: 'Ana Silva',
              disciplina: '12',
              disciplinaNome: 'Cálculo I',
              monitor: '9',
              monitorNome: 'Carlos',
              professor: '3',
              professorNome: 'Helena',
              semestre: '2026.1',
              metaNota: 7.5,
              metaFrequencia: 75,
              situacao: 'ATIVO',
              abertoEm: '2026-03-10',
              encerradoEm: null,
              parecerFinal: null,
              possuiFeedback: true,
              frequenciaPercentual: 100.0,
              notaInicial: 5.25,
              notaAtual: 8.25,
              tendencia: 'EM_EVOLUCAO',
              situacaoDesempenho: 'META_ATINGIDA',
            },
          ],
          total: 1,
        },
      });
    }
    if (url.includes('/feedback/pesquisa')) {
      return Response.json({
        resultado: {
          projecoes: [
            {
              identificador: '30',
              sessao: '44',
              sessaoInicio: '2026-04-10T14:00:00',
              disciplinaNome: 'Cálculo I',
              aluno: '20',
              alunoNome: 'Ana Silva',
              autor: '9',
              nota: 8.5,
              comentario: 'Bom avanço',
              criadoEm: '2026-04-10',
              atualizadoEm: null,
              bloqueado: true,
            },
          ],
          total: 1,
        },
      });
    }
    if (url.includes('/sessao/pesquisa')) {
      return Response.json({ resultado: { projecoes: [], total: 0 } });
    }
    if (url.includes('/sessao/contexto')) {
      return Response.json({ usuario: '9', pessoas: [], campus: [], curso: 'Computação' });
    }
    return Response.json({});
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<Acompanhamentos />);

  expect(await screen.findByText('Ana Silva · Cálculo I')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Excluir' })).toBeDisabled();

  await userEvent.click(screen.getByRole('button', { name: 'Abrir acompanhamento' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/acompanhamento/abrir',
      expect.objectContaining({ method: 'POST' }),
    ),
  );

  montar(<FeedbackDosAlunos />);
  expect(await screen.findByText('Acompanhamento encerrado')).toBeInTheDocument();
  const botoesEditar = screen.getAllByRole('button', { name: 'Editar' });
  expect(botoesEditar[0]).toBeDisabled();
});

it('exibe KPIs, gráficos SVG e aciona exportação em PDF no relatório', async () => {
  configurarAuth('PROFESSOR');
  const printMock = vi.fn();
  vi.stubGlobal('print', printMock);

  const fetchMock = vi.fn(async (url: string) => {
    if (url.includes('/acompanhamento/1/relatorio')) {
      return Response.json({
        acompanhamento: '1',
        de: null,
        ate: null,
        filtroIgnorado: false,
        totalSessoesValidas: 4,
        totalPresencas: 4,
        frequenciaPercentual: 100.0,
        quantidadeNotas: 4,
        mediaNotas: 6.75,
        notaInicial: 5.25,
        notaAtual: 8.25,
        variacaoNotas: 3.0,
        inclinacaoTendencia: 1.2,
        tendencia: 'EM_EVOLUCAO',
        situacao: 'META_ATINGIDA',
        sessoes: [
          {
            sessao: '1',
            data: '2026-04-01',
            situacao: 'PRESENTE',
            valida: true,
            presente: true,
            nota: 5.0,
            comentario: 'Início',
          },
          {
            sessao: '2',
            data: '2026-04-08',
            situacao: 'PRESENTE',
            valida: true,
            presente: true,
            nota: 5.5,
            comentario: 'Progresso',
          },
          {
            sessao: '3',
            data: '2026-04-15',
            situacao: 'PRESENTE',
            valida: true,
            presente: true,
            nota: 8.0,
            comentario: 'Ótimo',
          },
          {
            sessao: '4',
            data: '2026-04-22',
            situacao: 'PRESENTE',
            valida: true,
            presente: true,
            nota: 8.5,
            comentario: 'Dominou',
          },
        ],
      });
    }
    return Response.json({
      resultado: {
        projecoes: [
          {
            identificador: '1',
            aluno: '20',
            alunoNome: 'Ana Silva',
            disciplina: '12',
            disciplinaNome: 'Cálculo I',
            monitor: '9',
            monitorNome: 'Carlos',
            professor: '3',
            professorNome: 'Helena',
            semestre: '2026.1',
            metaNota: 7.5,
            metaFrequencia: 75,
            situacao: 'ATIVO',
            abertoEm: '2026-03-10',
            encerradoEm: null,
            parecerFinal: null,
            possuiFeedback: true,
            frequenciaPercentual: 100.0,
            notaInicial: 5.25,
            notaAtual: 8.25,
            tendencia: 'EM_EVOLUCAO',
            situacaoDesempenho: 'META_ATINGIDA',
          },
        ],
        total: 1,
      },
    });
  });
  vi.stubGlobal('fetch', fetchMock);

  montar(<RelatoriosDeDesempenho />, '/monitoria/acompanhamento/relatorios?id=1');

  expect(await screen.findByText('Meta atingida')).toBeInTheDocument();
  expect(screen.getByText('100.0%')).toBeInTheDocument();
  expect(screen.getByText('5.25 → 8.25')).toBeInTheDocument();
  expect(screen.getByText('+3.00')).toBeInTheDocument();
  expect(screen.getByRole('img', { name: 'Gráfico de evolução das notas' })).toBeInTheDocument();
  expect(screen.getByRole('img', { name: 'Comparativo nota inicial e atual' })).toBeInTheDocument();
  expect(screen.getByRole('img', { name: 'Gráfico de frequência acumulada' })).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Exportar PDF' }));
  expect(printMock).toHaveBeenCalledTimes(1);
});
