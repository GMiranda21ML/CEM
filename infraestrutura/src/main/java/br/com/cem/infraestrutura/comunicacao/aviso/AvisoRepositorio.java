package br.com.cem.infraestrutura.comunicacao.aviso;

import br.com.cem.dominio.comunicacao.aviso.Aviso;
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;
import br.com.cem.dominio.comunicacao.aviso.AvisoServico;
import br.com.cem.dominio.comunicacao.aviso.spi.IAvisoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

import java.util.Objects;
import java.util.Optional;

public class AvisoRepositorio
		extends AgregadoRepositorioAbstrato<AvisoIdentificador, Aviso, Long, AvisoJpa>
		implements IAvisoRepositorio {
	private final IAvisoRepositorioJpa repositorioJpa;

	public AvisoRepositorio(IAvisoRepositorioJpa repositorioJpa) {
		super(repositorioJpa, AvisoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected AvisoJpa paraJpa(Aviso a) {
		Objects.requireNonNull(a);
		return new AvisoJpa(
				a.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				a.getTitulo(),
				a.getTexto(),
				a.getValidade(),
				a.getAtivo(),
				a.getPublicadoEm());
	}

	@Override
	protected Aviso paraAgregado(AvisoJpa a) {
		Objects.requireNonNull(a);
		return new Aviso(
				Optional.of(
						new AvisoIdentificador(
								Objects.requireNonNull(a.getIdentificador()).toString())),
				a.getTitulo(),
				a.getTexto(),
				a.getValidade(),
				a.getAtivo(),
				a.getPublicadoEm());
	}

	@Override
	protected Long paraIdentificadorJpa(AvisoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return Long.valueOf(id.toString());
		} catch (NumberFormatException ex) {
			throw new AgregadoInexistenteExcecao(
					AvisoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
		}
	}

	@Override
	public boolean existePorIdentificador(AvisoIdentificador id) {
		Objects.requireNonNull(id);
		try {
			return super.existePorIdentificador(id);
		} catch (AgregadoInexistenteExcecao ex) {
			return false;
		}
	}
}
