package br.com.cem.aplicacao.acesso.dominioemail;

import java.util.Objects;

import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class DominioEmailProjecao implements IProjecao<DominioEmail> {
	private final DominioEmailDominio identificador;

	public DominioEmailProjecao(DominioEmailDominio identificador) {
		this.identificador = Objects.requireNonNull(identificador);
	}

	public DominioEmailProjecao(String identificador) {
		this(new DominioEmailDominio(identificador));
	}

	public DominioEmailDominio getIdentificador() {
		return identificador;
	}
}
