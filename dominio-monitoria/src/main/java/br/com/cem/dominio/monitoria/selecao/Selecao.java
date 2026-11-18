package br.com.cem.dominio.monitoria.selecao;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class Selecao implements IAgregado<SelecaoIdentificador> {
	public static final int SEMESTRE_TAMANHO = 10;
	public static final int MOTIVO_ENCERRAMENTO_TAMANHO = 100;

	private final Optional<SelecaoIdentificador> identificador;
	private final DisciplinaIdentificador disciplina;
	private final UsuarioIdentificador professor;
	private final String semestre;
	private final LocalDate prazoInscricao;
	private final int vagas;
	private final double crMinimo;
	private final int pesoCr;
	private final int pesoEntrevista;
	private final double patamarMinimo;
	private final SelecaoSituacao situacao;
	private final Optional<String> motivoEncerramento;
	private final LocalDate abertaEm;
	private final Optional<LocalDate> encerradaEm;

	public Selecao(
			Optional<SelecaoIdentificador> identificador,
			DisciplinaIdentificador disciplina,
			UsuarioIdentificador professor,
			String semestre,
			LocalDate prazoInscricao,
			int vagas,
			double crMinimo,
			int pesoCr,
			int pesoEntrevista,
			double patamarMinimo,
			SelecaoSituacao situacao,
			Optional<String> motivoEncerramento,
			LocalDate abertaEm,
			Optional<LocalDate> encerradaEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.professor = Objects.requireNonNull(professor);
		this.semestre = Validate.notBlank(semestre, "Semestre obrigatório").strip();
		Validate.isTrue(
				this.semestre.length() <= SEMESTRE_TAMANHO,
				"Semestre deve ter até %d caracteres",
				SEMESTRE_TAMANHO);
		this.prazoInscricao = Objects.requireNonNull(prazoInscricao);
		Validate.isTrue(vagas >= 1, "Informe ao menos uma vaga");
		this.vagas = vagas;
		Validate.isTrue(
				Double.isFinite(crMinimo) && crMinimo >= 0.0 && crMinimo <= 10.0,
				"CR mínimo deve estar entre 0 e 10");
		this.crMinimo = crMinimo;
		Validate.isTrue(
				pesoCr >= 0 && pesoEntrevista >= 0 && pesoCr + pesoEntrevista == 100,
				"Os pesos de CR e entrevista devem ser não negativos e somar 100%");
		this.pesoCr = pesoCr;
		this.pesoEntrevista = pesoEntrevista;
		Validate.isTrue(
				Double.isFinite(patamarMinimo) && patamarMinimo >= 0.0 && patamarMinimo <= 10.0,
				"Patamar mínimo deve estar entre 0 e 10");
		this.patamarMinimo = patamarMinimo;
		this.situacao = Objects.requireNonNull(situacao);
		this.motivoEncerramento =
				Objects.requireNonNull(motivoEncerramento)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		this.motivoEncerramento.ifPresent(
				texto ->
						Validate.isTrue(
								texto.length() <= MOTIVO_ENCERRAMENTO_TAMANHO,
								"Motivo de encerramento deve ter até %d caracteres",
								MOTIVO_ENCERRAMENTO_TAMANHO));
		this.abertaEm = Objects.requireNonNull(abertaEm);
		this.encerradaEm = Objects.requireNonNull(encerradaEm);
		if (situacao == SelecaoSituacao.ABERTA) {
			Validate.isTrue(
					this.encerradaEm.isEmpty() && this.motivoEncerramento.isEmpty(),
					"Seleção aberta não possui dados de encerramento");
		} else {
			Validate.isTrue(
					this.encerradaEm.isPresent() && this.motivoEncerramento.isPresent(),
					"Seleção encerrada exige data e motivo de encerramento");
		}
	}

	public static String semestreAtual(LocalDate data) {
		Objects.requireNonNull(data);
		return data.getYear() + "." + (data.getMonthValue() <= 6 ? 1 : 2);
	}

	@Override
	public Optional<SelecaoIdentificador> getIdentificador() {
		return identificador;
	}

	public DisciplinaIdentificador getDisciplina() {
		return disciplina;
	}

	public UsuarioIdentificador getProfessor() {
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

	public Optional<String> getMotivoEncerramento() {
		return motivoEncerramento;
	}

	public LocalDate getAbertaEm() {
		return abertaEm;
	}

	public Optional<LocalDate> getEncerradaEm() {
		return encerradaEm;
	}

	public boolean encerrada() {
		return situacao == SelecaoSituacao.ENCERRADA;
	}

	public boolean inscricoesAbertas(LocalDate hoje) {
		Objects.requireNonNull(hoje);
		return situacao == SelecaoSituacao.ABERTA && !hoje.isAfter(prazoInscricao);
	}

	public boolean emAvaliacao(LocalDate hoje) {
		Objects.requireNonNull(hoje);
		return situacao == SelecaoSituacao.ABERTA && hoje.isAfter(prazoInscricao);
	}

	public double calcularResultadoFinal(double cr, double notaEntrevista) {
		Validate.isTrue(Double.isFinite(cr) && cr >= 0.0 && cr <= 10.0, "CR deve estar entre 0 e 10");
		Validate.isTrue(
				Double.isFinite(notaEntrevista) && notaEntrevista >= 0.0 && notaEntrevista <= 10.0,
				"A nota da entrevista deve estar entre 0 e 10");
		return (pesoCr * cr + pesoEntrevista * notaEntrevista) / 100.0;
	}

	public Selecao atualizarCriterios(
			LocalDate novoPrazoInscricao,
			int novasVagas,
			double novoCrMinimo,
			int novoPesoCr,
			int novoPesoEntrevista,
			double novoPatamarMinimo) {
		Validate.validState(situacao == SelecaoSituacao.ABERTA, "Seleção encerrada não pode ser editada");
		return new Selecao(
				identificador,
				disciplina,
				professor,
				semestre,
				novoPrazoInscricao,
				novasVagas,
				novoCrMinimo,
				novoPesoCr,
				novoPesoEntrevista,
				novoPatamarMinimo,
				situacao,
				motivoEncerramento,
				abertaEm,
				encerradaEm);
	}

	public Selecao encerrar(String motivo, LocalDate dataEncerramento) {
		Validate.validState(situacao == SelecaoSituacao.ABERTA, "Seleção já está encerrada");
		Validate.notBlank(motivo, "Motivo de encerramento obrigatório");
		Objects.requireNonNull(dataEncerramento);
		return new Selecao(
				identificador,
				disciplina,
				professor,
				semestre,
				prazoInscricao,
				vagas,
				crMinimo,
				pesoCr,
				pesoEntrevista,
				patamarMinimo,
				SelecaoSituacao.ENCERRADA,
				Optional.of(motivo),
				abertaEm,
				Optional.of(dataEncerramento));
	}
}
