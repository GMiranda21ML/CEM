package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.infraestrutura.acesso.campus.CampusJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(
		name = "SES_SESSAO",
		indexes = {
			@Index(
					name = "IX_SES_MONITOR_HORARIO",
					columnList = "SES_MONITOR_ID,SES_INICIO,SES_FIM"),
		})
public class SessaoJpa implements IAgregadoJpa<Sessao> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SES_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "SES_DISCIPLINA")
	@NotNull
	@Size(max = Sessao.DISCIPLINA_TAMANHO)
	private String disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_PROFESSOR_ID")
	@NotNull
	private UsuarioJpa professor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_MONITOR_ID")
	@NotNull
	private UsuarioJpa monitor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAM_ID")
	@NotNull
	private CampusJpa campus;

	@Column(name = "SES_SALA")
	@Size(max = Sessao.SALA_TAMANHO)
	private @Nullable String sala;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "SAP_SESSAO_ALUNO",
			joinColumns = @JoinColumn(name = "SES_ID"),
			inverseJoinColumns = @JoinColumn(name = "USR_ID"),
			uniqueConstraints =
					@UniqueConstraint(
							name = "UK_SAP_SESSAO_ALUNO",
							columnNames = {"SES_ID", "USR_ID"}))
	private Set<UsuarioJpa> alunos;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
			name = "SSR_SESSAO_RESPOSTA",
			joinColumns = @JoinColumn(name = "SES_ID"),
			uniqueConstraints =
					@UniqueConstraint(
							name = "UK_SSR_SESSAO_ALUNO",
							columnNames = {"SES_ID", "USR_ID"}))
	@OrderColumn(name = "SSR_INDICE")
	private List<RespostaSessaoJpa> respostas;

	@Column(name = "SES_INICIO")
	@NotNull
	private LocalDateTime inicio;

	@Column(name = "SES_FIM")
	@NotNull
	private LocalDateTime fim;

	@Column(name = "SES_INICIO_ANTERIOR")
	private @Nullable LocalDateTime inicioAnterior;

	@Column(name = "SES_CANCELADA")
	private boolean cancelada;

	protected SessaoJpa() {}

	public SessaoJpa(
			@Nullable Long identificador,
			String disciplina,
			UsuarioJpa professor,
			UsuarioJpa monitor,
			CampusJpa campus,
			String sala,
			Collection<UsuarioJpa> alunos,
			List<RespostaSessaoJpa> respostas,
			LocalDateTime inicio,
			LocalDateTime fim,
			@Nullable LocalDateTime inicioAnterior,
			boolean cancelada) {
		this.identificador = identificador;
		this.disciplina = disciplina;
		this.professor = professor;
		this.monitor = monitor;
		this.campus = campus;
		this.sala = sala;
		this.alunos = Set.copyOf(alunos);
		this.respostas = List.copyOf(respostas);
		this.inicio = inicio;
		this.fim = fim;
		this.inicioAnterior = inicioAnterior;
		this.cancelada = cancelada;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public UsuarioJpa getProfessor() {
		return professor;
	}

	public UsuarioJpa getMonitor() {
		return monitor;
	}

	public CampusJpa getCampus() {
		return campus;
	}

	public @Nullable String getSala() {
		return sala;
	}

	public Set<UsuarioJpa> getAlunos() {
		return alunos;
	}

	public List<RespostaSessaoJpa> getRespostas() {
		return respostas;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public LocalDateTime getFim() {
		return fim;
	}

	public @Nullable LocalDateTime getInicioAnterior() {
		return inicioAnterior;
	}

	public boolean getCancelada() {
		return cancelada;
	}
}
