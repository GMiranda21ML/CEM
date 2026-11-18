package br.com.cem.infraestrutura.monitoria.selecao;

import br.com.cem.aplicacao.monitoria.selecao.DisciplinaProfessorProjecao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ISelecaoRepositorioJpa extends JpaRepository<SelecaoJpa, Long> {
	@Query(
			"select count(s) > 0 from SelecaoJpa s where s.disciplina.identificador = :disciplina"
					+ " and s.situacao = br.com.cem.dominio.monitoria.selecao.SelecaoSituacao.ABERTA"
					+ " and (:ignorado is null or s.identificador <> :ignorado)")
	boolean existeAbertaPorDisciplina(Long disciplina, @Nullable Long ignorado);

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
where (:coordenador = true
  or (:professor = true and p.identificador = :usuario)
  or (:aluno = true and s.situacao = br.com.cem.dominio.monitoria.selecao.SelecaoSituacao.ABERTA
      and s.prazoInscricao >= :hoje
      and (:cursoUsuario is not null and (lower(trim(:cursoUsuario)) = lower(trim(c.nome)) or lower(trim(:cursoUsuario)) = lower(trim(c.sigla))))))
  and (:texto is null or lower(d.nome) like :texto escape '\\' or lower(d.codigo) like :texto escape '\\')
""")
	Page<SelecaoProjecao> pesquisar(
			boolean coordenador,
			boolean professor,
			boolean aluno,
			Long usuario,
			@Nullable String cursoUsuario,
			LocalDate hoje,
			@Nullable String texto,
			Pageable pageable);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.selecao.DisciplinaProfessorProjecao(
 d.identificador, d.codigo, d.nome, c.nome)
from DisciplinaJpa d join d.curso c
where d.professor.identificador = :professor
order by lower(d.nome)
""")
	List<DisciplinaProfessorProjecao> listarDisciplinasDoProfessor(Long professor);

	@Query(
"""
select c.aluno.nome from CandidaturaJpa c
where c.selecao.identificador = :selecao
  and c.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA
order by lower(c.aluno.nome)
""")
	List<String> listarNomesMonitoresAprovados(Long selecao);
}
