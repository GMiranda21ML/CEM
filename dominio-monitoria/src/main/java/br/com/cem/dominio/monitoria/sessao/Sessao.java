package br.com.cem.dominio.monitoria.sessao;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Sessao implements IAgregado<SessaoIdentificador> {
	private final Optional<SessaoIdentificador> identificador;
	public static final int DISCIPLINA_TAMANHO = 150;
	private final String disciplina;
	private final UsuarioIdentificador professor;
	private final UsuarioIdentificador monitor;
	private final CampusIdentificador campus;
	private final Collection<UsuarioIdentificador> alunos;
	private final LocalDateTime inicio;
	private final LocalDateTime fim;
	private final boolean cancelada;

	public Sessao(
			Optional<SessaoIdentificador> identificador,
			String disciplina,
			UsuarioIdentificador professor,
			UsuarioIdentificador monitor,
			CampusIdentificador campus,
			Collection<UsuarioIdentificador> alunos,
			LocalDateTime inicio,
			LocalDateTime fim,
			boolean cancelada) {
		this.identificador = Objects.requireNonNull(identificador);
		this.disciplina = Validate.notBlank(disciplina, "Disciplina obrigatória").strip();
		Validate.isTrue(disciplina.length() <= 150, "Disciplina deve ter até 150 caracteres");
		this.professor = Objects.requireNonNull(professor);
		this.monitor = Objects.requireNonNull(monitor);
		this.campus = Objects.requireNonNull(campus);
		this.alunos = List.copyOf(Objects.requireNonNull(alunos));
		Validate.notEmpty(this.alunos, "Informe ao menos um aluno");
		Validate.isTrue(new HashSet<>(this.alunos).size() == this.alunos.size(), "Aluno repetido");
		Validate.isTrue(
				!this.alunos.contains(monitor), "Monitor não pode ser aluno da própria sessão");
		this.inicio = Objects.requireNonNull(inicio);
		this.fim = Objects.requireNonNull(fim);
		Validate.isTrue(
				fim.isAfter(inicio) && inicio.toLocalDate().equals(fim.toLocalDate()),
				"Horário inválido");
		this.cancelada = cancelada;
	}

	@Override
	public Optional<SessaoIdentificador> getIdentificador() {
		return identificador;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public UsuarioIdentificador getProfessor() {
		return professor;
	}

	public UsuarioIdentificador getMonitor() {
		return monitor;
	}

	public CampusIdentificador getCampus() {
		return campus;
	}

	public Collection<UsuarioIdentificador> getAlunos() {
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

	public boolean realizada(LocalDateTime agora) {
		return !cancelada && !fim.isAfter(Objects.requireNonNull(agora));
	}

	public boolean participa(UsuarioIdentificador pessoa) {
		Objects.requireNonNull(pessoa);
		return monitor.equals(pessoa) || professor.equals(pessoa) || alunos.contains(pessoa);
	}

	public boolean gerenciadaPor(UsuarioIdentificador usuario) {
		Objects.requireNonNull(usuario);
		return monitor.equals(usuario) || professor.equals(usuario);
	}

	public boolean podeInformarAusencia(UsuarioIdentificador aluno, LocalDateTime agora) {
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(agora);
		return alunos.contains(aluno) && !agora.isAfter(fim.plusDays(30));
	}

	public void validarFalta(UsuarioIdentificador pessoa, String papel, LocalDateTime agora) {
		Objects.requireNonNull(pessoa);
		Objects.requireNonNull(papel);
		Objects.requireNonNull(agora);
		Validate.isTrue(
				papel.equals("MONITOR")
						? monitor.equals(pessoa)
						: papel.equals("ALUNO") && alunos.contains(pessoa),
				"Pessoa não vinculada à sessão");
		Validate.validState(realizada(agora), "Somente sessões realizadas podem ter faltas");
	}

	public Sessao cancelar() {
		Validate.validState(!cancelada, "Sessão já cancelada");
		return new Sessao(
				identificador, disciplina, professor, monitor, campus, alunos, inicio, fim, true);
	}
}
