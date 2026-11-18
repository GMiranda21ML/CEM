package br.com.cem.ui.backend.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import java.util.Objects;

public record AbrirAcompanhamentoRequisicao(
		UsuarioIdentificador aluno,
		DisciplinaIdentificador disciplina,
		double metaNota,
		int metaFrequencia) {
	public AbrirAcompanhamentoRequisicao {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(disciplina);
	}
}
