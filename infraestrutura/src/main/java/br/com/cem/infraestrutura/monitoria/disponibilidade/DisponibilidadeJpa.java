package br.com.cem.infraestrutura.monitoria.disponibilidade;

import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.infraestrutura.acesso.campus.CampusJpa;
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

import java.time.LocalTime;

@Entity
@Table(name = "DSP_DISPONIBILIDADE")
public class DisponibilidadeJpa implements IAgregadoJpa<Disponibilidade> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DSP_ID", updatable = false)
	private @Nullable Long identificador;

	@Column(name = "DSP_DISCIPLINA")
	@NotNull
	@Size(max = Disponibilidade.DISCIPLINA_TAMANHO)
	private String disciplina;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DSP_MONITOR_ID")
	@NotNull
	private UsuarioJpa monitor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAM_ID")
	@NotNull
	private CampusJpa campus;

	@Column(name = "DSP_DIA_SEMANA")
	private int diaSemana;

	@Column(name = "DSP_INICIO")
	@NotNull
	private LocalTime inicio;

	@Column(name = "DSP_FIM")
	@NotNull
	private LocalTime fim;

	protected DisponibilidadeJpa() {}

	public DisponibilidadeJpa(
			@Nullable Long identificador,
			String disciplina,
			UsuarioJpa monitor,
			CampusJpa campus,
			int diaSemana,
			LocalTime inicio,
			LocalTime fim) {
		this.identificador = identificador;
		this.disciplina = disciplina;
		this.monitor = monitor;
		this.campus = campus;
		this.diaSemana = diaSemana;
		this.inicio = inicio;
		this.fim = fim;
	}

	public @Nullable Long getIdentificador() {
		return identificador;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public UsuarioJpa getMonitor() {
		return monitor;
	}

	public CampusJpa getCampus() {
		return campus;
	}

	public int getDiaSemana() {
		return diaSemana;
	}

	public LocalTime getInicio() {
		return inicio;
	}

	public LocalTime getFim() {
		return fim;
	}
}
