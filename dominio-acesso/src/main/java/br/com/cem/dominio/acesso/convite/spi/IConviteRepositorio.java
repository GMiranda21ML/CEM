package br.com.cem.dominio.acesso.convite.spi;

import java.util.Optional;

import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IConviteRepositorio extends IAgregadoRepositorio<ConviteIdentificador, Convite> {
	boolean existePendentePorEmail(ConviteEmail emailInstitucional);

	Optional<Convite> buscarPendentePorEmail(ConviteEmail emailInstitucional);
}
