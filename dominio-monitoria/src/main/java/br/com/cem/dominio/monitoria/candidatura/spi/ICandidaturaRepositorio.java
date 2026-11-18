package br.com.cem.dominio.monitoria.candidatura.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.util.Collection;

public interface ICandidaturaRepositorio
		extends IAgregadoRepositorio<CandidaturaIdentificador, Candidatura> {
	boolean existePorSelecao(SelecaoIdentificador selecao);

	boolean existeAtivaPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);

	long contarMonitoriasAtivasNoSemestre(
			UsuarioIdentificador aluno, String semestre, DisciplinaIdentificador disciplinaIgnorada);

	boolean ehMonitorAtivo(UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);

	boolean ehMonitorEmAlgumaDisciplina(UsuarioIdentificador aluno);

	Collection<Candidatura> listarPorSelecao(SelecaoIdentificador selecao);
}
