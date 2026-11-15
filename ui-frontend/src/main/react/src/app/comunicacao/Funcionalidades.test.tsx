import { Suspense } from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { clienteConsultas, NotificacoesProvider } from '../../d5';
import { consultaAutenticacao, type Usuario } from '../../d5/autenticacao';
import { Faq } from './faq/Faq';
import { MuralDeAvisos } from './aviso/MuralDeAvisos';

function preparar(coordenador: boolean) {
  clienteConsultas.setQueryData<Usuario | null>(consultaAutenticacao.queryKey, () => ({
    usuario: 'pessoa@cem.edu.br',
    autorizacoes: [coordenador ? 'COORDENADOR' : 'ALUNO'],
    faq: { incluir: coordenador, buscar: true, alterar: coordenador, excluir: coordenador },
    aviso: {
      incluir: false,
      buscar: true,
      alterar: coordenador,
      excluir: false,
      publicar: coordenador,
      alternar: coordenador,
    },
  }));
  const fetchMock = vi.fn(async (url: string, opcoes?: RequestInit) => {
    if (opcoes?.method === 'POST') return new Response(null, { status: 204 });
    const projecoes = url.includes('/faq/')
      ? [
          {
            identificador: '1',
            pergunta: 'Como participar?',
            resposta: 'Consulte as sessões.',
            ativo: true,
          },
        ]
      : [];
    return Response.json({ resultado: { projecoes, total: projecoes.length } });
  });
  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

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

it('permite ao aluno consultar o FAQ e enviar dúvida sem controles de gestão', async () => {
  const fetchMock = preparar(false);
  montar(<Faq />);
  expect(await screen.findByText('Como participar?')).toBeInTheDocument();
  expect(screen.queryByRole('button', { name: 'Cadastrar' })).not.toBeInTheDocument();
  await userEvent.type(screen.getByLabelText('Sua pergunta *'), 'Como confirmar minha presença?');
  await userEvent.click(screen.getByRole('button', { name: 'Enviar pergunta' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/comunicacao/pergunta/enviar',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ texto: 'Como confirmar minha presença?' }),
      }),
    ),
  );
});

it('permite à coordenação cadastrar pergunta e resposta com situação ativa', async () => {
  const fetchMock = preparar(true);
  montar(<Faq />);
  await screen.findByRole('heading', { name: 'Gerenciar FAQ' });
  await userEvent.type(screen.getByLabelText('Pergunta *'), 'Onde ocorre a monitoria?');
  await userEvent.type(screen.getByLabelText('Resposta *'), 'No Campus Central.');
  await userEvent.click(screen.getByRole('button', { name: 'Cadastrar' }));
  await waitFor(() =>
    expect(fetchMock).toHaveBeenCalledWith(
      '/backend/comunicacao/faq/incluir',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          pergunta: 'Onde ocorre a monitoria?',
          resposta: 'No Campus Central.',
          ativo: true,
          identificador: null,
        }),
      }),
    ),
  );
});

it('exibe mural ao aluno sem permitir publicação', async () => {
  preparar(false);
  montar(<MuralDeAvisos />);
  expect(await screen.findByRole('heading', { name: 'Mural de avisos' })).toBeInTheDocument();
  expect(screen.queryByRole('button', { name: '+ Novo aviso' })).not.toBeInTheDocument();
  expect(screen.getByText('Nenhum aviso')).toBeInTheDocument();
});

it.each(['monitor', 'professor'])(
  'permite ao %s publicar sem exigir permissão de edição',
  async () => {
    const fetchMock = preparar(false);
    clienteConsultas.setQueryData<Usuario | null>(consultaAutenticacao.queryKey, (atual) =>
      atual
        ? {
            ...atual,
            aviso: {
              incluir: false,
              buscar: true,
              alterar: false,
              excluir: false,
              publicar: true,
              alternar: false,
            },
          }
        : atual,
    );
    montar(<MuralDeAvisos />);
    await userEvent.click(await screen.findByRole('button', { name: '+ Novo aviso' }));
    await userEvent.type(screen.getByLabelText('Título *'), 'Encontro de monitoria');
    await userEvent.type(screen.getByLabelText('Texto *'), 'Aviso aos participantes.');
    await userEvent.click(screen.getByRole('button', { name: 'Publicar' }));
    await waitFor(() =>
      expect(fetchMock).toHaveBeenCalledWith(
        '/backend/comunicacao/aviso/publicar',
        expect.objectContaining({
          method: 'POST',
          body: expect.stringContaining('Encontro de monitoria'),
        }),
      ),
    );
  },
);
