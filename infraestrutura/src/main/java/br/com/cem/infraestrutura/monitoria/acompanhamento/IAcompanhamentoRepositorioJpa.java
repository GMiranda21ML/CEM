package br.com.cem.infraestrutura.monitoria.acompanhamento;

import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoProjecao;
import br.com.cem.aplicacao.monitoria.acompanhamento.AlunoDisciplinaOpcaoProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoProjecao(
 a.identificador, al.identificador, al.nome, d.identificador, d.nome,
 m.identificador, m.nome, p.identificador, p.nome, a.semestre,
 a.metaNota, a.metaFrequencia, a.situacao, a.abertoEm, a.encerradoEm, a.parecerFinal)
from AcompanhamentoJpa a
join a.aluno al
join a.disciplina d
join a.monitor m
join d.professor p
where (:coordenador = true
  or (:professor = true and p.identificador = :usuario)
  or (:aluno = true and (m.identificador = :usuario or al.identificador = :usuario)))
  and (:texto is null or lower(al.nome) like :texto escape '\\' or lower(d.nome) like :texto escape '\\' or lower(m.nome) like :texto escape '\\')
""")
	Page<AcompanhamentoProjecao> pesquisar(
			boolean coordenador,
			boolean professor,
			boolean aluno,
			Long usuario,
			@Nullable String texto,
			Pageable pageable);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.acompanhamento.AlunoDisciplinaOpcaoProjecao(
 u.identificador, u.nome, d.identificador, d.nome, c.nome)
from CandidaturaJpa cand
join cand.disciplina d
join d.curso c,
UsuarioJpa u
where cand.aluno.identificador = :monitor
  and cand.situacao = br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao.APROVADA
  and u.papel = br.com.cem.dominio.Autorizacao.ALUNO
  and u.situacao = br.com.cem.dominio.acesso.usuario.UsuarioSituacao.ATIVO
  and u.identificador <> :monitor
  and (lower(substring(u.email, locate('@', u.email) + 1)) = lower(d.instituicao)
       or lower(substring(u.email, locate('@', u.email) + 1)) like concat('%.', lower(d.instituicao)))
  and u.curso is not null
  and (lower(trim(u.curso)) = lower(trim(c.nome)) or lower(trim(u.curso)) = lower(trim(c.sigla)))
  and not exists (
    select 1 from AcompanhamentoJpa ac
    where ac.aluno = u and ac.disciplina = d
      and ac.situacao = br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao.ATIVO)
order by lower(d.nome), lower(u.nome)
""")
	List<AlunoDisciplinaOpcaoProjecao> listarOpcoesAbertura(Long monitor);
}
