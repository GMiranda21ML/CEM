package br.com.cem.dominio.monitoria.disponibilidade;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public final class Disponibilidade implements IAgregado<DisponibilidadeIdentificador> {
	private final Optional<DisponibilidadeIdentificador> identificador;
	public static final int DISCIPLINA_TAMANHO = 150;
	private final String disciplina;
	private final UsuarioIdentificador monitor;
	private final CampusIdentificador campus;
	private final int diaSemana;
	private final LocalTime inicio;
	private final LocalTime fim;

	public Disponibilidade(
			Optional<DisponibilidadeIdentificador> identificador,
			String disciplina,
			UsuarioIdentificador monitor,
			CampusIdentificador campus,
			int diaSemana,
			LocalTime inicio,
			LocalTime fim) {
		this.identificador = Objects.requireNonNull(identificador);
		this.disciplina = Validate.notBlank(disciplina, "Disciplina obrigatória").strip();
		Validate.isTrue(disciplina.length() <= 150, "Disciplina deve ter até 150 caracteres");
		this.monitor = Objects.requireNonNull(monitor);
		this.campus = Objects.requireNonNull(campus);
		Validate.inclusiveBetween(
				1, 7, diaSemana, "Dia deve estar entre segunda (1) e domingo (7)");
		this.diaSemana = diaSemana;
		this.inicio = Objects.requireNonNull(inicio);
		this.fim = Objects.requireNonNull(fim);
		Validate.isTrue(fim.isAfter(inicio), "Horário final deve ser posterior ao inicial");
	}

	@Override
	public Optional<DisponibilidadeIdentificador> getIdentificador() {
		return identificador;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public UsuarioIdentificador getMonitor() {
		return monitor;
	}

	public CampusIdentificador getCampus() {
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
