package br.com.cem.aplicacao.comunicacao.faq;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class FaqConsulta extends Consulta<FaqOrdenacao> {
	private final Optional<String> texto;

	public FaqConsulta(
			Optional<FaqOrdenacao> ordenacao,
			Optional<Boolean> descendente,
			Optional<Integer> indicePagina,
			Optional<Integer> tamanhoPagina,
			Optional<String> texto) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.texto = Objects.requireNonNull(texto);
	}

	public Optional<String> getTexto() {
		return texto;
	}
}
