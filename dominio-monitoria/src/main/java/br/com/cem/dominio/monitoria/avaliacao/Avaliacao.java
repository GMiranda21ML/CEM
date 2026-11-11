package br.com.cem.dominio.monitoria.avaliacao;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Avaliacao implements IAgregado<AvaliacaoIdentificador> {
	public static final int COMENTARIO_TAMANHO = 1000;

	private final Optional<AvaliacaoIdentificador> identificador;
	private final SessaoIdentificador sessao;
	private final UsuarioIdentificador autor;
	private final AvaliacaoPerfil perfil;
	private final int nota;
	private final @Nullable String comentario;
	private final LocalDateTime registradaEm;
	private final LocalDateTime atualizadaEm;

	public Avaliacao(Optional<AvaliacaoIdentificador> identificador, SessaoIdentificador sessao,
			UsuarioIdentificador autor, AvaliacaoPerfil perfil, int nota, @Nullable String comentario,
			LocalDateTime registradaEm, LocalDateTime atualizadaEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.sessao = Objects.requireNonNull(sessao);
		this.autor = Objects.requireNonNull(autor);
		this.perfil = Objects.requireNonNull(perfil);
		Validate.inclusiveBetween(1, 5, nota, "A nota deve ser um número inteiro entre 1 e 5");
		this.nota = nota;
		if (comentario != null) {
			Validate.isTrue(comentario.length() <= COMENTARIO_TAMANHO,
					"Comentário da avaliação maior que %d caracteres", COMENTARIO_TAMANHO);
		}
		this.comentario = comentario;
		this.registradaEm = Objects.requireNonNull(registradaEm);
		this.atualizadaEm = Objects.requireNonNull(atualizadaEm);
		Validate.isTrue(!atualizadaEm.isBefore(registradaEm),
				"A atualização da avaliação não pode ser anterior ao registro");
	}

	@Override
	public Optional<AvaliacaoIdentificador> getIdentificador() {
		return identificador;
	}

	public SessaoIdentificador getSessao() {
		return sessao;
	}

	public UsuarioIdentificador getAutor() {
		return autor;
	}

	public AvaliacaoPerfil getPerfil() {
		return perfil;
	}

	public int getNota() {
		return nota;
	}

	public @Nullable String getComentario() {
		return comentario;
	}

	public LocalDateTime getRegistradaEm() {
		return registradaEm;
	}

	public LocalDateTime getAtualizadaEm() {
		return atualizadaEm;
	}

	public Avaliacao editar(int novaNota, @Nullable String novoComentario, LocalDateTime atualizacao) {
		return new Avaliacao(identificador, sessao, autor, perfil, novaNota, novoComentario, registradaEm, atualizacao);
	}
}
