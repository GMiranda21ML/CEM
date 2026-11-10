package br.com.cem.infraestrutura.acesso.convite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IConviteRepositorioJpa extends JpaRepository<ConviteJpa, Long> {
	@Query("""
			select count(c) > 0
			  from ConviteJpa c
			 where lower(c.email) = lower(:email)
			   and c.situacao = br.com.cem.dominio.acesso.convite.ConviteSituacao.AGUARDANDO_ACEITE
			""")
	boolean existePendentePorEmail(String email);

	@Query("""
			  from ConviteJpa c
			 where lower(c.email) = lower(:email)
			   and c.situacao = br.com.cem.dominio.acesso.convite.ConviteSituacao.AGUARDANDO_ACEITE
			""")
	Optional<ConviteJpa> buscarPendentePorEmail(String email);
}
