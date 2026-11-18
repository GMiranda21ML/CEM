package br.com.cem.infraestrutura.monitoria.candidatura;

import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao(
 c.identificador, c.selecao.identificador, d.identificador, d.nome,
 c.selecao.semestre, a.identificador, a.nome, c.cr, c.situacao,
 c.notaEntrevista, c.comentarioEntrevista, c.resultadoFinal,
 c.motivoDecisao, c.criadaEm, c.decididaEm)
from CandidaturaJpa c join c.disciplina d join c.aluno a
where (:coordenador = true
  or (:professor = true and d.professor.identificador = :usuario)
  or (:aluno = true and a.identificador = :usuario))
  and (:selecao is null or c.selecao.identificador = :selecao)
  and (:texto is null or lower(a.nome) like :texto escape '\\' or lower(d.nome) like :texto escape '\\')
""")
	Page<CandidaturaProjecao> pesquisar(
			boolean coordenador,
			boolean professor,
			boolean aluno,
			Long usuario,
			@Nullable Long selecao,
			@Nullable String texto,
			Pageable pageable);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao(
 s.identificador, d.identificador, d.nome, d.codigo, c.nome,
 p.identificador, p.nome, s.semestre, s.prazoInscricao, s.vagas,
 s.crMinimo, s.pesoCr, s.pesoEntrevista, s.patamarMinimo, s.situacao,
 s.motivoEncerramento, s.abertaEm, s.encerradaEm,
 (select count(ca) from CandidaturaJpa ca where ca.selecao = s and ca.situacao <> br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.CANCELADA),
 (select count(ca) from CandidaturaJpa ca where ca.selecao = s and ca.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.PENDENTE),
 (select count(ca) from CandidaturaJpa ca where ca.selecao = s and ca.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA))
from SelecaoJpa s join s.disciplina d join d.curso c join d.professor p
where s.situacao = br.com.cem.dominio.monitoria.selecao.SelecaoSituacao.ABERTA
  and s.prazoInscricao >= :hoje
  and (:cursoUsuario is not null and (lower(trim(:cursoUsuario)) = lower(trim(c.nome)) or lower(trim(:cursoUsuario)) = lower(trim(c.sigla))))
order by s.prazoInscricao, lower(d.nome)
""")
	List<SelecaoProjecao> listarSelecoesAbertasCurso(@Nullable String cursoUsuario, LocalDate hoje);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao(
 c.identificador, c.selecao.identificador, d.identificador, d.nome,
 c.selecao.semestre, a.identificador, a.nome, c.cr, c.situacao,
 c.notaEntrevista, c.comentarioEntrevista, c.resultadoFinal,
 c.motivoDecisao, c.criadaEm, c.decididaEm)
from CandidaturaJpa c join c.disciplina d join c.aluno a
where a.identificador = :aluno
  and c.situacao in (
    br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.PENDENTE,
    br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA)
""")
	List<CandidaturaProjecao> listarAtivasDoAluno(Long aluno);
}
