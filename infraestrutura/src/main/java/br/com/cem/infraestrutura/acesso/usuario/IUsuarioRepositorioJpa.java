package br.com.cem.infraestrutura.acesso.usuario;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUsuarioRepositorioJpa extends JpaRepository<UsuarioJpa, Long> {
	@Query("""
			select count(u) > 0
			  from UsuarioJpa u
			 where lower(u.email) = lower(:email)
			   and (:identificadorIgnorado is null or u.identificador <> :identificadorIgnorado)
			""")
	boolean existePorEmail(String email, @Nullable Long identificadorIgnorado);

	Optional<UsuarioJpa> findByEmailIgnoreCase(String email);
}
