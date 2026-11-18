package br.com.cem.infraestrutura.monitoria.feedback;

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
}
