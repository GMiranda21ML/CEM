package br.com.cem.aplicacao.monitoria.selecao;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;

import java.util.Objects;

public record DisciplinaProfessorProjecao(
		DisciplinaIdentificador identificador, String codigo, String nome, String cursoNome) {
	public DisciplinaProfessorProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(codigo);
		Objects.requireNonNull(nome);
		Objects.requireNonNull(cursoNome);
	}

	public DisciplinaProfessorProjecao(
			Long identificador, String codigo, String nome, String cursoNome) {
		this(new DisciplinaIdentificador(identificador.toString()), codigo, nome, cursoNome);
	}
}
