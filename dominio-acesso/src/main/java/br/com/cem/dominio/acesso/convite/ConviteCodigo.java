package br.com.cem.dominio.acesso.convite;

import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class ConviteCodigo extends ObjetoValorRegexAbstrato {
	public static final int TAMANHO = 6;

	private static final String FORMATO = "\\d{" + TAMANHO + "}";

	private static final String MENSAGEM = "O código de verificação deve conter " + TAMANHO + " dígitos";

	public ConviteCodigo(String valor) {
		super(valor, FORMATO, MENSAGEM);
	}
}
