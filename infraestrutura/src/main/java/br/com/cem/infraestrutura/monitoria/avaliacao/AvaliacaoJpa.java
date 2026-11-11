package br.com.cem.infraestrutura.monitoria.avaliacao;

import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "AVL_AVALIACAO", uniqueConstraints = @UniqueConstraint(name = "UK_AVL_SESSAO_USUARIO", columnNames = {
		"AVL_SESSAO_ID", "USR_ID" }))
public class AvaliacaoJpa implements IAgregadoJpa<Avaliacao> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AVL_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "AVL_SESSAO_ID", updatable = false)
	@NotNull
	@Size(max = 100)
	private String sessao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID", updatable = false)
	@NotNull
	private UsuarioJpa autor;

	@Column(name = "AVL_PERFIL", updatable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private AvaliacaoPerfil perfil;

	@Column(name = "AVL_NOTA")
	@Min(1)
	@Max(5)
	private int nota;

	@Column(name = "AVL_COMENTARIO")
	@Size(max = Avaliacao.COMENTARIO_TAMANHO)
	private @Nullable String comentario;

	@Column(name = "AVL_REGISTRADA_EM", updatable = false)
	@NotNull
	private LocalDateTime registradaEm;

	@Column(name = "AVL_ATUALIZADA_EM")
	@NotNull
	private LocalDateTime atualizadaEm;

	protected AvaliacaoJpa() {
	}

	public AvaliacaoJpa(@Nullable Long identificador, String sessao, UsuarioJpa autor, AvaliacaoPerfil perfil, int nota,
			@Nullable String comentario, LocalDateTime registradaEm, LocalDateTime atualizadaEm) {
		this.identificador = identificador;
		this.sessao = sessao;
		this.autor = autor;
		this.perfil = perfil;
		this.nota = nota;
		this.comentario = comentario;
		this.registradaEm = registradaEm;
		this.atualizadaEm = atualizadaEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getSessao() {
		return sessao;
	}

	public UsuarioJpa getAutor() {
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
}
