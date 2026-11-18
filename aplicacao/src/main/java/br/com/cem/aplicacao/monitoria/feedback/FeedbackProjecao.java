package br.com.cem.aplicacao.monitoria.feedback;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record FeedbackProjecao(
		FeedbackIdentificador identificador,
		SessaoIdentificador sessao,
		LocalDateTime sessaoInicio,
		String disciplinaNome,
		UsuarioIdentificador aluno,
		String alunoNome,
		UsuarioIdentificador autor,
		double nota,
		Optional<String> comentario,
		LocalDate criadoEm,
		Optional<LocalDate> atualizadoEm,
		boolean bloqueado)
		implements IProjecao<Feedback> {
	public FeedbackProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(sessaoInicio);
		Objects.requireNonNull(disciplinaNome);
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(alunoNome);
		Objects.requireNonNull(autor);
		Objects.requireNonNull(comentario);
		Objects.requireNonNull(criadoEm);
		Objects.requireNonNull(atualizadoEm);
	}

	public FeedbackProjecao(
			Long identificador,
			Long sessao,
			LocalDateTime sessaoInicio,
			String disciplinaNome,
			Long aluno,
			String alunoNome,
			Long autor,
			double nota,
			@Nullable String comentario,
			LocalDate criadoEm,
			@Nullable LocalDate atualizadoEm,
			boolean bloqueado) {
		this(
				new FeedbackIdentificador(identificador.toString()),
				new SessaoIdentificador(sessao.toString()),
				sessaoInicio,
				disciplinaNome,
				new UsuarioIdentificador(aluno.toString()),
				alunoNome,
				new UsuarioIdentificador(autor.toString()),
				nota,
				Optional.ofNullable(comentario),
				criadoEm,
				Optional.ofNullable(atualizadoEm),
				bloqueado);
	}
}
