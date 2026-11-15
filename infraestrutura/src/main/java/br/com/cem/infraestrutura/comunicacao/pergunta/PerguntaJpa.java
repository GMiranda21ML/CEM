package br.com.cem.infraestrutura.comunicacao.pergunta;

import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRG_PERGUNTA")
public class PerguntaJpa implements IAgregadoJpa<Pergunta> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRG_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRG_AUTOR_ID")
	@NotNull
	private UsuarioJpa autor;

	@Column(name = "PRG_TEXTO")
	@NotNull
	@Size(max = Pergunta.TEXTO_TAMANHO)
	private String texto;

	@Column(name = "PRG_SITUACAO")
	@NotNull
	@Size(max = Pergunta.SITUACAO_TAMANHO)
	private String situacao;

	@Column(name = "PRG_RESPOSTA")
	@Size(max = Pergunta.RESPOSTA_TAMANHO)
	private @Nullable String resposta;

	@Column(name = "PRG_CRIADA_EM")
	@NotNull
	private LocalDateTime criadaEm;

	protected PerguntaJpa() {}

	public PerguntaJpa(
			@Nullable Long identificador,
			UsuarioJpa autor,
			String texto,
			String situacao,
			@Nullable String resposta,
			LocalDateTime criadaEm) {
		this.identificador = identificador;
		this.autor = autor;
		this.texto = texto;
		this.situacao = situacao;
		this.resposta = resposta;
		this.criadaEm = criadaEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public UsuarioJpa getAutor() {
		return autor;
	}

	public String getTexto() {
		return texto;
	}

	public String getSituacao() {
		return situacao;
	}

	public @Nullable String getResposta() {
		return resposta;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}
}
