package br.com.cem.infraestrutura.monitoria.falta;

import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.SessaoJpa;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "FLT_FALTA",
		uniqueConstraints =
				@UniqueConstraint(
						name = "UK_FLT_SESSAO_PESSOA",
						columnNames = {"SES_ID", "FLT_PESSOA_ID"}))
public class FaltaJpa implements IAgregadoJpa<Falta> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FLT_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_ID")
	@NotNull
	private SessaoJpa sessao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FLT_PESSOA_ID")
	@NotNull
	private UsuarioJpa pessoa;

	@Column(name = "FLT_PAPEL")
	@NotNull
	@Size(max = Falta.PAPEL_TAMANHO)
	private String papel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FLT_AUTOR_ID")
	@NotNull
	private UsuarioJpa autor;

	@Column(name = "FLT_OBSERVACAO")
	@Size(max = Falta.OBSERVACAO_TAMANHO)
	private @Nullable String observacao;

	@Column(name = "FLT_REGISTRADA_EM")
	@NotNull
	private LocalDateTime registradaEm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_REPOSICAO_ID")
	private @Nullable SessaoJpa reposicao;

	protected FaltaJpa() {}

	public FaltaJpa(
			@Nullable Long identificador,
			SessaoJpa sessao,
			UsuarioJpa pessoa,
			String papel,
			UsuarioJpa autor,
			@Nullable String observacao,
			LocalDateTime registradaEm,
			@Nullable SessaoJpa reposicao) {
		this.identificador = identificador;
		this.sessao = sessao;
		this.pessoa = pessoa;
		this.papel = papel;
		this.autor = autor;
		this.observacao = observacao;
		this.registradaEm = registradaEm;
		this.reposicao = reposicao;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public SessaoJpa getSessao() {
		return sessao;
	}

	public UsuarioJpa getPessoa() {
		return pessoa;
	}

	public String getPapel() {
		return papel;
	}

	public UsuarioJpa getAutor() {
		return autor;
	}

	public @Nullable String getObservacao() {
		return observacao;
	}

	public LocalDateTime getRegistradaEm() {
		return registradaEm;
	}

	public @Nullable SessaoJpa getReposicao() {
		return reposicao;
	}
}
