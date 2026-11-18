package br.com.cem.dominio.monitoria.candidatura;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class Candidatura implements IAgregado<CandidaturaIdentificador> {
	public static final int COMENTARIO_TAMANHO = 1000;
	public static final int MOTIVO_TAMANHO = 250;

	private final Optional<CandidaturaIdentificador> identificador;
	private final SelecaoIdentificador selecao;
	private final DisciplinaIdentificador disciplina;
	private final UsuarioIdentificador aluno;
	private final double cr;
	private final CandidaturaSituacao situacao;
	private final Optional<Double> notaEntrevista;
	private final Optional<String> comentarioEntrevista;
	private final Optional<Double> resultadoFinal;
	private final Optional<String> motivoDecisao;
	private final LocalDateTime criadaEm;
	private final Optional<LocalDate> decididaEm;

	public Candidatura(
			Optional<CandidaturaIdentificador> identificador,
			SelecaoIdentificador selecao,
			DisciplinaIdentificador disciplina,
			UsuarioIdentificador aluno,
			double cr,
			CandidaturaSituacao situacao,
			Optional<Double> notaEntrevista,
			Optional<String> comentarioEntrevista,
			Optional<Double> resultadoFinal,
			Optional<String> motivoDecisao,
			LocalDateTime criadaEm,
			Optional<LocalDate> decididaEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.selecao = Objects.requireNonNull(selecao);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.aluno = Objects.requireNonNull(aluno);
		Validate.isTrue(Double.isFinite(cr) && cr >= 0.0 && cr <= 10.0, "CR deve estar entre 0 e 10");
		this.cr = cr;
		this.situacao = Objects.requireNonNull(situacao);
		this.notaEntrevista = Objects.requireNonNull(notaEntrevista);
		this.notaEntrevista.ifPresent(
				nota ->
						Validate.isTrue(
								Double.isFinite(nota) && nota >= 0.0 && nota <= 10.0,
								"A nota da entrevista deve estar entre 0 e 10"));
		this.comentarioEntrevista =
				Objects.requireNonNull(comentarioEntrevista)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		this.comentarioEntrevista.ifPresent(
				texto ->
						Validate.isTrue(
								texto.length() <= COMENTARIO_TAMANHO,
								"Comentário da entrevista deve ter até %d caracteres",
								COMENTARIO_TAMANHO));
		this.resultadoFinal = Objects.requireNonNull(resultadoFinal);
		this.resultadoFinal.ifPresent(
				resultado ->
						Validate.isTrue(
								Double.isFinite(resultado) && resultado >= 0.0 && resultado <= 10.0,
								"Resultado final inválido"));
		this.motivoDecisao =
				Objects.requireNonNull(motivoDecisao)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		this.motivoDecisao.ifPresent(
				texto ->
						Validate.isTrue(
								texto.length() <= MOTIVO_TAMANHO,
								"Motivo da decisão deve ter até %d caracteres",
								MOTIVO_TAMANHO));
		this.criadaEm = Objects.requireNonNull(criadaEm);
		this.decididaEm = Objects.requireNonNull(decididaEm);
	}

	public static Comparator<Candidatura> comparadorClassificacao() {
		return Comparator.comparingDouble(
						(Candidatura c) -> c.getResultadoFinal().orElse(-1.0))
				.reversed()
				.thenComparing(Comparator.comparingDouble(Candidatura::getCr).reversed())
				.thenComparing(Candidatura::getCriadaEm);
	}

	@Override
	public Optional<CandidaturaIdentificador> getIdentificador() {
		return identificador;
	}

	public SelecaoIdentificador getSelecao() {
		return selecao;
	}

	public DisciplinaIdentificador getDisciplina() {
		return disciplina;
	}

	public UsuarioIdentificador getAluno() {
		return aluno;
	}

	public double getCr() {
		return cr;
	}

	public CandidaturaSituacao getSituacao() {
		return situacao;
	}

	public Optional<Double> getNotaEntrevista() {
		return notaEntrevista;
	}

	public Optional<String> getComentarioEntrevista() {
		return comentarioEntrevista;
	}

	public Optional<Double> getResultadoFinal() {
		return resultadoFinal;
	}

	public Optional<String> getMotivoDecisao() {
		return motivoDecisao;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}

	public Optional<LocalDate> getDecididaEm() {
		return decididaEm;
	}

	public boolean ativa() {
		return situacao == CandidaturaSituacao.PENDENTE || situacao == CandidaturaSituacao.APROVADA;
	}

	public Candidatura cancelar(LocalDate data) {
		Objects.requireNonNull(data);
		Validate.validState(
				situacao == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser canceladas");
		return new Candidatura(
				identificador,
				selecao,
				disciplina,
				aluno,
				cr,
				CandidaturaSituacao.CANCELADA,
				notaEntrevista,
				comentarioEntrevista,
				resultadoFinal,
				Optional.of("Cancelada pelo aluno"),
				criadaEm,
				Optional.of(data));
	}

	public Candidatura registrarEntrevista(
			double nota, Optional<String> comentario, double resultadoCalculado) {
		Objects.requireNonNull(comentario);
		Validate.validState(
				situacao == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem receber nota de entrevista");
		return new Candidatura(
				identificador,
				selecao,
				disciplina,
				aluno,
				cr,
				situacao,
				Optional.of(nota),
				comentario,
				Optional.of(resultadoCalculado),
				motivoDecisao,
				criadaEm,
				decididaEm);
	}

	public Candidatura aprovar(double resultadoCalculado, LocalDate data, String motivo) {
		Objects.requireNonNull(data);
		Validate.notBlank(motivo, "Motivo obrigatório");
		Validate.validState(
				situacao == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser aprovadas");
		return new Candidatura(
				identificador,
				selecao,
				disciplina,
				aluno,
				cr,
				CandidaturaSituacao.APROVADA,
				notaEntrevista,
				comentarioEntrevista,
				Optional.of(resultadoCalculado),
				Optional.of(motivo),
				criadaEm,
				Optional.of(data));
	}

	public Candidatura reprovar(double resultadoCalculado, LocalDate data, String motivo) {
		Objects.requireNonNull(data);
		Validate.notBlank(motivo, "Motivo obrigatório");
		Validate.validState(
				situacao == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser reprovadas");
		return new Candidatura(
				identificador,
				selecao,
				disciplina,
				aluno,
				cr,
				CandidaturaSituacao.REPROVADA,
				notaEntrevista,
				comentarioEntrevista,
				Optional.of(resultadoCalculado),
				Optional.of(motivo),
				criadaEm,
				Optional.of(data));
	}

	public Candidatura reprovarAutomaticamente(
			Optional<Double> resultadoCalculado, String motivo, LocalDate data) {
		Objects.requireNonNull(resultadoCalculado);
		Objects.requireNonNull(data);
		Validate.notBlank(motivo, "Motivo obrigatório");
		Validate.validState(
				situacao == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser reprovadas");
		return new Candidatura(
				identificador,
				selecao,
				disciplina,
				aluno,
				cr,
				CandidaturaSituacao.REPROVADA,
				notaEntrevista,
				comentarioEntrevista,
				resultadoCalculado,
				Optional.of(motivo),
				criadaEm,
				Optional.of(data));
	}
}
