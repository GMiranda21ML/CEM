package br.com.cem.infraestrutura.acesso.dominioemail;

import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "DEM_DOMINIO_EMAIL")
public class DominioEmailJpa implements IAgregadoJpa<DominioEmail> {
	@Id
	@Column(name = "DEM_DOMINIO", updatable = false)
	@NotNull
	@Size(max = DominioEmailDominio.TAMANHO)
	private String dominio;

	protected DominioEmailJpa() {
	}

	public DominioEmailJpa(String dominio) {
		this.dominio = dominio;
	}

	public String getDominio() {
		return dominio;
	}
}
