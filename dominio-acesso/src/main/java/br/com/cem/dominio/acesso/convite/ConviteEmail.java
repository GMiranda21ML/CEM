package br.com.cem.dominio.acesso.convite;

import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class ConviteEmail extends ObjetoValorRegexAbstrato {
	public static final int TAMANHO = 180;

	private static final String FORMATO = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";

	private static final String MENSAGEM = "E-mail institucional inválido";

	public ConviteEmail(String valor) {
		super(valor, FORMATO, MENSAGEM);
	}
}
