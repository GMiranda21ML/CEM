package br.com.cem.dominio.monitoria.feedback;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class Feedback implements IAgregado<FeedbackIdentificador> {
	public static final int COMENTARIO_TAMANHO = 1000;

	private final Optional<FeedbackIdentificador> identificador;
	private final SessaoIdentificador sessao;
	private final UsuarioIdentificador aluno;
	private final UsuarioIdentificador autor;
	private final double nota;
	private final Optional<String> comentario;
	private final LocalDate criadoEm;
	private final Optional<LocalDate> atualizadoEm;

	public Feedback(
			Optional<FeedbackIdentificador> identificador,
			SessaoIdentificador sessao,
			UsuarioIdentificador aluno,
			UsuarioIdentificador autor,
			double nota,
			Optional<String> comentario,
			LocalDate criadoEm,
			Optional<LocalDate> atualizadoEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.sessao = Objects.requireNonNull(sessao);
		this.aluno = Objects.requireNonNull(aluno);
		this.autor = Objects.requireNonNull(autor);
		Validate.isTrue(
				Double.isFinite(nota) && nota >= 0.0 && nota <= 10.0,
				"A nota deve estar entre 0 e 10");
		this.nota = nota;
		this.comentario =
				Objects.requireNonNull(comentario)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		this.comentario.ifPresent(
				texto ->
						Validate.isTrue(
								texto.length() <= COMENTARIO_TAMANHO,
								"Comentário deve ter até %d caracteres",
								COMENTARIO_TAMANHO));
		this.criadoEm = Objects.requireNonNull(criadoEm);
		this.atualizadoEm = Objects.requireNonNull(atualizadoEm);
		this.atualizadoEm.ifPresent(
				data ->
						Validate.isTrue(
								!data.isBefore(criadoEm),
								"A atualização não pode ser anterior à criação"));
	}

	@Override
	public Optional<FeedbackIdentificador> getIdentificador() {
		return identificador;
	}

	public SessaoIdentificador getSessao() {
		return sessao;
	}

	public UsuarioIdentificador getAluno() {
		return aluno;
	}

	public UsuarioIdentificador getAutor() {
		return autor;
	}

	public double getNota() {
		return nota;
	}

	public Optional<String> getComentario() {
		return comentario;
	}

	public LocalDate getCriadoEm() {
		return criadoEm;
	}

	public Optional<LocalDate> getAtualizadoEm() {
		return atualizadoEm;
	}

	public Feedback editar(double novaNota, Optional<String> novoComentario, LocalDate dataEdicao) {
		Objects.requireNonNull(novoComentario);
		Objects.requireNonNull(dataEdicao);
		return new Feedback(
				identificador,
				sessao,
				aluno,
				autor,
				novaNota,
				novoComentario,
				criadoEm,
				Optional.of(dataEdicao));
	}
}
