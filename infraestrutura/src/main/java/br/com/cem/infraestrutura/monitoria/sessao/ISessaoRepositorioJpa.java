package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.aplicacao.monitoria.sessao.CampusAgendaProjecao;
import br.com.cem.aplicacao.monitoria.sessao.PessoaAgendaProjecao;
import br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISessaoRepositorioJpa extends JpaRepository<SessaoJpa, Long> {
	@Query("select count(s) > 0 from SessaoJpa s where s.monitor.identificador = :monitor")
	boolean existePorMonitor(Long monitor);

	@Override
	@EntityGraph(attributePaths = "alunos")
	Optional<SessaoJpa> findById(Long identificador);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.sessao.PessoaAgendaProjecao(u.identificador,u.nome,u.papel)
from UsuarioJpa u where :coordenador=true or u.identificador=:usuario or exists
(select 1 from SessaoJpa s where
 (s.professor.identificador=:usuario or s.monitor.identificador=:usuario or exists
  (select 1 from SessaoJpa s2 join s2.alunos a where s2=s and a.identificador=:usuario))
 and (s.professor=u or s.monitor=u or u member of s.alunos))
order by u.nome
""")
	List<PessoaAgendaProjecao> pessoas(boolean coordenador, Long usuario);

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.sessao.CampusAgendaProjecao(c.identificador,c.nome)
from CampusJpa c where c.situacao=br.com.cem.dominio.acesso.campus.CampusSituacao.ATIVO order by c.nome
""")
	List<CampusAgendaProjecao> campusAtivos();

	@Query(
"""
select new br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao(a.identificador, a.disciplina, a.professor.identificador, a.monitor.identificador, a.campus.identificador, a.inicio, a.fim, a.cancelada) from SessaoJpa a where (:coordenador = true or a.monitor.identificador = :usuario or a.professor.identificador = :usuario or exists (select 1 from SessaoJpa s join s.alunos u where s = a and u.identificador = :usuario)) and (:texto is null or lower(a.disciplina) like :texto escape '\\')
""")
	Page<SessaoProjecao> pesquisar(
			boolean coordenador, Long usuario, @Nullable String texto, Pageable pageable);

	@Query(
			"select count(a)>0 from SessaoJpa a where a.cancelada=false and a.inicio < :fim and"
					+ " a.fim > :inicio and (a.monitor.identificador=:monitor or"
					+ " a.monitor.identificador=:aluno or exists (select 1 from SessaoJpa s join"
					+ " s.alunos u where s=a and (u.identificador=:aluno or"
					+ " u.identificador=:monitor)))")
	boolean conflita(Long monitor, Long aluno, LocalDateTime inicio, LocalDateTime fim);
}
