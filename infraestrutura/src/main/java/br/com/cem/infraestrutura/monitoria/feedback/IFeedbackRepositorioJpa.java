package br.com.cem.infraestrutura.monitoria.feedback;

import br.com.cem.aplicacao.monitoria.feedback.FeedbackProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IFeedbackRepositorioJpa extends JpaRepository<FeedbackJpa, Long> {
	@Query(
			"select count(f) > 0 from FeedbackJpa f where f.sessao.identificador = :sessao"
					+ " and f.aluno.identificador = :aluno")
	boolean existePorSessaoEAluno(Long sessao, Long aluno);

	@Query(
"""
select count(f) > 0 from FeedbackJpa f
join f.sessao s,
DisciplinaJpa d
where d.identificador = :disciplina
  and f.aluno.identificador = :aluno
  and (lower(trim(s.disciplina)) = lower(trim(d.nome))
       or lower(trim(s.disciplina)) = lower(trim(d.codigo)))
  and (s.professor.identificador = d.professor.identificador
       or exists (
         select 1 from AcompanhamentoJpa a
         where a.disciplina = d and a.aluno.identificador = :aluno and a.monitor = s.monitor))
""")
	boolean existePorAlunoEDisciplina(Long aluno, Long disciplina);

	@Query(
"""
select f from FeedbackJpa f
join f.sessao s,
DisciplinaJpa d
where d.identificador = :disciplina
  and f.aluno.identificador = :aluno
  and (lower(trim(s.disciplina)) = lower(trim(d.nome))
       or lower(trim(s.disciplina)) = lower(trim(d.codigo)))
  and (s.professor.identificador = d.professor.identificador
       or exists (
         select 1 from AcompanhamentoJpa a
         where a.disciplina = d and a.aluno.identificador = :aluno and a.monitor = s.monitor))
order by s.inicio, f.identificador
""")
	List<FeedbackJpa> listarPorAlunoEDisciplina(Long aluno, Long disciplina);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.feedback.FeedbackProjecao(
 f.identificador, s.identificador, s.inicio, s.disciplina,
 al.identificador, al.nome, f.autor.identificador,
 f.nota, f.comentario, f.criadoEm, f.atualizadoEm, false)
from FeedbackJpa f
join f.sessao s
join f.aluno al
where (:coordenador = true
  or (:professor = true and s.professor.identificador = :usuario)
  or (:aluno = true and (f.autor.identificador = :usuario or al.identificador = :usuario or s.monitor.identificador = :usuario)))
  and (:texto is null
       or lower(al.nome) like :texto escape '\\'
       or lower(s.disciplina) like :texto escape '\\'
       or lower(coalesce(f.comentario, '')) like :texto escape '\\')
""")
	Page<FeedbackProjecao> pesquisar(
			boolean coordenador,
			boolean professor,
			boolean aluno,
			Long usuario,
			@Nullable String texto,
			Pageable pageable);
}
