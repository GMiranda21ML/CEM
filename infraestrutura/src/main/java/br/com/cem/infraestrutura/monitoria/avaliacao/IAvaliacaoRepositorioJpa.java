package br.com.cem.infraestrutura.monitoria.avaliacao;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao;

public interface IAvaliacaoRepositorioJpa extends JpaRepository<AvaliacaoJpa, Long> {
	@Query("""
			select count(a) > 0
			  from AvaliacaoJpa a
			 where a.sessao = :sessao
			   and a.autor.identificador = :autor
			""")
	boolean existePorSessaoEAutor(String sessao, Long autor);

	@Query("""
			select new br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao(a.identificador, a.sessao, u.identificador, u.nome, a.perfil, a.nota, a.comentario, a.registradaEm, a.atualizadaEm)
			  from AvaliacaoJpa a
			  join a.autor u
			 where u.identificador = :autor
			   and (:sessao is null or a.sessao = :sessao)
			""")
	Page<AvaliacaoProjecao> pesquisarProprias(Long autor, @Nullable String sessao, Pageable pageable);

	@Query("""
			select new br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao(a.identificador, a.sessao, u.identificador, u.nome, a.perfil, a.nota, a.comentario, a.registradaEm, a.atualizadaEm)
			  from AvaliacaoJpa a
			  join a.autor u
			 where a.sessao = :sessao
			""")
	Page<AvaliacaoProjecao> pesquisarDaSessao(String sessao, Pageable pageable);

	@Query("select coalesce(avg(a.nota), 0) from AvaliacaoJpa a where a.sessao = :sessao")
	double calcularMedia(String sessao);
}
