import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';
import { cadastrarCurso, consultaCursos, excluirCurso } from './api';

interface DadosCurso { nome: string; sigla: string }

export async function loader() {
  await clienteConsultas.ensureQueryData(consultaCursos());
  return null;
}

export function Cursos() {
  const { publicar } = useNotificacoes();
  const [busca, setBusca] = useState('');
  const { data } = useSuspenseQuery(consultaCursos(busca));
  const { register, handleSubmit, reset, formState } = useForm<DadosCurso>({ mode: 'onChange' });
  const atualizar = () => clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'cursos'] });
  const cadastrar = useMutation({
    mutationFn: cadastrarCurso,
    onSuccess: () => { reset(); atualizar(); publicar('sucesso', 'Curso cadastrado.'); },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });
  const excluir = useMutation({
    mutationFn: excluirCurso,
    onSuccess: () => { atualizar(); publicar('sucesso', 'Curso removido.'); },
    onError: (erro) => publicar('erro', mensagemErro(erro)),
  });

  return <>
    <div className="page-header"><div><div className="eyebrow">Área do coordenador</div><h1>Cursos</h1>
      <p>Cadastre os cursos da sua instituição e acompanhe os vínculos acadêmicos.</p></div></div>
    <div className="content-grid">
      <section className="panel"><h2>Cadastrar curso</h2>
        <form className="form-grid" onSubmit={handleSubmit((dados) => cadastrar.mutate(dados))} noValidate>
          <div className="field full"><label htmlFor="nome-curso">Nome *</label>
            <input id="nome-curso" maxLength={150} {...register('nome', { required: true, maxLength: 150 })} /></div>
          <div className="field full"><label htmlFor="sigla-curso">Sigla *</label>
            <input id="sigla-curso" maxLength={20} {...register('sigla', { required: true, maxLength: 20 })} /></div>
          <div className="form-actions"><button className="button button-primary" type="submit"
            disabled={!formState.isValid || cadastrar.isPending}>Cadastrar</button></div>
        </form>
      </section>
      <section className="panel"><h2>Cursos cadastrados</h2><p className="panel-sub">{data.resultado.total} curso(s).</p>
        <div className="field"><label htmlFor="busca-curso">Buscar</label>
          <input id="busca-curso" value={busca} onChange={(e) => setBusca(e.target.value)} placeholder="Nome ou sigla" /></div>
        <div className="list-table-wrap"><table className="list-table"><thead><tr>
          <th>Curso</th><th>Disciplinas</th><th>Usuários</th><th /></tr></thead><tbody>
          {data.resultado.projecoes.map((curso) => {
            const temVinculos = curso.quantidadeDisciplinas > 0 || curso.quantidadeUsuarios > 0;
            return <tr key={curso.identificador}><td><strong>{curso.nome}</strong><div className="subtext">{curso.sigla}</div></td>
              <td>{curso.quantidadeDisciplinas}</td><td>{curso.quantidadeUsuarios}</td><td>
                <button className="button button-danger button-small" type="button" disabled={temVinculos || excluir.isPending}
                  title={temVinculos ? 'Cursos com disciplinas ou usuários vinculados não podem ser removidos.' : 'Remover curso'}
                  onClick={() => excluir.mutate(curso.identificador)}>Remover</button></td></tr>;
          })}
        </tbody></table></div>
        {!data.resultado.projecoes.length && <p className="empty">Nenhum curso cadastrado.</p>}
      </section>
    </div>
  </>;
}
