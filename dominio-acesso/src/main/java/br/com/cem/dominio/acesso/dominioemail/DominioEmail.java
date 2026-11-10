package br.com.cem.dominio.acesso.dominioemail;

import java.util.Objects;
import java.util.Optional;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class DominioEmail implements IAgregado<DominioEmailDominio> {
	private final DominioEmailDominio dominio;

	public DominioEmail(DominioEmailDominio dominio) {
		this.dominio = Objects.requireNonNull(dominio);
	}

	@Override
	public Optional<DominioEmailDominio> getIdentificador() {
		return Optional.of(dominio);
	}

	public DominioEmailDominio getDominio() {
		return dominio;
	}
}
