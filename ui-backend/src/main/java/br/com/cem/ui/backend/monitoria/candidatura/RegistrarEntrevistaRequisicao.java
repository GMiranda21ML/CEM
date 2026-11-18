package br.com.cem.ui.backend.monitoria.candidatura;

import java.util.Objects;
import java.util.Optional;

public record RegistrarEntrevistaRequisicao(double nota, Optional<String> comentario) {
	public RegistrarEntrevistaRequisicao {
		Objects.requireNonNull(comentario);
	}
}
