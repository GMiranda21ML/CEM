package br.com.cem.aplicacao.monitoria.feedback;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class FeedbackConsulta extends Consulta<FeedbackOrdenacao> {
	private final Optional<String> texto;

	public FeedbackConsulta(
			Optional<FeedbackOrdenacao> ordenacao,
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
