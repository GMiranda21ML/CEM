package br.com.cem.aplicacao.comunicacao.pergunta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record PerguntaProjecao(
		PerguntaIdentificador identificador,
		UsuarioIdentificador autor,
		String texto,
		String situacao,
		Optional<String> resposta,
		LocalDateTime criadaEm)
		implements IProjecao<Pergunta> {
	public PerguntaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(autor);
		Objects.requireNonNull(texto);
		Objects.requireNonNull(situacao);
		Objects.requireNonNull(resposta);
		Objects.requireNonNull(criadaEm);
	}

	public PerguntaProjecao(
			Long identificador,
			Long autor,
			String texto,
			String situacao,
			@Nullable String resposta,
			LocalDateTime criadaEm) {
		this(
				new PerguntaIdentificador(identificador.toString()),
				new UsuarioIdentificador(autor.toString()),
				texto,
				situacao,
				Optional.ofNullable(resposta),
				criadaEm);
	}
}
