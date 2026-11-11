package br.com.cem.infraestrutura.monitoria.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAvaliacaoRepositorioJpa extends JpaRepository<AvaliacaoJpa, Long> {
	@Query("""
			select count(a) > 0
			  from AvaliacaoJpa a
			 where a.sessao = :sessao
			   and a.autor.identificador = :autor
			""")
	boolean existePorSessaoEAutor(String sessao, Long autor);
}
