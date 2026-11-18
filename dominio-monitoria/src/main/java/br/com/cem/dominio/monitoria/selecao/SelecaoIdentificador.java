package br.com.cem.dominio.monitoria.selecao;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

import org.apache.commons.lang3.Validate;

public final class SelecaoIdentificador extends ObjetoValorStringAbstrato
		implements IIdentificador {
	public static final int TAMANHO = 100;

	public SelecaoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador obrigatório"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador muito longo");
	}
}
