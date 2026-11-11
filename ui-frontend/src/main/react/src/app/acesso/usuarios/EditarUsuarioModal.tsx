import { useMutation } from '@tanstack/react-query';
import { Modal } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { atualizarDadosUsuario, type UsuarioProjecao } from '../api';
import { clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';

interface DadosEdicao {
  nome: string;
  curso: string;
  campus: string;
}

export function EditarUsuarioModal({ usuario, fechar }: { usuario: UsuarioProjecao; fechar: () => void }) {
  const { publicar } = useNotificacoes();
  const { register, handleSubmit, formState } = useForm<DadosEdicao>({
    mode: 'onChange',
    defaultValues: {
      nome: usuario.nome,
      curso: usuario.curso ?? '',
      campus: usuario.campus ?? '',
    },
  });

  const alterar = useMutation({
    mutationFn: (dados: DadosEdicao) =>
      atualizarDadosUsuario({
        identificador: usuario.identificador,
        nome: dados.nome,
        curso: dados.curso || null,
        campus: dados.campus || null,
      }),
    onSuccess: () => {
      clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'usuarios'] });
      publicar('sucesso', 'Dados atualizados.');
      fechar();
    },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return (
    <Modal show onHide={fechar} contentClassName="panel">
      <form onSubmit={handleSubmit((dados) => alterar.mutate(dados))} noValidate>
        <h2>Editar {usuario.nome}</h2>
        <p className="panel-sub">
          E-mail {usuario.email} · perfil {usuario.papel} (o perfil não muda depois do cadastro).
        </p>
        <div className="form-grid">
          <div className="field full">
            <label htmlFor="nome-edicao">Nome completo *</label>
            <input id="nome-edicao" autoFocus maxLength={100} {...register('nome', { required: true })} />
          </div>
          <div className="field">
            <label htmlFor="curso-edicao">Curso</label>
            <input id="curso-edicao" {...register('curso')} />
          </div>
          <div className="field">
            <label htmlFor="campus-edicao">Campus</label>
            <input id="campus-edicao" {...register('campus')} />
          </div>
          <div className="form-actions">
            <button className="button button-primary" type="submit" disabled={!formState.isValid || alterar.isPending}>
              Salvar
            </button>
            <button className="button button-ghost" type="button" onClick={fechar}>
              Cancelar
            </button>
          </div>
        </div>
      </form>
    </Modal>
  );
}
