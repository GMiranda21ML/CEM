package br.com.cem.ui.backend.monitoria.feedback;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import java.util.Objects;
import java.util.Optional;

public record RegistrarFeedbackRequisicao(
		SessaoIdentificador sessao,
		UsuarioIdentificador aluno,
		double nota,
		Optional<String> comentario) {
	public RegistrarFeedbackRequisicao {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(comentario);
	}
}
