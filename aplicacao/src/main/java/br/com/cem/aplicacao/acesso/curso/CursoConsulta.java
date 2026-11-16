package br.com.cem.aplicacao.acesso.curso;

import java.util.Objects;
import java.util.Optional;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class CursoConsulta extends Consulta<CursoOrdenacao> {
	private final Optional<String> texto;

	public CursoConsulta(Optional<CursoOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<String> texto) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.texto = Objects.requireNonNull(texto);
	}

	public Optional<String> getTexto() { return texto; }
}
