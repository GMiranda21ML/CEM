package br.com.cem.infraestrutura.monitoria.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICandidaturaRepositorioJpa extends JpaRepository<CandidaturaJpa, Long> {
	@Query("select count(c) > 0 from CandidaturaJpa c where c.selecao.identificador = :selecao")
	boolean existePorSelecao(Long selecao);

	@Query(
			"select count(c) > 0 from CandidaturaJpa c where c.aluno.identificador = :aluno"
					+ " and c.disciplina.identificador = :disciplina"
					+ " and c.situacao in ("
					+ "br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.PENDENTE,"
					+ " br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA)")
	boolean existeAtivaPorAlunoEDisciplina(Long aluno, Long disciplina);

	@Query(
			"select count(distinct c.disciplina.identificador) from CandidaturaJpa c"
					+ " where c.aluno.identificador = :aluno"
					+ " and c.selecao.semestre = :semestre"
					+ " and c.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA"
					+ " and c.disciplina.identificador <> :disciplinaIgnorada")
	long contarMonitoriasAtivasNoSemestre(Long aluno, String semestre, Long disciplinaIgnorada);

	@Query(
			"select count(c) > 0 from CandidaturaJpa c where c.aluno.identificador = :aluno"
					+ " and c.disciplina.identificador = :disciplina"
					+ " and c.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA")
	boolean ehMonitorAtivo(Long aluno, Long disciplina);

	@Query(
			"select count(c) > 0 from CandidaturaJpa c where c.aluno.identificador = :aluno"
					+ " and c.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA")
	boolean ehMonitorEmAlgumaDisciplina(Long aluno);

	@Query("select c from CandidaturaJpa c where c.selecao.identificador = :selecao")
	List<CandidaturaJpa> listarPorSelecao(Long selecao);
}
