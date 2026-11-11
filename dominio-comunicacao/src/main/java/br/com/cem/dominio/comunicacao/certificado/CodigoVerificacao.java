package br.com.cem.dominio.comunicacao.certificado;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorRegexAbstrato;

public final class CodigoVerificacao extends ObjetoValorRegexAbstrato implements IIdentificador {
	public static final int TAMANHO = 36;
	private static final String FORMATO = "CEM-[A-Z0-9-]{6,32}";

	public CodigoVerificacao(String valor) {
		super(valor, FORMATO, "Código de verificação inválido");
	}
}
