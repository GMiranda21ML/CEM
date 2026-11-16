package br.com.cem.ui.backend.acesso.disciplina;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

public record EditarDisciplinaRequisicao(String nome, String area, UsuarioIdentificador professor) {}
