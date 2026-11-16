import { createBrowserRouter, Navigate } from 'react-router';
import { exigirAutenticacao, RotaProtegida } from '../d5';
import { ErroRota } from '../d5/bootstrap/ErroRota';
import { App } from './App';

export const rotas = [
  {
    path: '/',
    Component: App,
    // Caminhos que não correspondem a nenhuma rota
    ErrorBoundary: ErroRota,
    children: [
      {
        path: 'login',
        lazy: async () => ({ Component: (await import('./login/Login')).Login }),
      },
      {
        path: 'criar-conta',
        lazy: async () => ({ Component: (await import('./criar-conta/CriarConta')).CriarConta }),
      },
      {
        path: 'convite',
        lazy: async () => ({
          Component: (await import('./convite/TenhoUmConvite')).TenhoUmConvite,
        }),
      },
      {
        // Adicione aqui as rotas de cada agregado, dentro desta rota protegida.
        Component: RotaProtegida,
        // Erros dos loaders das rotas filhas, exibidos dentro do layout, com o menu
        ErrorBoundary: ErroRota,
        loader: exigirAutenticacao,
        children: [
          {
            path: 'comunicacao/faq/pesquisa',
            lazy: async () => {
              const modulo = await import('./comunicacao/faq/Faq');
              return { Component: modulo.Faq, loader: modulo.loader };
            },
          },
          {
            path: 'comunicacao/aviso/pesquisa',
            lazy: async () => {
              const modulo = await import('./comunicacao/aviso/MuralDeAvisos');
              return { Component: modulo.MuralDeAvisos, loader: modulo.loader };
            },
          },
          {
            path: 'monitoria/falta/pesquisa',
            lazy: async () => {
              const modulo = await import('./monitoria/falta/FaltasEReposicao');
              return { Component: modulo.FaltasEReposicao, loader: modulo.loader };
            },
          },
          {
            path: 'monitoria/alerta/pesquisa',
            lazy: async () => {
              const modulo = await import('./monitoria/alerta/AlertasDeFaltas');
              return { Component: modulo.AlertasDeFaltas, loader: modulo.loader };
            },
          },
          {
            path: 'monitoria/sessao/pesquisa',
            lazy: async () => {
              const modulo = await import('./monitoria/sessao/AgendaDeReposicao');
              return { Component: modulo.AgendaDeReposicao, loader: modulo.loader };
            },
          },
          { index: true, element: <Navigate to="/inicio" replace /> },
          {
            path: 'inicio',
            lazy: async () => {
              const modulo = await import('./inicio/Inicio');
              return { Component: modulo.Inicio, loader: modulo.loader };
            },
          },
          {
            path: 'acesso/coordenadores',
            lazy: async () => {
              const modulo = await import('./acesso/coordenadores/Coordenadores');
              return { Component: modulo.Coordenadores, loader: modulo.loader };
            },
          },
          {
            path: 'acesso/usuarios',
            lazy: async () => {
              const modulo = await import('./acesso/usuarios/UsuariosEAcessos');
              return { Component: modulo.UsuariosEAcessos, loader: modulo.loader };
            },
          },
          {
            path: 'acesso/curso/pesquisa',
            lazy: async () => {
              const modulo = await import('./acesso/cursos/Cursos');
              return { Component: modulo.Cursos, loader: modulo.loader };
            },
          },
          {
            path: 'acesso/disciplina/pesquisa',
            lazy: async () => {
              const modulo = await import('./acesso/cursos/Disciplinas');
              return { Component: modulo.Disciplinas, loader: modulo.loader };
            },
          },
        ],
      },
    ],
  },
];

export const roteador = createBrowserRouter(rotas);
