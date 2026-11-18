package br.com.cem.infraestrutura.monitoria.falta;

import br.com.cem.aplicacao.monitoria.falta.FaltaProjecao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IFaltaRepositorioJpa extends JpaRepository<FaltaJpa, Long> {
	@Query("select a from FaltaJpa a where lower(a.sessao.disciplina) = lower(:disciplina)")
	List<FaltaJpa> listarPorDisciplina(String disciplina);
	@Query(
"""
select new br.com.cem.aplicacao.monitoria.falta.FaltaProjecao(a.identificador, a.sessao.identificador, a.pessoa.identificador, a.papel, a.autor.identificador, a.observacao, a.registradaEm, reposicao.identificador) from FaltaJpa a left join a.reposicao reposicao where (:coordenador = true or a.autor.identificador = :usuario or a.sessao.monitor.identificador = :usuario or a.sessao.professor.identificador = :usuario or (a.papel = 'MONITOR' and exists (select 1 from SessaoJpa s join s.alunos u where s=a.sessao and u.identificador=:usuario))) and (:texto is null or lower(a.observacao) like :texto escape '\\')
""")
	Page<FaltaProjecao> pesquisar(
			boolean coordenador, Long usuario, @Nullable String texto, Pageable pageable);

	@Query(
			"select count(a)>0 from FaltaJpa a where a.sessao.identificador=:sessao and"
					+ " a.pessoa.identificador=:pessoa")
	boolean existePorSessaoEPessoa(Long sessao, Long pessoa);

	@Query(
			"select count(a) from FaltaJpa a where a.pessoa.identificador=:pessoa and"
					+ " a.papel=:papel and a.sessao.disciplina=:disciplina")
	long contar(Long pessoa, String papel, String disciplina);
}
