package br.com.cem.aplicacao.monitoria.disponibilidade;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class DisponibilidadeConsulta extends Consulta<DisponibilidadeOrdenacao> {
	private final Optional<String> texto;

	public DisponibilidadeConsulta(
			Optional<DisponibilidadeOrdenacao> ordenacao,
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
