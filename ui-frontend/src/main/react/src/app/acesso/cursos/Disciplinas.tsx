import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { clienteConsultas, mensagemErro, useNotificacoes } from '../../../d5';
import { cadastrarDisciplina, consultaCursos, consultaDisciplinas, consultaProfessores, editarDisciplina, excluirDisciplina, type DisciplinaProjecao } from './api';

interface DadosDisciplina { nome: string; codigo: string; area: string; curso: string; professor: string }

export async function loader() {
  await Promise.all([
    clienteConsultas.ensureQueryData(consultaCursos()),
    clienteConsultas.ensureQueryData(consultaDisciplinas()),
  ]);
  return null;
}

export function Disciplinas() {
  const { publicar } = useNotificacoes();
  const [busca, setBusca] = useState('');
  const [cursoFiltro, setCursoFiltro] = useState('');
  const [edicao, setEdicao] = useState<DisciplinaProjecao | null>(null);
  const cursos = useSuspenseQuery(consultaCursos()).data.resultado.projecoes;
  const disciplinas = useSuspenseQuery(consultaDisciplinas(busca, cursoFiltro)).data.resultado;
  const { register, handleSubmit, reset, setValue, watch, formState } = useForm<DadosDisciplina>({ mode: 'onChange' });
  const cursoSelecionado = watch('curso') ?? '';
  const { data: professores = [], isLoading: carregandoProfessores } = useQuery(consultaProfessores(cursoSelecionado));
  const invalidar = () => {
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'disciplinas'] });
    clienteConsultas.invalidateQueries({ queryKey: ['acesso', 'cursos'] });
  };
  const concluir = (mensagem: string) => { invalidar(); reset(); setEdicao(null); publicar('sucesso', mensagem); };
  const cadastrar = useMutation({ mutationFn: cadastrarDisciplina,
    onSuccess: () => concluir('Disciplina cadastrada.'), onError: (erro) => publicar('erro', mensagemErro(erro)) });
  const alterar = useMutation({ mutationFn: ({ id, dados }: { id: string; dados: DadosDisciplina }) =>
    editarDisciplina(id, { nome: dados.nome, area: dados.area, professor: dados.professor }),
    onSuccess: () => concluir('Disciplina atualizada.'), onError: (erro) => publicar('erro', mensagemErro(erro)) });
  const excluir = useMutation({ mutationFn: excluirDisciplina,
    onSuccess: () => concluir('Disciplina removida.'), onError: (erro) => publicar('erro', mensagemErro(erro)) });

  function iniciarEdicao(disciplina: DisciplinaProjecao) {
    setEdicao(disciplina);
    setValue('nome', disciplina.nome, { shouldValidate: true });
    setValue('codigo', disciplina.codigo, { shouldValidate: true });
    setValue('area', disciplina.area, { shouldValidate: true });
    setValue('curso', disciplina.curso, { shouldValidate: true });
    setValue('professor', disciplina.professor, { shouldValidate: true });
  }

  function cancelarEdicao() { setEdicao(null); reset(); }

  return <>
    <div className="page-header"><div><div className="eyebrow">Área do coordenador</div><h1>Disciplinas e áreas</h1>
      <p>Cadastre disciplinas, associe o curso e o professor responsável e organize por área de conhecimento.</p></div></div>
    <div className="content-grid">
      <section className="panel"><h2>{edicao ? 'Editar disciplina' : 'Cadastrar disciplina'}</h2>
        <form className="form-grid" onSubmit={handleSubmit((dados) => edicao
          ? alterar.mutate({ id: edicao.identificador, dados }) : cadastrar.mutate(dados))} noValidate>
          <div className="field full"><label htmlFor="nome-disciplina">Nome *</label>
            <input id="nome-disciplina" maxLength={150} {...register('nome', { required: true, maxLength: 150 })} /></div>
          <div className="field"><label htmlFor="codigo-disciplina">Código *</label>
            <input id="codigo-disciplina" maxLength={7} placeholder="AAA0000" disabled={Boolean(edicao)}
              {...register('codigo', { required: true, pattern: /^[A-Z]{3}[0-9]{4}$/ })} /></div>
          <div className="field"><label htmlFor="area-disciplina">Área de conhecimento *</label>
            <input id="area-disciplina" maxLength={100} {...register('area', { required: true, maxLength: 100 })} /></div>
          <div className="field full"><label htmlFor="curso-disciplina">Curso *</label>
            <select id="curso-disciplina" disabled={Boolean(edicao)} {...register('curso', { required: true })}>
              <option value="">Selecione um curso</option>{cursos.map((curso) =>
                <option key={curso.identificador} value={curso.identificador}>{curso.nome} ({curso.sigla})</option>)}
            </select></div>
          <div className="field full"><label htmlFor="professor-disciplina">Professor responsável *</label>
            <select id="professor-disciplina" {...register('professor', { required: true })}>
              <option value="">{carregandoProfessores ? 'Carregando professores…' : 'Selecione um professor'}</option>
              {professores.map((professor) => <option key={professor.identificador} value={professor.identificador}>{professor.nome}</option>)}
            </select>
            {cursoSelecionado && !carregandoProfessores && professores.length === 0 &&
              <small className="subtext">Não há professor ativo vinculado a este curso e instituição.</small>}
          </div>
          <div className="form-actions"><button className="button button-primary" type="submit"
            disabled={!formState.isValid || cadastrar.isPending || alterar.isPending || !professores.some((p) => p.identificador === watch('professor'))}>
            {edicao ? 'Salvar alterações' : 'Cadastrar'}</button>
            {edicao && <button className="button button-ghost" type="button" onClick={cancelarEdicao}>Cancelar</button>}
          </div>
        </form>
      </section>
      <section className="panel"><h2>Disciplinas cadastradas</h2><p className="panel-sub">{disciplinas.total} disciplina(s). Disciplinas com vínculos de monitoria não podem ser removidas.</p>
        <div className="form-grid">
          <div className="field"><label htmlFor="busca-disciplina">Buscar</label>
            <input id="busca-disciplina" value={busca} onChange={(e) => setBusca(e.target.value)} placeholder="Nome, código ou área" /></div>
          <div className="field"><label htmlFor="filtro-curso">Curso</label>
            <select id="filtro-curso" value={cursoFiltro} onChange={(e) => setCursoFiltro(e.target.value)}>
              <option value="">Todos</option>{cursos.map((curso) => <option key={curso.identificador} value={curso.identificador}>{curso.nome}</option>)}
            </select></div>
        </div>
        <div className="list-table-wrap"><table className="list-table"><thead><tr>
          <th>Disciplina</th><th>Área</th><th>Curso</th><th>Professor</th><th /></tr></thead><tbody>
          {disciplinas.projecoes.map((disciplina) => <tr key={disciplina.identificador}>
            <td><strong>{disciplina.nome}</strong><div className="subtext">{disciplina.codigo}</div></td>
            <td>{disciplina.area}</td><td>{disciplina.cursoNome}</td><td>{disciplina.professorNome}</td><td>
              <div className="row-actions"><button className="button button-ghost button-small" type="button"
                onClick={() => iniciarEdicao(disciplina)}>Editar</button>
                <button className="button button-danger button-small" type="button"
                  disabled={!disciplina.exclusaoPermitida || excluir.isPending}
                  title={disciplina.exclusaoPermitida ? 'Remover disciplina' : 'A disciplina possui vínculos de monitoria.'}
                  onClick={() => excluir.mutate(disciplina.identificador)}>Remover</button></div></td>
          </tr>)}
        </tbody></table></div>
        {!disciplinas.projecoes.length && <p className="empty">Nenhuma disciplina encontrada.</p>}
      </section>
    </div>
  </>;
}
