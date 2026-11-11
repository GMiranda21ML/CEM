package br.com.cem.ui.backend.monitoria.avaliacao;

import org.jspecify.annotations.Nullable;

public record RegistrarAvaliacaoRequisicao(String sessao, int nota, @Nullable String comentario) {
}
