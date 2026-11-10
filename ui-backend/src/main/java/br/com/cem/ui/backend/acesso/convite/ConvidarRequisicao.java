package br.com.cem.ui.backend.acesso.convite;

import br.com.cem.dominio.Autorizacao;

public record ConvidarRequisicao(String email, Autorizacao papel, String curso) {
}
