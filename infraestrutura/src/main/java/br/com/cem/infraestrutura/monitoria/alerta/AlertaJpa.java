package br.com.cem.infraestrutura.monitoria.alerta;

import br.com.cem.dominio.monitoria.alerta.Alerta;
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
@Table(name = "ALF_ALERTA")
public class AlertaJpa implements IAgregadoJpa<Alerta> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALF_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALF_PESSOA_ID")
	@NotNull
	private UsuarioJpa pessoa;

	@Column(name = "ALF_PAPEL")
	@NotNull
	@Size(max = Alerta.PAPEL_TAMANHO)
	private String papel;

	@Column(name = "ALF_DISCIPLINA")
	@NotNull
	@Size(max = Alerta.DISCIPLINA_TAMANHO)
	private String disciplina;

	@Column(name = "ALF_ACOMPANHADO")
	private boolean acompanhado;

	@Column(name = "ALF_CRIADO_EM")
	@NotNull
	private LocalDateTime criadoEm;

	protected AlertaJpa() {}

	public AlertaJpa(
			@Nullable Long identificador,
			UsuarioJpa pessoa,
			String papel,
			String disciplina,
			boolean acompanhado,
			LocalDateTime criadoEm) {
		this.identificador = identificador;
		this.pessoa = pessoa;
		this.papel = papel;
		this.disciplina = disciplina;
		this.acompanhado = acompanhado;
		this.criadoEm = criadoEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public UsuarioJpa getPessoa() {
		return pessoa;
	}

	public String getPapel() {
		return papel;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public boolean getAcompanhado() {
		return acompanhado;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}
}
