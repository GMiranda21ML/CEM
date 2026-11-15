package br.com.cem.aplicacao.comunicacao.faq;

import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.util.Objects;

public record FaqProjecao(
		FaqIdentificador identificador, String pergunta, String resposta, boolean ativo)
		implements IProjecao<Faq> {
	public FaqProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(pergunta);
		Objects.requireNonNull(resposta);
	}

	public FaqProjecao(Long identificador, String pergunta, String resposta, boolean ativo) {
		this(new FaqIdentificador(identificador.toString()), pergunta, resposta, ativo);
	}
}
