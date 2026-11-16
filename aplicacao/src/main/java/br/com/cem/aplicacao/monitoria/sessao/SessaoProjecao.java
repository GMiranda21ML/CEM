package br.com.cem.aplicacao.monitoria.sessao;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.time.LocalDateTime;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record SessaoProjecao(
		SessaoIdentificador identificador,
		String disciplina,
		UsuarioIdentificador professor,
		UsuarioIdentificador monitor,
		CampusIdentificador campus,
		String sala,
		LocalDateTime inicio,
		LocalDateTime fim,
		boolean cancelada,
		@Nullable LocalDateTime inicioAnterior)
		implements IProjecao<Sessao> {
	public SessaoProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(professor);
		Objects.requireNonNull(monitor);
		Objects.requireNonNull(campus);
		Objects.requireNonNull(sala);
		Objects.requireNonNull(inicio);
		Objects.requireNonNull(fim);
	}

	public SessaoProjecao(
			Long identificador,
			String disciplina,
			Long professor,
			Long monitor,
			Long campus,
		String sala,
			LocalDateTime inicio,
			LocalDateTime fim,
		boolean cancelada,
		@Nullable LocalDateTime inicioAnterior) {
		this(
				new SessaoIdentificador(identificador.toString()),
				disciplina,
				new UsuarioIdentificador(professor.toString()),
				new UsuarioIdentificador(monitor.toString()),
				new CampusIdentificador(campus.toString()),
				sala,
				inicio,
				fim,
				cancelada,
				inicioAnterior);
	}
}
