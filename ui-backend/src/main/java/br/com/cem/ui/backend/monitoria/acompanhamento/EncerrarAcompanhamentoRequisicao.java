package br.com.cem.ui.backend.monitoria.acompanhamento;

import java.util.Objects;

public record EncerrarAcompanhamentoRequisicao(String parecerFinal) {
	public EncerrarAcompanhamentoRequisicao {
		Objects.requireNonNull(parecerFinal);
	}
}
