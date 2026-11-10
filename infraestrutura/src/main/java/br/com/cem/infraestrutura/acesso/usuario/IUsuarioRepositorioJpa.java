package br.com.cem.infraestrutura.acesso.usuario;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.acesso.usuario.UsuarioProjecao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

public interface IUsuarioRepositorioJpa extends JpaRepository<UsuarioJpa, Long> {
	@Query("""
			select count(u) > 0
			  from UsuarioJpa u
			 where lower(u.email) = lower(:email)
			   and (:identificadorIgnorado is null or u.identificador <> :identificadorIgnorado)
			""")
	boolean existePorEmail(String email, @Nullable Long identificadorIgnorado);

	Optional<UsuarioJpa> findByEmailIgnoreCase(String email);

	@Query("""
			select new br.com.cem.aplicacao.acesso.usuario.UsuarioProjecao(u.identificador, u.nome, u.email, u.papel, u.curso, u.situacao)
			  from UsuarioJpa u
			 where (:nome is null or lower(u.nome) like :nome escape '\\')
			   and (:papel is null or u.papel = :papel)
			   and (:situacao is null or u.situacao = :situacao)
			""")
	Page<UsuarioProjecao> pesquisar(@Nullable String nome, @Nullable Autorizacao papel,
			@Nullable UsuarioSituacao situacao, Pageable pageable);
}
