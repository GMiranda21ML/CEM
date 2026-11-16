package br.com.cem.dominio.acesso.curso;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class CursoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public static final int TAMANHO = 100;

	public CursoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador do curso em branco"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador do curso maior que %d caracteres", TAMANHO);
	}
}
