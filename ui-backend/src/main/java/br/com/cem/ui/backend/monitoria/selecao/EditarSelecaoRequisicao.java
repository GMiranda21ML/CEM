package br.com.cem.ui.backend.monitoria.selecao;

import java.time.LocalDate;
import java.util.Objects;

public record EditarSelecaoRequisicao(
		LocalDate prazoInscricao,
		int vagas,
		double crMinimo,
		int pesoCr,
		int pesoEntrevista,
		double patamarMinimo) {
	public EditarSelecaoRequisicao {
		Objects.requireNonNull(prazoInscricao);
	}
}
