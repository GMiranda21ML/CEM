package br.com.cem.dominio.acesso.usuario;

import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class UsuarioEmail extends ObjetoValorRegexAbstrato {
	public static final int TAMANHO = 180;

	private static final String FORMATO = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";

	private static final String MENSAGEM = "E-mail institucional inválido";

	public UsuarioEmail(String valor) {
		super(valor, FORMATO, MENSAGEM);
	}

	public String getDominio() {
		var valor = toString();
		return valor.substring(valor.indexOf('@') + 1).toLowerCase();
	}
}
