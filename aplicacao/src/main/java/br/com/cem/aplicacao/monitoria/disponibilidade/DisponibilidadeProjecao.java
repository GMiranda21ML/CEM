package br.com.cem.aplicacao.monitoria.disponibilidade;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.time.LocalTime;
import java.util.Objects;

public record DisponibilidadeProjecao(
		DisponibilidadeIdentificador identificador,
		String disciplina,
		UsuarioIdentificador monitor,
		CampusIdentificador campus,
		int diaSemana,
		LocalTime inicio,
		LocalTime fim)
		implements IProjecao<Disponibilidade> {
	public DisponibilidadeProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(monitor);
		Objects.requireNonNull(campus);
		Objects.requireNonNull(inicio);
		Objects.requireNonNull(fim);
	}

	public DisponibilidadeProjecao(
			Long identificador,
			String disciplina,
			Long monitor,
			Long campus,
			int diaSemana,
			LocalTime inicio,
			LocalTime fim) {
		this(
				new DisponibilidadeIdentificador(identificador.toString()),
				disciplina,
				new UsuarioIdentificador(monitor.toString()),
				new CampusIdentificador(campus.toString()),
				diaSemana,
				inicio,
				fim);
	}
}
