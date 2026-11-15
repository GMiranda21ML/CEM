package br.com.cem.aplicacao.monitoria.alerta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.AlertaIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.time.LocalDateTime;
import java.util.Objects;

public record AlertaProjecao(
		AlertaIdentificador identificador,
		UsuarioIdentificador pessoa,
		String papel,
		String disciplina,
		boolean acompanhado,
		LocalDateTime criadoEm)
		implements IProjecao<Alerta> {
	public AlertaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(pessoa);
		Objects.requireNonNull(papel);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(criadoEm);
	}

	public AlertaProjecao(
			Long identificador,
			Long pessoa,
			String papel,
			String disciplina,
			boolean acompanhado,
			LocalDateTime criadoEm) {
		this(
				new AlertaIdentificador(identificador.toString()),
				new UsuarioIdentificador(pessoa.toString()),
				papel,
				disciplina,
				acompanhado,
				criadoEm);
	}
}
