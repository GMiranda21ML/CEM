package br.com.cem.infraestrutura.acesso.dominioemail;

import java.util.Objects;

import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;
import br.com.cem.dominio.acesso.dominioemail.spi.IDominioEmailRepositorio;

import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class DominioEmailRepositorio
		extends AgregadoRepositorioAbstrato<DominioEmailDominio, DominioEmail, String, DominioEmailJpa>
		implements IDominioEmailRepositorio {
	public DominioEmailRepositorio(IDominioEmailRepositorioJpa repositorioJpa) {
		super(repositorioJpa, DominioEmailServico.DOMINIO_INEXISTENTE_MODELO);
	}

	@Override
	protected DominioEmailJpa paraJpa(DominioEmail agregado) {
		Objects.requireNonNull(agregado);

		return new DominioEmailJpa(agregado.getIdentificador().orElseThrow().toString());
	}

	@Override
	protected DominioEmail paraAgregado(DominioEmailJpa agregadoJpa) {
		Objects.requireNonNull(agregadoJpa);

		return new DominioEmail(new DominioEmailDominio(agregadoJpa.getDominio()));
	}

	@Override
	protected String paraIdentificadorJpa(DominioEmailDominio identificador) {
		Objects.requireNonNull(identificador);

		return identificador.toString();
	}
}
