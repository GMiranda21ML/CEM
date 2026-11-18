package br.com.cem.ui.backend.monitoria.selecao;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;

import java.time.LocalDate;
import java.util.Objects;

public record AbrirSelecaoRequisicao(
		DisciplinaIdentificador disciplina,
		LocalDate prazoInscricao,
		int vagas,
		double crMinimo,
		int pesoCr,
		int pesoEntrevista,
		double patamarMinimo) {
	public AbrirSelecaoRequisicao {
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(prazoInscricao);
	}
}
