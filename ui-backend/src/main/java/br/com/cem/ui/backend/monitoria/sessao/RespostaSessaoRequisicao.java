package br.com.cem.ui.backend.monitoria.sessao;

import br.com.cem.dominio.monitoria.sessao.SituacaoRespostaSessao;

import java.time.LocalDateTime;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

public record RespostaSessaoRequisicao(
		String aluno,
		SituacaoRespostaSessao situacao,
		@Nullable String motivo,
		@Nullable LocalDateTime inicioSugerido) {
	public RespostaSessaoRequisicao {
		Objects.requireNonNull(situacao);
	}
}
