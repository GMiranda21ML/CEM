package br.com.cem.dominio.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class Acompanhamento implements IAgregado<AcompanhamentoIdentificador> {
	public static final int SEMESTRE_TAMANHO = 10;
	public static final int PARECER_MINIMO = 20;
	public static final int PARECER_TAMANHO = 1000;
	public static final int META_FREQUENCIA_MINIMA = 50;
	public static final int META_FREQUENCIA_MAXIMA = 100;

	private final Optional<AcompanhamentoIdentificador> identificador;
	private final UsuarioIdentificador aluno;
	private final DisciplinaIdentificador disciplina;
	private final UsuarioIdentificador monitor;
	private final String semestre;
	private final double metaNota;
	private final int metaFrequencia;
	private final AcompanhamentoSituacao situacao;
	private final LocalDate abertoEm;
	private final Optional<LocalDate> encerradoEm;
	private final Optional<String> parecerFinal;

	public Acompanhamento(
			Optional<AcompanhamentoIdentificador> identificador,
			UsuarioIdentificador aluno,
			DisciplinaIdentificador disciplina,
			UsuarioIdentificador monitor,
			String semestre,
			double metaNota,
			int metaFrequencia,
			AcompanhamentoSituacao situacao,
			LocalDate abertoEm,
			Optional<LocalDate> encerradoEm,
			Optional<String> parecerFinal) {
		this.identificador = Objects.requireNonNull(identificador);
		this.aluno = Objects.requireNonNull(aluno);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.monitor = Objects.requireNonNull(monitor);
		Validate.isTrue(
				!aluno.equals(monitor), "O monitor não pode abrir acompanhamento para si mesmo");
		this.semestre = Validate.notBlank(semestre, "Semestre obrigatório").strip();
		Validate.isTrue(
				this.semestre.length() <= SEMESTRE_TAMANHO,
				"Semestre deve ter até %d caracteres",
				SEMESTRE_TAMANHO);
		Validate.isTrue(
				Double.isFinite(metaNota) && metaNota >= 0.0 && metaNota <= 10.0,
				"Meta de nota deve estar entre 0 e 10");
		this.metaNota = metaNota;
		Validate.inclusiveBetween(
				META_FREQUENCIA_MINIMA,
				META_FREQUENCIA_MAXIMA,
				metaFrequencia,
				"Meta de frequência deve estar entre %d%% e %d%%",
				META_FREQUENCIA_MINIMA,
				META_FREQUENCIA_MAXIMA);
		this.metaFrequencia = metaFrequencia;
		this.situacao = Objects.requireNonNull(situacao);
		this.abertoEm = Objects.requireNonNull(abertoEm);
		this.encerradoEm = Objects.requireNonNull(encerradoEm);
		this.parecerFinal =
				Objects.requireNonNull(parecerFinal)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		if (situacao == AcompanhamentoSituacao.ATIVO) {
			Validate.isTrue(
					this.encerradoEm.isEmpty() && this.parecerFinal.isEmpty(),
					"Acompanhamento ativo não possui parecer ou data de encerramento");
		} else {
			Validate.isTrue(
					this.encerradoEm.isPresent() && this.parecerFinal.isPresent(),
					"Acompanhamento encerrado exige data e parecer final");
			String parecer = this.parecerFinal.get();
			Validate.inclusiveBetween(
					PARECER_MINIMO,
					PARECER_TAMANHO,
					parecer.length(),
					"O parecer final deve ter entre %d e %d caracteres",
					PARECER_MINIMO,
					PARECER_TAMANHO);
		}
	}

	@Override
	public Optional<AcompanhamentoIdentificador> getIdentificador() {
		return identificador;
	}

	public UsuarioIdentificador getAluno() {
		return aluno;
	}

	public DisciplinaIdentificador getDisciplina() {
		return disciplina;
	}

	public UsuarioIdentificador getMonitor() {
		return monitor;
	}

	public String getSemestre() {
		return semestre;
	}

	public double getMetaNota() {
		return metaNota;
	}

	public int getMetaFrequencia() {
		return metaFrequencia;
	}

	public AcompanhamentoSituacao getSituacao() {
		return situacao;
	}

	public LocalDate getAbertoEm() {
		return abertoEm;
	}

	public Optional<LocalDate> getEncerradoEm() {
		return encerradoEm;
	}

	public Optional<String> getParecerFinal() {
		return parecerFinal;
	}

	public boolean ativo() {
		return situacao == AcompanhamentoSituacao.ATIVO;
	}

	public boolean podeGerenciar(
			UsuarioIdentificador usuario, UsuarioIdentificador professorDisciplina) {
		Objects.requireNonNull(usuario);
		Objects.requireNonNull(professorDisciplina);
		return monitor.equals(usuario) || professorDisciplina.equals(usuario);
	}

	public boolean podeConsultar(
			UsuarioIdentificador usuario,
			UsuarioIdentificador professorDisciplina,
			boolean coordenador) {
		Objects.requireNonNull(usuario);
		Objects.requireNonNull(professorDisciplina);
		return coordenador
				|| aluno.equals(usuario)
				|| monitor.equals(usuario)
				|| professorDisciplina.equals(usuario);
	}

	public Acompanhamento alterarMetas(double novaMetaNota, int novaMetaFrequencia) {
		Validate.validState(ativo(), "Acompanhamento encerrado não permite alteração de metas");
		return new Acompanhamento(
				identificador,
				aluno,
				disciplina,
				monitor,
				semestre,
				novaMetaNota,
				novaMetaFrequencia,
				situacao,
				abertoEm,
				encerradoEm,
				parecerFinal);
	}

	public Acompanhamento encerrar(String parecer, LocalDate dataEncerramento) {
		Validate.validState(ativo(), "Acompanhamento já está encerrado");
		Objects.requireNonNull(parecer);
		Objects.requireNonNull(dataEncerramento);
		String texto = parecer.strip();
		Validate.inclusiveBetween(
				PARECER_MINIMO,
				PARECER_TAMANHO,
				texto.length(),
				"O parecer final precisa ter pelo menos %d caracteres",
				PARECER_MINIMO);
		return new Acompanhamento(
				identificador,
				aluno,
				disciplina,
				monitor,
				semestre,
				metaNota,
				metaFrequencia,
				AcompanhamentoSituacao.ENCERRADO,
				abertoEm,
				Optional.of(dataEncerramento),
				Optional.of(texto));
	}
}
