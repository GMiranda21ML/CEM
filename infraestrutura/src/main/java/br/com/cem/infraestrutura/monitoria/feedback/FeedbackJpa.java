package br.com.cem.infraestrutura.monitoria.feedback;

import br.com.cem.dominio.monitoria.feedback.Feedback;
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

import java.time.LocalDate;

@Entity
@Table(
		name = "FDB_FEEDBACK",
		uniqueConstraints = {
			@UniqueConstraint(
					name = "UQ_FDB_SESSAO_ALUNO",
					columnNames = {"SES_ID", "FDB_ALUNO_ID"})
		})
public class FeedbackJpa implements IAgregadoJpa<Feedback> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FDB_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_ID")
	@NotNull
	private SessaoJpa sessao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDB_ALUNO_ID")
	@NotNull
	private UsuarioJpa aluno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDB_AUTOR_ID")
	@NotNull
	private UsuarioJpa autor;

	@Column(name = "FDB_NOTA")
	private double nota;

	@Column(name = "FDB_COMENTARIO")
	@Size(max = Feedback.COMENTARIO_TAMANHO)
	private @Nullable String comentario;

	@Column(name = "FDB_CRIADO_EM", updatable = false)
	@NotNull
	private LocalDate criadoEm;

	@Column(name = "FDB_ATUALIZADO_EM")
	private @Nullable LocalDate atualizadoEm;

	protected FeedbackJpa() {}

	public FeedbackJpa(
			@Nullable Long identificador,
			SessaoJpa sessao,
			UsuarioJpa aluno,
			UsuarioJpa autor,
			double nota,
			@Nullable String comentario,
			LocalDate criadoEm,
			@Nullable LocalDate atualizadoEm) {
		this.identificador = identificador;
		this.sessao = sessao;
		this.aluno = aluno;
		this.autor = autor;
		this.nota = nota;
		this.comentario = comentario;
		this.criadoEm = criadoEm;
		this.atualizadoEm = atualizadoEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public SessaoJpa getSessao() {
		return sessao;
	}

	public UsuarioJpa getAluno() {
		return aluno;
	}

	public UsuarioJpa getAutor() {
		return autor;
	}

	public double getNota() {
		return nota;
	}

	public @Nullable String getComentario() {
		return comentario;
	}

	public LocalDate getCriadoEm() {
		return criadoEm;
	}

	public @Nullable LocalDate getAtualizadoEm() {
		return atualizadoEm;
	}
}
