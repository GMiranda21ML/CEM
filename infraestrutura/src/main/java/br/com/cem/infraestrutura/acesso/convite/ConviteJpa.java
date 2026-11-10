package br.com.cem.infraestrutura.acesso.convite;

import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteCodigo;
import br.com.cem.dominio.acesso.convite.ConviteEmail;
import br.com.cem.dominio.acesso.convite.ConviteSituacao;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CNV_CONVITE")
public class ConviteJpa implements IAgregadoJpa<Convite> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CNV_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "CNV_EMAIL")
	@NotNull
	@Size(max = ConviteEmail.TAMANHO)
	private String email;

	@Column(name = "CNV_PAPEL")
	@Enumerated(EnumType.STRING)
	@NotNull
	private Autorizacao papel;

	@Column(name = "CNV_CURSO")
	@NotNull
	@Size(max = Convite.CURSO_TAMANHO)
	private String curso;

	@Column(name = "CNV_CODIGO")
	@NotNull
	@Size(max = ConviteCodigo.TAMANHO)
	private String codigo;

	@Column(name = "CNV_ENVIADO_EM")
	@NotNull
	private LocalDateTime enviadoEm;

	@Column(name = "CNV_VALIDO_ATE")
	@NotNull
	private LocalDateTime validoAte;

	@Column(name = "CNV_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private ConviteSituacao situacao;

	protected ConviteJpa() {
	}

	public ConviteJpa(@Nullable Long identificador, String email, Autorizacao papel, String curso, String codigo,
			LocalDateTime enviadoEm, LocalDateTime validoAte, ConviteSituacao situacao) {
		this.identificador = identificador;
		this.email = email;
		this.papel = papel;
		this.curso = curso;
		this.codigo = codigo;
		this.enviadoEm = enviadoEm;
		this.validoAte = validoAte;
		this.situacao = situacao;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getEmail() {
		return email;
	}

	public Autorizacao getPapel() {
		return papel;
	}

	public String getCurso() {
		return curso;
	}

	public String getCodigo() {
		return codigo;
	}

	public LocalDateTime getEnviadoEm() {
		return enviadoEm;
	}

	public LocalDateTime getValidoAte() {
		return validoAte;
	}

	public ConviteSituacao getSituacao() {
		return situacao;
	}
}
