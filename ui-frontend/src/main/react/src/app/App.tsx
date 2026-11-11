import { Link, Outlet, useLocation, useNavigate } from 'react-router';
import { NotificacoesProvider, sair, useAutenticacao } from '../d5';
import { Notificacoes } from '../d5/bootstrap/Notificacoes';
import { ReautenticacaoProvider } from '../d5/bootstrap/ReautenticacaoProvider';

function ItemMenu({ caminho, children }: { caminho: string; children: string }) {
  const { pathname } = useLocation();
  const ativo = pathname.startsWith(caminho);
  return (
    <Link className={`nav-item${ativo ? ' active' : ''}`} to={caminho}>
      <span className="nav-label">{children}</span>
    </Link>
  );
}

function iniciais(email: string): string {
  return email.slice(0, 2).toUpperCase();
}

export function App() {
  const { autenticado, usuario, autorizacoes } = useAutenticacao();
  const navegar = useNavigate();

  async function aoSair() {
    await sair();
    navegar('/login');
  }

  return (
    <NotificacoesProvider>
      <ReautenticacaoProvider>
        <div className="app-shell">
          <aside className="sidebar" aria-label="Menu lateral">
            <Link to="/" className="brand">
              <span className="brand-mark" aria-hidden="true">
                C<span>✦</span>
              </span>
              <span>
                <strong>CEM</strong>
                <small>Monitoria &amp; Mentoria</small>
              </span>
            </Link>

            <nav id="main-nav" aria-label="Telas do sistema">
              {!autenticado && (
                <>
                  <div className="nav-group">Acesso</div>
                  <ItemMenu caminho="/login">Entrar</ItemMenu>
                  <ItemMenu caminho="/criar-conta">Criar conta de aluno</ItemMenu>
                  <ItemMenu caminho="/convite">Tenho um convite</ItemMenu>
                </>
              )}

              {autenticado && autorizacoes.includes('ADMINISTRADOR') && (
                <>
                  <div className="nav-group">Área do administrador</div>
                  <ItemMenu caminho="/acesso/coordenadores">Coordenadores</ItemMenu>
                </>
              )}

              {autenticado && autorizacoes.includes('COORDENADOR') && (
                <>
                  <div className="nav-group">Área do coordenador</div>
                  <ItemMenu caminho="/acesso/usuarios">Usuários e acessos</ItemMenu>
                </>
              )}

              {autenticado && (
                <>
                  <div className="nav-group">Geral</div>
                  <ItemMenu caminho="/inicio">Início</ItemMenu>
                </>
              )}
            </nav>

            <div className="sidebar-bottom">CEM · Central de Monitoria e Mentoria</div>
          </aside>

          <div className="main-panel">
            <header className="topbar">
              <div className="topbar-context">
                <strong>CEM</strong>
              </div>
              <div className="topbar-right">
                {autenticado ? (
                  <>
                    <div className="user-chip" title="Usuário logado">
                      <span className="avatar">{iniciais(usuario ?? '')}</span>
                      <span className="user-meta">
                        <strong>{usuario}</strong>
                      </span>
                    </div>
                    <button className="button button-ghost button-small" type="button" onClick={aoSair}>
                      Sair
                    </button>
                  </>
                ) : (
                  <Link to="/login" className="button button-secondary button-small">
                    Entrar
                  </Link>
                )}
              </div>
            </header>
            <Notificacoes />
            <main>
              <Outlet />
            </main>
            <footer className="site-footer">
              <span>CEM · Central de Monitoria e Mentoria</span>
            </footer>
          </div>
        </div>
      </ReautenticacaoProvider>
    </NotificacoesProvider>
  );
}
