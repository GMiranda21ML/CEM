package br.com.cem.dominio.monitoria.avaliacao;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class AvaliacaoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public AvaliacaoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador da avaliação em branco"));
	}
}
