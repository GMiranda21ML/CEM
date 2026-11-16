package br.com.cem.dominio.monitoria.sessao;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Sessao implements IAgregado<SessaoIdentificador> {
	private final Optional<SessaoIdentificador> identificador;
	public static final int DISCIPLINA_TAMANHO = 150;
	public static final int SALA_TAMANHO = 100;
	private final String disciplina;
	private final UsuarioIdentificador professor;
	private final UsuarioIdentificador monitor;
	private final CampusIdentificador campus;
	private final String sala;
	private final Collection<UsuarioIdentificador> alunos;
	private final Collection<RespostaSessao> respostas;
	private final LocalDateTime inicio;
	private final LocalDateTime fim;
	private final Optional<LocalDateTime> inicioAnterior;
	private final boolean cancelada;

	/** Compatibilidade com sessões criadas antes da informação de sala ser exibida. */
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
		this(
				identificador,
				disciplina,
				professor,
				monitor,
				campus,
				"Sala não informada",
				alunos,
				inicio,
				fim,
				cancelada);
	}

	@JsonCreator
	public Sessao(
			@JsonProperty("identificador") Optional<SessaoIdentificador> identificador,
			@JsonProperty("disciplina") String disciplina,
			@JsonProperty("professor") UsuarioIdentificador professor,
			@JsonProperty("monitor") UsuarioIdentificador monitor,
			@JsonProperty("campus") CampusIdentificador campus,
			@JsonProperty("sala") String sala,
			@JsonProperty("alunos") Collection<UsuarioIdentificador> alunos,
			@JsonProperty("inicio") LocalDateTime inicio,
			@JsonProperty("fim") LocalDateTime fim,
			@JsonProperty("cancelada") boolean cancelada) {
		this(
				identificador,
				disciplina,
				professor,
				monitor,
				campus,
				sala,
				alunos,
				alunos.stream().map(RespostaSessao::semResposta).toList(),
				inicio,
				fim,
				Optional.empty(),
				cancelada);
	}

	private Sessao(
			Optional<SessaoIdentificador> identificador,
			String disciplina,
			UsuarioIdentificador professor,
			UsuarioIdentificador monitor,
			CampusIdentificador campus,
			String sala,
			Collection<UsuarioIdentificador> alunos,
			Collection<RespostaSessao> respostas,
			LocalDateTime inicio,
			LocalDateTime fim,
			Optional<LocalDateTime> inicioAnterior,
			boolean cancelada) {
		this.identificador = Objects.requireNonNull(identificador);
		this.disciplina = Validate.notBlank(disciplina, "Disciplina obrigatória").strip();
		Validate.isTrue(disciplina.length() <= DISCIPLINA_TAMANHO, "Disciplina deve ter até 150 caracteres");
		this.professor = Objects.requireNonNull(professor);
		this.monitor = Objects.requireNonNull(monitor);
		this.campus = Objects.requireNonNull(campus);
		this.sala = Validate.notBlank(sala, "Sala obrigatória").strip();
		Validate.isTrue(sala.length() <= SALA_TAMANHO, "Sala deve ter até 100 caracteres");
		this.alunos = List.copyOf(Objects.requireNonNull(alunos));
		Validate.notEmpty(this.alunos, "Informe ao menos um aluno");
		Validate.isTrue(new HashSet<>(this.alunos).size() == this.alunos.size(), "Aluno repetido");
		Validate.isTrue(!this.alunos.contains(monitor), "Monitor não pode ser aluno da própria sessão");
		this.respostas = List.copyOf(Objects.requireNonNull(respostas));
		Validate.isTrue(
				new HashSet<>(this.respostas.stream().map(RespostaSessao::getAluno).toList()).size()
						== this.respostas.size()
						&& this.alunos.size() == this.respostas.size()
						&& this.respostas.stream().allMatch(r -> this.alunos.contains(r.getAluno())),
				"Cada aluno deve possuir exatamente uma resposta na sessão");
		this.inicio = Objects.requireNonNull(inicio);
		this.fim = Objects.requireNonNull(fim);
		this.inicioAnterior = Objects.requireNonNull(inicioAnterior);
		Validate.isTrue(
				fim.isAfter(inicio) && inicio.toLocalDate().equals(fim.toLocalDate()),
				"Horário inválido");
		this.cancelada = cancelada;
	}

	public static Sessao restaurar(
			Optional<SessaoIdentificador> identificador,
			String disciplina,
			UsuarioIdentificador professor,
			UsuarioIdentificador monitor,
			CampusIdentificador campus,
			String sala,
			Collection<UsuarioIdentificador> alunos,
			Collection<RespostaSessao> respostas,
			LocalDateTime inicio,
			LocalDateTime fim,
			Optional<LocalDateTime> inicioAnterior,
			boolean cancelada) {
		return new Sessao(
				identificador,
				disciplina,
				professor,
				monitor,
				campus,
				sala,
				alunos,
				respostas.isEmpty()
						? alunos.stream().map(RespostaSessao::semResposta).toList()
						: respostas,
				inicio,
				fim,
				inicioAnterior,
				cancelada);
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

	public String getSala() {
		return sala;
	}

	public Collection<UsuarioIdentificador> getAlunos() {
		return alunos;
	}

	public Collection<RespostaSessao> getRespostas() {
		return respostas;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public LocalDateTime getFim() {
		return fim;
	}

	public Optional<LocalDateTime> getInicioAnterior() {
		return inicioAnterior;
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
		return copiar(alunos, respostas, inicio, fim, inicioAnterior, true);
	}

	public Sessao adicionarAluno(UsuarioIdentificador aluno) {
		Objects.requireNonNull(aluno);
		Validate.isTrue(!alunos.contains(aluno), "Aluno já incluído na sessão");
		var novosAlunos = new ArrayList<>(alunos);
		novosAlunos.add(aluno);
		var novasRespostas = new ArrayList<>(respostas);
		novasRespostas.add(RespostaSessao.semResposta(aluno));
		return copiar(novosAlunos, novasRespostas, inicio, fim, inicioAnterior, cancelada);
	}

	public Sessao responder(RespostaSessao resposta, LocalDateTime agora) {
		Objects.requireNonNull(resposta);
		Objects.requireNonNull(agora);
		Validate.isTrue(resposta.getSituacao() != SituacaoRespostaSessao.SEM_RESPOSTA, "Selecione uma resposta de participação");
		UsuarioIdentificador aluno = resposta.getAluno();
		Validate.isTrue(alunos.contains(aluno), "Aluno não participa da sessão");
		Validate.validState(agora.isBefore(inicio), "A resposta só pode ser alterada antes do início");
		var novasRespostas =
				respostas.stream()
						.map(
							r ->
								r.getAluno().equals(aluno)
										? resposta
										: r)
						.toList();
		return copiar(alunos, novasRespostas, inicio, fim, inicioAnterior, cancelada);
	}

	public Sessao remarcar(LocalDateTime novoInicio, LocalDateTime novoFim) {
		Objects.requireNonNull(novoInicio);
		Objects.requireNonNull(novoFim);
		Validate.isTrue(
				Duration.between(inicio, fim).equals(Duration.between(novoInicio, novoFim)),
				"A remarcação deve preservar a duração da sessão");
		Validate.isTrue(
				novoFim.isAfter(novoInicio) && novoInicio.toLocalDate().equals(novoFim.toLocalDate()),
				"Horário inválido");
		return copiar(
				alunos,
				alunos.stream().map(RespostaSessao::semResposta).toList(),
				novoInicio,
				novoFim,
				Optional.of(inicio),
				cancelada);
	}

	private Sessao copiar(
			Collection<UsuarioIdentificador> novosAlunos,
			Collection<RespostaSessao> novasRespostas,
			LocalDateTime novoInicio,
			LocalDateTime novoFim,
			Optional<LocalDateTime> novoInicioAnterior,
			boolean novaCancelada) {
		return new Sessao(
				identificador,
				disciplina,
				professor,
				monitor,
				campus,
				sala,
				novosAlunos,
				novasRespostas,
				novoInicio,
				novoFim,
				novoInicioAnterior,
				novaCancelada);
	}
}
