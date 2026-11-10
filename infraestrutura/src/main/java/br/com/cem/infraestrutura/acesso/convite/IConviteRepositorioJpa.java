package br.com.cem.infraestrutura.acesso.convite;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.cem.aplicacao.acesso.convite.ConviteProjecao;
import br.com.cem.dominio.acesso.convite.ConviteSituacao;

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

	@Query("""
			select new br.com.cem.aplicacao.acesso.convite.ConviteProjecao(c.identificador, c.email, c.papel, c.curso, c.enviadoEm, c.validoAte, c.situacao)
			  from ConviteJpa c
			 where (:situacao is null or c.situacao = :situacao)
			""")
	Page<ConviteProjecao> pesquisar(@Nullable ConviteSituacao situacao, Pageable pageable);
}
