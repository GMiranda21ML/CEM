import { Suspense } from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { clienteConsultas, NotificacoesProvider } from '../../../d5';
import { Cursos } from './Cursos';
import { Disciplinas } from './Disciplinas';

function montar(componente: React.ReactNode) {
  render(
    <QueryClientProvider client={clienteConsultas}>
      <MemoryRouter>
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

it('cadastra cursos e impede remover os que possuem vínculos', async () => {
  const fetchMock = vi.fn(async (_url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    return Response.json({
      resultado: {
        projecoes: [{ identificador: '1', nome: 'Sistemas de Informação', sigla: 'SI',
          quantidadeDisciplinas: 3, quantidadeUsuarios: 8 }],
        total: 1,
      },
    });
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<Cursos />);

  expect(await screen.findByText('Sistemas de Informação')).toBeInTheDocument();
  expect(screen.getByText('3')).toBeInTheDocument();
  expect(screen.getByText('8')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Remover' })).toBeDisabled();

  await userEvent.type(screen.getByLabelText('Nome *'), 'Engenharia Civil');
  await userEvent.type(screen.getByLabelText('Sigla *'), 'EC');
  await userEvent.click(screen.getByRole('button', { name: 'Cadastrar' }));
  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith(
    '/backend/acesso/curso/cadastrar',
    expect.objectContaining({ method: 'POST', body: JSON.stringify({ nome: 'Engenharia Civil', sigla: 'EC' }) }),
  ));
});

it('cadastra disciplina selecionando curso e professor válidos', async () => {
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    if (url.includes('/disciplina/professores')) {
      return Response.json([{ identificador: '2', nome: 'Helena Duarte' }]);
    }
    if (url.includes('/disciplina/pesquisa')) {
      return Response.json({ resultado: { projecoes: [], total: 0 } });
    }
    return Response.json({ resultado: { projecoes: [
      { identificador: '1', nome: 'Computação', sigla: 'CC', quantidadeDisciplinas: 0, quantidadeUsuarios: 1 },
    ], total: 1 } });
  });
  vi.stubGlobal('fetch', fetchMock);
  montar(<Disciplinas />);

  await userEvent.type(await screen.findByLabelText('Nome *'), 'Estatística');
  await userEvent.type(screen.getByLabelText('Código *'), 'EST1001');
  await userEvent.type(screen.getByLabelText('Área de conhecimento *'), 'Matemática');
  await userEvent.selectOptions(screen.getByLabelText('Curso *'), '1');
  await userEvent.selectOptions(await screen.findByLabelText('Professor responsável *'), '2');
  await userEvent.click(screen.getByRole('button', { name: 'Cadastrar' }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledWith(
    '/backend/acesso/disciplina/cadastrar',
    expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ nome: 'Estatística', codigo: 'EST1001', area: 'Matemática', curso: '1', professor: '2' }),
    }),
  ));
});
