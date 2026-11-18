package br.com.cem.aplicacao.monitoria.candidatura;

import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

import java.util.Objects;
import java.util.Optional;

public final class CandidaturaConsulta extends Consulta<CandidaturaOrdenacao> {
	private final Optional<String> texto;
	private final Optional<SelecaoIdentificador> selecao;

	public CandidaturaConsulta(
			Optional<CandidaturaOrdenacao> ordenacao,
			Optional<Boolean> descendente,
			Optional<Integer> indicePagina,
			Optional<Integer> tamanhoPagina,
			Optional<String> texto,
			Optional<SelecaoIdentificador> selecao) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.texto = Objects.requireNonNull(texto);
		this.selecao = Objects.requireNonNull(selecao);
	}

	public Optional<String> getTexto() {
		return texto;
	}

	public Optional<SelecaoIdentificador> getSelecao() {
		return selecao;
	}
}
