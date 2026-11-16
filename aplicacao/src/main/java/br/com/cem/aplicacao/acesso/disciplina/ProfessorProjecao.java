package br.com.cem.aplicacao.acesso.disciplina;

import java.util.Objects;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

public record ProfessorProjecao(UsuarioIdentificador identificador, String nome) {
	public ProfessorProjecao { Objects.requireNonNull(identificador); Objects.requireNonNull(nome); }
	public ProfessorProjecao(Long identificador, String nome) {
		this(new UsuarioIdentificador(identificador.toString()), nome);
	}
}
