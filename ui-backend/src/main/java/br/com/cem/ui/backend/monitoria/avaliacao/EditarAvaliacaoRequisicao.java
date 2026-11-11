package br.com.cem.ui.backend.monitoria.avaliacao;

import org.jspecify.annotations.Nullable;

public record EditarAvaliacaoRequisicao(int nota, @Nullable String comentario) {
}
