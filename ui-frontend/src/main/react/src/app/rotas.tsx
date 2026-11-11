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
        lazy: async () => ({ Component: (await import('./convite/TenhoUmConvite')).TenhoUmConvite }),
      },
      {
        // Adicione aqui as rotas de cada agregado, dentro desta rota protegida.
        Component: RotaProtegida,
        // Erros dos loaders das rotas filhas, exibidos dentro do layout, com o menu
        ErrorBoundary: ErroRota,
        loader: exigirAutenticacao,
        children: [
          { index: true, element: <Navigate to="/inicio" replace /> },
          {
            path: 'inicio',
            lazy: async () => ({ Component: (await import('./inicio/Inicio')).Inicio }),
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
        ],
      },
    ],
  },
];

export const roteador = createBrowserRouter(rotas);
