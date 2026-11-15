package br.com.cem.infraestrutura.monitoria.sessao;

import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.infraestrutura.acesso.campus.CampusJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import dev.sauloaraujo.d5.framework.infraestrutura.IAgregadoJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
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

	@Column(name = "SES_INICIO")
	@NotNull
	private LocalDateTime inicio;

	@Column(name = "SES_FIM")
	@NotNull
	private LocalDateTime fim;

	@Column(name = "SES_CANCELADA")
	private boolean cancelada;

	protected SessaoJpa() {}

	public SessaoJpa(
			@Nullable Long identificador,
			String disciplina,
			UsuarioJpa professor,
			UsuarioJpa monitor,
			CampusJpa campus,
			Collection<UsuarioJpa> alunos,
			LocalDateTime inicio,
			LocalDateTime fim,
			boolean cancelada) {
		this.identificador = identificador;
		this.disciplina = disciplina;
		this.professor = professor;
		this.monitor = monitor;
		this.campus = campus;
		this.alunos = Set.copyOf(alunos);
		this.inicio = inicio;
		this.fim = fim;
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

	public Set<UsuarioJpa> getAlunos() {
		return alunos;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public LocalDateTime getFim() {
		return fim;
	}

	public boolean getCancelada() {
		return cancelada;
	}
}
