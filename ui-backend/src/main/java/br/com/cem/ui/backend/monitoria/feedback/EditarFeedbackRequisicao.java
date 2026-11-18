package br.com.cem.ui.backend.monitoria.feedback;

import java.util.Objects;
import java.util.Optional;

public record EditarFeedbackRequisicao(double nota, Optional<String> comentario) {
	public EditarFeedbackRequisicao {
		Objects.requireNonNull(comentario);
	}
}
