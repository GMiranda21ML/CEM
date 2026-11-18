package br.com.cem.infraestrutura.monitoria.acompanhamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAcompanhamentoRepositorioJpa extends JpaRepository<AcompanhamentoJpa, Long> {
	@Query(
			"select count(a) > 0 from AcompanhamentoJpa a where a.aluno.identificador = :aluno"
					+ " and a.disciplina.identificador = :disciplina"
					+ " and a.situacao = br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao.ATIVO")
	boolean existeAtivoPorAlunoEDisciplina(Long aluno, Long disciplina);

	@Query(
			"select count(a) > 0 from AcompanhamentoJpa a where a.aluno.identificador = :aluno"
					+ " and a.disciplina.identificador = :disciplina"
					+ " and a.situacao = br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao.ENCERRADO")
	boolean existeEncerradoPorAlunoEDisciplina(Long aluno, Long disciplina);

	@Query(
"""
select count(a) > 0 from AcompanhamentoJpa a
join a.disciplina d
where a.aluno.identificador = :aluno
  and a.situacao = br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao.ENCERRADO
  and (lower(trim(d.nome)) = lower(trim(:disciplinaNomeOuCodigo))
       or lower(trim(d.codigo)) = lower(trim(:disciplinaNomeOuCodigo)))
  and d.professor.identificador = :professor
""")
	boolean existeBloqueioPorEncerramento(
			Long aluno, String disciplinaNomeOuCodigo, Long professor);
}
