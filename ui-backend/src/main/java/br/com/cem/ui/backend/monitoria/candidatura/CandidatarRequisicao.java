package br.com.cem.ui.backend.monitoria.candidatura;

import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import java.util.Objects;

public record CandidatarRequisicao(SelecaoIdentificador selecao, double cr) {
	public CandidatarRequisicao {
		Objects.requireNonNull(selecao);
	}
}
