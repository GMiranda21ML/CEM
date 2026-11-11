package br.com.cem.dominio.acesso.campus;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class CampusIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public CampusIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador do campus em branco"));
	}
}
