package br.com.cem.ui.backend.acesso.usuario;

import org.jspecify.annotations.Nullable;

public record AtualizarDadosRequisicao(String nome, @Nullable String curso, @Nullable String campus) {
}
