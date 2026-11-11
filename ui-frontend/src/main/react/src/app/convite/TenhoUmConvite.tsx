import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router';
import { aceitarConvite, verificarConvite, type ConviteProjecao } from '../acesso/api';
import { entrar, mensagemErro, useNotificacoes } from '../../d5';

interface DadosVerificacao {
  email: string;
  codigo: string;
}

interface DadosConta {
  nome: string;
  senha: string;
}

export function TenhoUmConvite() {
  const navegar = useNavigate();
  const { publicar } = useNotificacoes();
  const [convite, definirConvite] = useState<(DadosVerificacao & { convite: ConviteProjecao }) | null>(null);

  const formVerificacao = useForm<DadosVerificacao>({ mode: 'onChange' });
  const formConta = useForm<DadosConta>({ mode: 'onChange' });

  const verificacao = useMutation({
    mutationFn: (dados: DadosVerificacao) => verificarConvite(dados.email, dados.codigo),
    onSuccess: (resultado, dados) => definirConvite({ ...dados, convite: resultado }),
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const aceite = useMutation({
    mutationFn: async (dados: DadosConta) => {
      if (!convite) {
        return;
      }
      await aceitarConvite({ email: convite.email, codigo: convite.codigo, ...dados });
      await entrar(convite.email, dados.senha);
    },
    onSuccess: () => {
      publicar('sucesso', 'Conta criada! Bem-vindo(a) ao CEM.');
      navegar('/inicio', { replace: true });
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  const papelTexto = convite?.convite.papel === 'COORDENADOR' ? 'coordenador' : 'professor';

  return (
    <div className="login-wrap">
      <section className="login-card">
        <div className="eyebrow">Convite</div>
        <h1>{convite ? 'Criar sua conta' : 'Tenho um convite'}</h1>
        <p className="muted">
          Coordenadores são convidados pelo super administrador; professores, pelo coordenador. O
          convite chega por e-mail com um código de 6 dígitos.
        </p>

        {!convite && (
          <form
            className="form-grid"
            onSubmit={formVerificacao.handleSubmit((dados) => verificacao.mutate(dados))}
            noValidate
          >
            <div className="field full">
              <label htmlFor="email">E-mail institucional *</label>
              <input
                id="email"
                type="email"
                autoFocus
                {...formVerificacao.register('email', { required: true })}
              />
            </div>
            <div className="field full">
              <label htmlFor="codigo">Código recebido *</label>
              <input id="codigo" maxLength={6} {...formVerificacao.register('codigo', { required: true })} />
            </div>
            <div className="form-actions">
              <button
                className="button button-primary"
                type="submit"
                disabled={!formVerificacao.formState.isValid || verificacao.isPending}
              >
                Validar código →
              </button>
            </div>
          </form>
        )}

        {convite && (
          <>
            <div className="notice green">
              <span aria-hidden="true">ⓘ</span>
              <div>
                Convite válido para <b>{convite.email}</b> como <b>{papelTexto}</b> do curso{' '}
                <b>{convite.convite.curso}</b>. Complete seus dados.
              </div>
            </div>
            <form className="form-grid" onSubmit={formConta.handleSubmit((dados) => aceite.mutate(dados))} noValidate>
              <div className="field full">
                <label htmlFor="nome">Nome completo *</label>
                <input id="nome" autoFocus maxLength={100} {...formConta.register('nome', { required: true })} />
              </div>
              <div className="field full">
                <label htmlFor="senha">Senha *</label>
                <input id="senha" type="password" {...formConta.register('senha', { required: true })} />
                <small>8 ou mais caracteres, com maiúscula, minúscula, número e símbolo.</small>
              </div>
              <div className="form-actions">
                <button
                  className="button button-primary"
                  type="submit"
                  disabled={!formConta.formState.isValid || aceite.isPending}
                >
                  Criar minha conta →
                </button>
                <button
                  className="button button-ghost"
                  type="button"
                  onClick={() => definirConvite(null)}
                >
                  Usar outro código
                </button>
              </div>
            </form>
          </>
        )}

        <p className="tiny muted" style={{ marginTop: '14px' }}>
          <Link to="/login">Já tenho conta</Link> · <Link to="/criar-conta">Sou aluno e não tenho conta</Link>
        </p>
      </section>
      <section className="login-side">
        <h2>Quem convida quem</h2>
        <ol className="steps-list">
          <li>
            <b>Super administrador</b> convida o coordenador da instituição.
          </li>
          <li>
            <b>Coordenador</b> convida os professores do curso.
          </li>
          <li>O convidado informa e-mail e código, define nome e senha e já entra no sistema.</li>
          <li>O código vale 7 dias e pode ser reenviado; o anterior deixa de valer.</li>
        </ol>
      </section>
    </div>
  );
}
