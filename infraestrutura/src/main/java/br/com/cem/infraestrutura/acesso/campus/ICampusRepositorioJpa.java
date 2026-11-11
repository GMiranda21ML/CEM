package br.com.cem.infraestrutura.acesso.campus;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ICampusRepositorioJpa extends JpaRepository<CampusJpa, Long> {
	@Query("""
			select count(c) > 0
			  from CampusJpa c
			 where lower(c.nome) = lower(:nome)
			   and c.instituicao = :instituicao
			   and (:identificadorIgnorado is null or c.identificador <> :identificadorIgnorado)
			""")
	boolean existePorNome(String nome, String instituicao, @Nullable Long identificadorIgnorado);
}
