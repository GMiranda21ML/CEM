package br.com.cem.dominio.acesso.campus;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class InstituicaoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public static final int TAMANHO = 180;

	public InstituicaoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador da instituição em branco"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador da instituição maior que %d caracteres", TAMANHO);
	}
}
