package br.com.cem.dominio.monitoria.avaliacao;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class SessaoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public static final int TAMANHO = 100;

	public SessaoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador da sessão em branco"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador da sessão maior que %d caracteres", TAMANHO);
	}
}
