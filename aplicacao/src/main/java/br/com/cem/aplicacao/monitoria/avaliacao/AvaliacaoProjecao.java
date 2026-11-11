package br.com.cem.aplicacao.monitoria.avaliacao;

import java.time.LocalDateTime;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class AvaliacaoProjecao implements IProjecao<Avaliacao> {
	private final AvaliacaoIdentificador identificador;
	private final SessaoIdentificador sessao;
	private final UsuarioIdentificador autor;
	private final String autorNome;
	private final AvaliacaoPerfil perfil;
	private final int nota;
	private final @Nullable String comentario;
	private final LocalDateTime registradaEm;
	private final LocalDateTime atualizadaEm;

	public AvaliacaoProjecao(AvaliacaoIdentificador identificador, SessaoIdentificador sessao,
			UsuarioIdentificador autor, String autorNome, AvaliacaoPerfil perfil, int nota, @Nullable String comentario,
			LocalDateTime registradaEm, LocalDateTime atualizadaEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.sessao = Objects.requireNonNull(sessao);
		this.autor = Objects.requireNonNull(autor);
		this.autorNome = Objects.requireNonNull(autorNome);
		this.perfil = Objects.requireNonNull(perfil);
		this.nota = nota;
		this.comentario = comentario;
		this.registradaEm = Objects.requireNonNull(registradaEm);
		this.atualizadaEm = Objects.requireNonNull(atualizadaEm);
	}

	public AvaliacaoProjecao(Long identificador, String sessao, Long autor, String autorNome, AvaliacaoPerfil perfil,
			int nota, @Nullable String comentario, LocalDateTime registradaEm, LocalDateTime atualizadaEm) {
		this(new AvaliacaoIdentificador(identificador.toString()), new SessaoIdentificador(sessao),
				new UsuarioIdentificador(autor.toString()), autorNome, perfil, nota, comentario, registradaEm,
				atualizadaEm);
	}

	public AvaliacaoIdentificador getIdentificador() {
		return identificador;
	}

	public SessaoIdentificador getSessao() {
		return sessao;
	}

	public UsuarioIdentificador getAutor() {
		return autor;
	}

	public String getAutorNome() {
		return autorNome;
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
}
