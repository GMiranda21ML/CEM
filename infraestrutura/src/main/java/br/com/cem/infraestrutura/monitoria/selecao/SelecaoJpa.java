package br.com.cem.infraestrutura.monitoria.selecao;

import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
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
		name = "SEL_SELECAO",
		indexes = {
			@Index(
					name = "IX_SEL_DISCIPLINA_SITUACAO",
					columnList = "DSC_ID,SEL_SITUACAO")
		})
public class SelecaoJpa implements IAgregadoJpa<Selecao> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SEL_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DSC_ID")
	@NotNull
	private DisciplinaJpa disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID")
	@NotNull
	private UsuarioJpa professor;

	@Column(name = "SEL_SEMESTRE")
	@NotNull
	@Size(max = Selecao.SEMESTRE_TAMANHO)
	private String semestre;

	@Column(name = "SEL_PRAZO_INSCRICAO")
	@NotNull
	private LocalDate prazoInscricao;

	@Column(name = "SEL_VAGAS")
	private int vagas;

	@Column(name = "SEL_CR_MINIMO")
	private double crMinimo;

	@Column(name = "SEL_PESO_CR")
	private int pesoCr;

	@Column(name = "SEL_PESO_ENTREVISTA")
	private int pesoEntrevista;

	@Column(name = "SEL_PATAMAR_MINIMO")
	private double patamarMinimo;

	@Column(name = "SEL_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private SelecaoSituacao situacao;

	@Column(name = "SEL_MOTIVO_ENCERRAMENTO")
	@Size(max = Selecao.MOTIVO_ENCERRAMENTO_TAMANHO)
	private @Nullable String motivoEncerramento;

	@Column(name = "SEL_ABERTA_EM", updatable = false)
	@NotNull
	private LocalDate abertaEm;

	@Column(name = "SEL_ENCERRADA_EM")
	private @Nullable LocalDate encerradaEm;

	protected SelecaoJpa() {}

	public SelecaoJpa(
			@Nullable Long identificador,
			DisciplinaJpa disciplina,
			UsuarioJpa professor,
			String semestre,
			LocalDate prazoInscricao,
			int vagas,
			double crMinimo,
			int pesoCr,
			int pesoEntrevista,
			double patamarMinimo,
			SelecaoSituacao situacao,
			@Nullable String motivoEncerramento,
			LocalDate abertaEm,
			@Nullable LocalDate encerradaEm) {
		this.identificador = identificador;
		this.disciplina = disciplina;
		this.professor = professor;
		this.semestre = semestre;
		this.prazoInscricao = prazoInscricao;
		this.vagas = vagas;
		this.crMinimo = crMinimo;
		this.pesoCr = pesoCr;
		this.pesoEntrevista = pesoEntrevista;
		this.patamarMinimo = patamarMinimo;
		this.situacao = situacao;
		this.motivoEncerramento = motivoEncerramento;
		this.abertaEm = abertaEm;
		this.encerradaEm = encerradaEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public DisciplinaJpa getDisciplina() {
		return disciplina;
	}

	public UsuarioJpa getProfessor() {
		return professor;
	}

	public String getSemestre() {
		return semestre;
	}

	public LocalDate getPrazoInscricao() {
		return prazoInscricao;
	}

	public int getVagas() {
		return vagas;
	}

	public double getCrMinimo() {
		return crMinimo;
	}

	public int getPesoCr() {
		return pesoCr;
	}

	public int getPesoEntrevista() {
		return pesoEntrevista;
	}

	public double getPatamarMinimo() {
		return patamarMinimo;
	}

	public SelecaoSituacao getSituacao() {
		return situacao;
	}

	public @Nullable String getMotivoEncerramento() {
		return motivoEncerramento;
	}

	public LocalDate getAbertaEm() {
		return abertaEm;
	}

	public @Nullable LocalDate getEncerradaEm() {
		return encerradaEm;
	}
}
