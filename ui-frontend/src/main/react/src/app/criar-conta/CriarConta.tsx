import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router';
import { criarContaAluno } from '../acesso/api';
import { entrar, mensagemErro, useNotificacoes } from '../../d5';

interface DadosFormulario {
  nome: string;
  email: string;
  curso: string;
  senha: string;
}

export function CriarConta() {
  const navegar = useNavigate();
  const { publicar } = useNotificacoes();
  const {
    register,
    handleSubmit,
    formState: { isValid },
  } = useForm<DadosFormulario>({ mode: 'onChange' });

  const { mutate, isPending } = useMutation({
    mutationFn: async (dados: DadosFormulario) => {
      await criarContaAluno(dados);
      await entrar(dados.email, dados.senha);
    },
    onSuccess: () => {
      publicar('sucesso', 'Conta criada! Bem-vindo(a) ao CEM.');
      navegar('/inicio', { replace: true });
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <div className="login-wrap">
      <section className="login-card">
        <div className="eyebrow">Cadastro de aluno</div>
        <h1>Criar conta</h1>
        <p className="muted">
          Alunos criam a própria conta. O sistema confere se o domínio do e-mail pertence a uma
          instituição liberada pela coordenação.
        </p>
        <form className="form-grid" onSubmit={handleSubmit((dados) => mutate(dados))} noValidate>
          <div className="field full">
            <label htmlFor="nome">Nome completo *</label>
            <input id="nome" autoFocus maxLength={100} {...register('nome', { required: true })} />
          </div>
          <div className="field full">
            <label htmlFor="email">E-mail da instituição *</label>
            <input
              id="email"
              type="email"
              placeholder="seu.nome@aluno.cem.edu.br"
              {...register('email', { required: true })}
            />
            <small>Só é aceito o domínio de e-mail cadastrado pela coordenação da sua instituição.</small>
          </div>
          <div className="field full">
            <label htmlFor="curso">Curso *</label>
            <input id="curso" {...register('curso', { required: true })} />
          </div>
          <div className="field full">
            <label htmlFor="senha">Senha *</label>
            <input id="senha" type="password" {...register('senha', { required: true })} />
            <small>8 ou mais caracteres, com maiúscula, minúscula, número e símbolo.</small>
          </div>
          <div className="form-actions">
            <button className="button button-primary" type="submit" disabled={!isValid || isPending}>
              Criar minha conta →
            </button>
          </div>
        </form>
        <p className="tiny muted" style={{ marginTop: '14px' }}>
          Já tem conta? <Link to="/login">Entrar</Link> · <Link to="/convite">Recebi um convite</Link>
        </p>
      </section>
      <section className="login-side">
        <h2>Como funciona</h2>
        <ol className="steps-list">
          <li>A coordenação cadastra o domínio de e-mail dos alunos (ex.: @aluno.cem.edu.br).</li>
          <li>Você se cadastra com o e-mail desse domínio.</li>
          <li>E-mails de outros domínios (ex.: @gmail.com) são recusados: não há instituição para eles.</li>
          <li>Professores e coordenadores não se cadastram aqui: eles recebem um convite com código.</li>
        </ol>
      </section>
    </div>
  );
}
