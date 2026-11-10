package br.com.cem.dominio.acesso.convite;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class ConviteIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public ConviteIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador do convite em branco"));
	}
}
