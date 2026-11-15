package br.com.cem.aplicacao.monitoria.sessao;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.time.LocalDateTime;
import java.util.Objects;

public record SessaoProjecao(
		SessaoIdentificador identificador,
		String disciplina,
		UsuarioIdentificador professor,
		UsuarioIdentificador monitor,
		CampusIdentificador campus,
		LocalDateTime inicio,
		LocalDateTime fim,
		boolean cancelada)
		implements IProjecao<Sessao> {
	public SessaoProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(professor);
		Objects.requireNonNull(monitor);
		Objects.requireNonNull(campus);
		Objects.requireNonNull(inicio);
		Objects.requireNonNull(fim);
	}

	public SessaoProjecao(
			Long identificador,
			String disciplina,
			Long professor,
			Long monitor,
			Long campus,
			LocalDateTime inicio,
			LocalDateTime fim,
			boolean cancelada) {
		this(
				new SessaoIdentificador(identificador.toString()),
				disciplina,
				new UsuarioIdentificador(professor.toString()),
				new UsuarioIdentificador(monitor.toString()),
				new CampusIdentificador(campus.toString()),
				inicio,
				fim,
				cancelada);
	}
}
