package br.com.cem.infraestrutura.monitoria.acompanhamento;

import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaJpa;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

@Entity
@Table(
		name = "ACM_ACOMPANHAMENTO",
		indexes = {
			@Index(
					name = "IX_ACM_ALUNO_DISCIPLINA_SITUACAO",
					columnList = "ACM_ALUNO_ID,DSC_ID,ACM_SITUACAO")
		})
public class AcompanhamentoJpa implements IAgregadoJpa<Acompanhamento> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ACM_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACM_ALUNO_ID")
	@NotNull
	private UsuarioJpa aluno;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DSC_ID")
	@NotNull
	private DisciplinaJpa disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACM_MONITOR_ID")
	@NotNull
	private UsuarioJpa monitor;

	@Column(name = "ACM_SEMESTRE")
	@NotNull
	@Size(max = Acompanhamento.SEMESTRE_TAMANHO)
	private String semestre;

	@Column(name = "ACM_META_NOTA")
	private double metaNota;

	@Column(name = "ACM_META_FREQUENCIA")
	private int metaFrequencia;

	@Column(name = "ACM_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private AcompanhamentoSituacao situacao;

	@Column(name = "ACM_ABERTO_EM", updatable = false)
	@NotNull
	private LocalDate abertoEm;

	@Column(name = "ACM_ENCERRADO_EM")
	private @Nullable LocalDate encerradoEm;

	@Column(name = "ACM_PARECER_FINAL")
	@Size(max = Acompanhamento.PARECER_TAMANHO)
	private @Nullable String parecerFinal;

	protected AcompanhamentoJpa() {}

	public AcompanhamentoJpa(
			@Nullable Long identificador,
			UsuarioJpa aluno,
			DisciplinaJpa disciplina,
			UsuarioJpa monitor,
			String semestre,
			double metaNota,
			int metaFrequencia,
			AcompanhamentoSituacao situacao,
			LocalDate abertoEm,
			@Nullable LocalDate encerradoEm,
			@Nullable String parecerFinal) {
		this.identificador = identificador;
		this.aluno = aluno;
		this.disciplina = disciplina;
		this.monitor = monitor;
		this.semestre = semestre;
		this.metaNota = metaNota;
		this.metaFrequencia = metaFrequencia;
		this.situacao = situacao;
		this.abertoEm = abertoEm;
		this.encerradoEm = encerradoEm;
		this.parecerFinal = parecerFinal;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public UsuarioJpa getAluno() {
		return aluno;
	}

	public DisciplinaJpa getDisciplina() {
		return disciplina;
	}

	public UsuarioJpa getMonitor() {
		return monitor;
	}

	public String getSemestre() {
		return semestre;
	}

	public double getMetaNota() {
		return metaNota;
	}

	public int getMetaFrequencia() {
		return metaFrequencia;
	}

	public AcompanhamentoSituacao getSituacao() {
		return situacao;
	}

	public LocalDate getAbertoEm() {
		return abertoEm;
	}

	public @Nullable LocalDate getEncerradoEm() {
		return encerradoEm;
	}

	public @Nullable String getParecerFinal() {
		return parecerFinal;
	}
}
