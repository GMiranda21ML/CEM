package br.com.cem.dominio.comunicacao.aviso;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

import org.apache.commons.lang3.Validate;

public final class AvisoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public static final int TAMANHO = 100;

	public AvisoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador obrigatório"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador muito longo");
	}
}
