package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.dominio.monitoria.sessao.RespostaSessao;
import br.com.cem.dominio.monitoria.sessao.SituacaoRespostaSessao;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Embeddable
public class RespostaSessaoJpa {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID")
	@NotNull
	private UsuarioJpa aluno;

	@Enumerated(EnumType.STRING)
	@Column(name = "SSR_SITUACAO")
	@NotNull
	private SituacaoRespostaSessao situacao;

	@Column(name = "SSR_MOTIVO")
	@Size(max = RespostaSessao.MOTIVO_TAMANHO)
	private @Nullable String motivo;

	@Column(name = "SSR_INICIO_SUGERIDO")
	private @Nullable LocalDateTime inicioSugerido;

	protected RespostaSessaoJpa() {}

	public RespostaSessaoJpa(
			UsuarioJpa aluno,
			SituacaoRespostaSessao situacao,
			@Nullable String motivo,
			@Nullable LocalDateTime inicioSugerido) {
		this.aluno = aluno;
		this.situacao = situacao;
		this.motivo = motivo;
		this.inicioSugerido = inicioSugerido;
	}

	public UsuarioJpa getAluno() {
		return aluno;
	}

	public SituacaoRespostaSessao getSituacao() {
		return situacao;
	}

	public @Nullable String getMotivo() {
		return motivo;
	}

	public @Nullable LocalDateTime getInicioSugerido() {
		return inicioSugerido;
	}
}
