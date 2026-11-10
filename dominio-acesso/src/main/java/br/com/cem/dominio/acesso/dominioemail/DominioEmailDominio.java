package br.com.cem.dominio.acesso.dominioemail;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class DominioEmailDominio extends ObjetoValorRegexAbstrato implements IIdentificador {
	public static final int TAMANHO = 150;

	private static final String FORMATO = "[a-z0-9]+([-.][a-z0-9]+)*\\.[a-z]{2,}";

	private static final String MENSAGEM = "Domínio de e-mail inválido";

	public DominioEmailDominio(String valor) {
		super(valor, FORMATO, MENSAGEM);
	}
}
