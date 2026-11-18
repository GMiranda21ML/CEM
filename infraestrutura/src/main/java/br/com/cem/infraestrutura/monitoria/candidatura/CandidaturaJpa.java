package br.com.cem.infraestrutura.monitoria.candidatura;

import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.SelecaoJpa;

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
import java.time.LocalDateTime;

@Entity
@Table(
		name = "CND_CANDIDATURA",
		indexes = {
			@Index(name = "IX_CND_SELECAO", columnList = "SEL_ID"),
			@Index(
					name = "IX_CND_ALUNO_DISCIPLINA_SITUACAO",
					columnList = "USR_ID,DSC_ID,CND_SITUACAO")
		})
public class CandidaturaJpa implements IAgregadoJpa<Candidatura> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CND_ID", updatable = false)
	private @Nullable Long identificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEL_ID")
	@NotNull
	private SelecaoJpa selecao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DSC_ID")
	@NotNull
	private DisciplinaJpa disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID")
	@NotNull
	private UsuarioJpa aluno;

	@Column(name = "CND_CR")
	private double cr;

	@Column(name = "CND_SITUACAO")
	@Enumerated(EnumType.STRING)
	@NotNull
	private CandidaturaSituacao situacao;

	@Column(name = "CND_NOTA_ENTREVISTA")
	private @Nullable Double notaEntrevista;

	@Column(name = "CND_COMENTARIO_ENTREVISTA")
	@Size(max = Candidatura.COMENTARIO_TAMANHO)
	private @Nullable String comentarioEntrevista;

	@Column(name = "CND_RESULTADO_FINAL")
	private @Nullable Double resultadoFinal;

	@Column(name = "CND_MOTIVO_DECISAO")
	@Size(max = Candidatura.MOTIVO_TAMANHO)
	private @Nullable String motivoDecisao;

	@Column(name = "CND_CRIADA_EM", updatable = false)
	@NotNull
	private LocalDateTime criadaEm;

	@Column(name = "CND_DECIDIDA_EM")
	private @Nullable LocalDate decididaEm;

	protected CandidaturaJpa() {}

	public CandidaturaJpa(
			@Nullable Long identificador,
			SelecaoJpa selecao,
			DisciplinaJpa disciplina,
			UsuarioJpa aluno,
			double cr,
			CandidaturaSituacao situacao,
			@Nullable Double notaEntrevista,
			@Nullable String comentarioEntrevista,
			@Nullable Double resultadoFinal,
			@Nullable String motivoDecisao,
			LocalDateTime criadaEm,
			@Nullable LocalDate decididaEm) {
		this.identificador = identificador;
		this.selecao = selecao;
		this.disciplina = disciplina;
		this.aluno = aluno;
		this.cr = cr;
		this.situacao = situacao;
		this.notaEntrevista = notaEntrevista;
		this.comentarioEntrevista = comentarioEntrevista;
		this.resultadoFinal = resultadoFinal;
		this.motivoDecisao = motivoDecisao;
		this.criadaEm = criadaEm;
		this.decididaEm = decididaEm;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public SelecaoJpa getSelecao() {
		return selecao;
	}

	public DisciplinaJpa getDisciplina() {
		return disciplina;
	}

	public UsuarioJpa getAluno() {
		return aluno;
	}

	public double getCr() {
		return cr;
	}

	public CandidaturaSituacao getSituacao() {
		return situacao;
	}

	public @Nullable Double getNotaEntrevista() {
		return notaEntrevista;
	}

	public @Nullable String getComentarioEntrevista() {
		return comentarioEntrevista;
	}

	public @Nullable Double getResultadoFinal() {
		return resultadoFinal;
	}

	public @Nullable String getMotivoDecisao() {
		return motivoDecisao;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}

	public @Nullable LocalDate getDecididaEm() {
		return decididaEm;
	}
}
