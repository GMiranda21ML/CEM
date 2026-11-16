package br.com.cem.dominio.acesso.disciplina;

import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class DisciplinaCodigo extends ObjetoValorRegexAbstrato {
	public static final int TAMANHO = 7;

	private static final String FORMATO = "[A-Z]{3}[0-9]{4}";
	private static final String MENSAGEM = "O código da disciplina deve conter 3 letras maiúsculas seguidas de 4 números (ex.: EST1001)";

	public DisciplinaCodigo(String valor) {
		super(valor, FORMATO, MENSAGEM);
	}
}
