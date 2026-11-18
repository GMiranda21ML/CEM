package br.com.cem.aplicacao.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import java.util.Objects;

public record AlunoDisciplinaOpcaoProjecao(
		UsuarioIdentificador aluno,
		String alunoNome,
		DisciplinaIdentificador disciplina,
		String disciplinaNome,
		String cursoNome) {
	public AlunoDisciplinaOpcaoProjecao {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(alunoNome);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(disciplinaNome);
		Objects.requireNonNull(cursoNome);
	}

	public AlunoDisciplinaOpcaoProjecao(
			Long aluno, String alunoNome, Long disciplina, String disciplinaNome, String cursoNome) {
		this(
				new UsuarioIdentificador(aluno.toString()),
				alunoNome,
				new DisciplinaIdentificador(disciplina.toString()),
				disciplinaNome,
				cursoNome);
	}
}
