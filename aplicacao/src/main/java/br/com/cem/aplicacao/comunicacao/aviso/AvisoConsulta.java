package br.com.cem.aplicacao.comunicacao.aviso;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class AvisoConsulta extends Consulta<AvisoOrdenacao> {
	private final Optional<String> texto;

	public AvisoConsulta(
			Optional<AvisoOrdenacao> ordenacao,
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
