package br.com.cem.ui.backend.acesso.campus;

import org.jspecify.annotations.Nullable;

public record CadastrarCampusRequisicao(String nome, @Nullable String endereco) {
}
