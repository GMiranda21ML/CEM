package br.com.cem.aplicacao.monitoria.sessao;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class SessaoConsulta extends Consulta<SessaoOrdenacao> {
	private final Optional<String> texto;

	public SessaoConsulta(
			Optional<SessaoOrdenacao> ordenacao,
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
