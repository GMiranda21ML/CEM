package br.com.cem.aplicacao.monitoria.falta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record FaltaProjecao(
		FaltaIdentificador identificador,
		SessaoIdentificador sessao,
		UsuarioIdentificador pessoa,
		String papel,
		UsuarioIdentificador autor,
		Optional<String> observacao,
		LocalDateTime registradaEm,
		Optional<SessaoIdentificador> reposicao)
		implements IProjecao<Falta> {
	public FaltaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(pessoa);
		Objects.requireNonNull(papel);
		Objects.requireNonNull(autor);
		Objects.requireNonNull(observacao);
		Objects.requireNonNull(registradaEm);
		Objects.requireNonNull(reposicao);
	}

	public FaltaProjecao(
			Long identificador,
			Long sessao,
			Long pessoa,
			String papel,
			Long autor,
			@Nullable String observacao,
			LocalDateTime registradaEm,
			@Nullable Long reposicao) {
		this(
				new FaltaIdentificador(identificador.toString()),
				new SessaoIdentificador(sessao.toString()),
				new UsuarioIdentificador(pessoa.toString()),
				papel,
				new UsuarioIdentificador(autor.toString()),
				Optional.ofNullable(observacao),
				registradaEm,
				Optional.ofNullable(reposicao).map(id -> new SessaoIdentificador(id.toString())));
	}
}
