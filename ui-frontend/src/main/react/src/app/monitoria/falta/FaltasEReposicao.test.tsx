import { Suspense } from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { clienteConsultas, NotificacoesProvider } from '../../../d5';
import { FaltasEReposicao } from './FaltasEReposicao';
import type { Falta } from '../api';

const sessao = {
  identificador: '74',
  disciplina: 'Algoritmos e Programação',
  professor: '131',
  monitor: '132',
  campus: '87',
  sala: 'Laboratório 101',
  alunos: ['133'],
  inicio: '2020-11-17T10:00:00',
  fim: '2020-11-17T11:00:00',
  cancelada: false,
};
const falta: Falta = {
  identificador: '9',
  sessao: '74',
  pessoa: '133',
  papel: 'ALUNO',
  autor: '132',
  observacao: null,
  registradaEm: '2020-11-18T09:00:00',
  reposicao: null,
};

function preparar(registradas: Falta[]) {
  let faltas = [...registradas];
  const fetchMock = vi.fn(async (endereco: string, opcoes?: RequestInit) => {
    const url = new URL(endereco, 'http://localhost');
    if (opcoes?.method === 'POST') {
      if (url.pathname.endsWith('/registrar')) faltas = [falta];
      return new Response(null, { status: 204 });
    }
    if (url.pathname.endsWith('/contexto'))
      return Response.json({
        usuario: '132',
        campus: [{ identificador: '87', nome: 'Campus Central' }],
        pessoas: [
          { identificador: '132', nome: 'Monitor Teste', papel: 'ALUNO' },
          { identificador: '133', nome: 'Aluno Teste', papel: 'ALUNO' },
        ],
      });
    if (url.pathname.endsWith('/alteracao')) return Response.json({ agregado: sessao });
    if (url.pathname.endsWith('/sugestao'))
      return Response.json([
        { inicio: '2030-11-21T14:00:00', fim: '2030-11-21T15:00:00', campus: '87' },
      ]);
    const projecoes = url.pathname.includes('/falta/')
      ? url.searchParams.has('texto')
        ? faltas.filter((f) => f.observacao !== null)
        : faltas
      : [sessao];
    return Response.json({ resultado: { projecoes, total: projecoes.length } });
  });
  vi.stubGlobal('fetch', fetchMock);
  render(
    <QueryClientProvider client={clienteConsultas}>
      <MemoryRouter>
        <NotificacoesProvider>
          <Suspense fallback="Carregando">
            <FaltasEReposicao />
          </Suspense>
        </NotificacoesProvider>
      </MemoryRouter>
    </QueryClientProvider>,
  );
  return fetchMock;
}

afterEach(() => {
  clienteConsultas.clear();
  vi.unstubAllGlobals();
});

it('mostra falta sem observação, seus participantes e a ação de reposição', async () => {
  preparar([falta]);
  const botao = await screen.findByRole('button', { name: 'Sugerir reposição' });
  const linha = botao.closest('tr')!;
  expect(within(linha).getByText('Aluno Teste')).toBeInTheDocument();
  expect(within(linha).getByText('Monitor Teste')).toBeInTheDocument();
  expect(within(linha).getByText('—')).toBeInTheDocument();
  expect(screen.queryByText('Nenhuma falta registrada')).not.toBeInTheDocument();
});

it('atualiza a tabela depois de registrar uma falta sem observação', async () => {
  const fetchMock = preparar([]);
  await screen.findByText('Nenhuma falta registrada');
  await waitFor(() =>
    expect(screen.getByRole('option', { name: 'Aluno: Aluno Teste' })).toBeInTheDocument(),
  );
  await userEvent.selectOptions(screen.getByLabelText('Quem faltou? *'), 'ALUNO|133');
  await userEvent.click(screen.getByRole('button', { name: 'Registrar falta' }));
  expect(await screen.findByRole('button', { name: 'Sugerir reposição' })).toBeInTheDocument();
  expect(fetchMock).toHaveBeenCalledWith(
    '/backend/monitoria/falta/registrar',
    expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({
        sessao: '74',
        pessoa: '133',
        papel: 'ALUNO',
        observacao: '',
      }),
    }),
  );
});

it('permite consultar e escolher uma sugestão a partir da falta registrada', async () => {
  const fetchMock = preparar([falta]);
  await userEvent.click(await screen.findByRole('button', { name: 'Sugerir reposição' }));
  await userEvent.click(await screen.findByRole('button', { name: /→ Agendar/ }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/monitoria/falta/9/agendar',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          inicio: '2030-11-21T14:00:00',
          fim: '2030-11-21T15:00:00',
          campus: '87',
        }),
      }),
    ),
  );
});
