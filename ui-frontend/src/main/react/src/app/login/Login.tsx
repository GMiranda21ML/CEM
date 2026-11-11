import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { entrar, mensagemErroAutenticacao, useNotificacoes } from '../../d5';

interface Credenciais {
  usuario: string;
  senha: string;
}

/** Só aceita retorno para um caminho da própria aplicação, nunca para outro site. */
function destinoDeRetorno(retorno: string | null): string {
  return retorno?.startsWith('/') && !retorno.startsWith('//') ? retorno : '/';
}

export function Login() {
  const navegar = useNavigate();
  const [parametros] = useSearchParams();
  const { publicar } = useNotificacoes();
  const {
    register,
    handleSubmit,
    formState: { isValid },
  } = useForm<Credenciais>({ mode: 'onChange' });

  const { mutate, isPending } = useMutation({
    mutationFn: ({ usuario, senha }: Credenciais) => entrar(usuario, senha),
    // replace evita que /login fique no histórico do navegador — senão um "voltar" retornaria
    // para a tela de login.
    onSuccess: () => navegar(destinoDeRetorno(parametros.get('retorno')), { replace: true }),
    onError: (erro) => publicar('erro', mensagemErroAutenticacao(erro), 0),
  });

  return (
    <div className="login-wrap">
      <section className="login-card">
        <div className="eyebrow">Central de Monitoria e Mentoria</div>
        <h1>Entrar no CEM</h1>
        <p className="muted">
          Use seu e-mail institucional. Cada perfil vê apenas a sua área: aluno, professor,
          coordenação ou administração.
        </p>
        <form className="form-grid" onSubmit={handleSubmit((credenciais) => mutate(credenciais))} noValidate>
          <div className="field full">
            <label htmlFor="usuario">E-mail institucional *</label>
            <input
              id="usuario"
              type="email"
              placeholder="nome@cem.edu.br"
              autoFocus
              {...register('usuario', { required: true })}
            />
          </div>
          <div className="field full">
            <label htmlFor="senha">Senha *</label>
            <input id="senha" type="password" {...register('senha', { required: true })} />
          </div>
          <div className="form-actions">
            <button className="button button-primary" type="submit" disabled={!isValid || isPending}>
              Entrar →
            </button>
          </div>
        </form>
        <div className="login-links">
          <Link className="login-link" to="/criar-conta">
            <b>Sou aluno e não tenho conta</b>
            <span>Crie sua conta com o e-mail da sua instituição</span>
          </Link>
          <Link className="login-link" to="/convite">
            <b>Recebi um convite</b>
            <span>Coordenadores e professores ativam a conta com o código enviado por e-mail</span>
          </Link>
        </div>
      </section>
      <section className="login-side">
        <h2>Como funciona o acesso</h2>
        <ol className="steps-list">
          <li>O super administrador convida o coordenador da instituição.</li>
          <li>O coordenador convida os professores e libera os domínios de e-mail dos alunos.</li>
          <li>Convites chegam por e-mail com um código de 6 dígitos, válido por 7 dias.</li>
          <li>O aluno cria a própria conta com o e-mail de um domínio liberado.</li>
        </ol>
      </section>
    </div>
  );
}
