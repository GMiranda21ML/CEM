package br.com.cem.dominio.acesso.disciplina;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class DisciplinaIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public static final int TAMANHO = 100;

	public DisciplinaIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador da disciplina em branco"));
		Validate.isTrue(valor.length() <= TAMANHO, "Identificador da disciplina maior que %d caracteres", TAMANHO);
	}
}
