package br.com.cem.aplicacao.monitoria.selecao;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class SelecaoConsulta extends Consulta<SelecaoOrdenacao> {
	private final Optional<String> texto;

	public SelecaoConsulta(
			Optional<SelecaoOrdenacao> ordenacao,
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
