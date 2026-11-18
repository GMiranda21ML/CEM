package br.com.cem.aplicacao.monitoria.acompanhamento;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao;
import br.com.cem.dominio.monitoria.acompanhamento.SituacaoDesempenho;
import br.com.cem.dominio.monitoria.acompanhamento.TendenciaEvolucao;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public record AcompanhamentoProjecao(
		AcompanhamentoIdentificador identificador,
		UsuarioIdentificador aluno,
		String alunoNome,
		DisciplinaIdentificador disciplina,
		String disciplinaNome,
		UsuarioIdentificador monitor,
		String monitorNome,
		UsuarioIdentificador professor,
		String professorNome,
		String semestre,
		double metaNota,
		int metaFrequencia,
		AcompanhamentoSituacao situacao,
		LocalDate abertoEm,
		Optional<LocalDate> encerradoEm,
		Optional<String> parecerFinal,
		boolean possuiFeedback,
		Optional<Double> frequenciaPercentual,
		Optional<Double> notaInicial,
		Optional<Double> notaAtual,
		TendenciaEvolucao tendencia,
		SituacaoDesempenho situacaoDesempenho)
		implements IProjecao<Acompanhamento> {
	public AcompanhamentoProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(alunoNome);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(disciplinaNome);
		Objects.requireNonNull(monitor);
		Objects.requireNonNull(monitorNome);
		Objects.requireNonNull(professor);
		Objects.requireNonNull(professorNome);
		Objects.requireNonNull(semestre);
		Objects.requireNonNull(situacao);
		Objects.requireNonNull(abertoEm);
		Objects.requireNonNull(encerradoEm);
		Objects.requireNonNull(parecerFinal);
		Objects.requireNonNull(frequenciaPercentual);
		Objects.requireNonNull(notaInicial);
		Objects.requireNonNull(notaAtual);
		Objects.requireNonNull(tendencia);
		Objects.requireNonNull(situacaoDesempenho);
	}

	public AcompanhamentoProjecao(
			Long identificador,
			Long aluno,
			String alunoNome,
			Long disciplina,
			String disciplinaNome,
			Long monitor,
			String monitorNome,
			Long professor,
			String professorNome,
			String semestre,
			double metaNota,
			int metaFrequencia,
			AcompanhamentoSituacao situacao,
			LocalDate abertoEm,
			@Nullable LocalDate encerradoEm,
			@Nullable String parecerFinal) {
		this(
				new AcompanhamentoIdentificador(identificador.toString()),
				new UsuarioIdentificador(aluno.toString()),
				alunoNome,
				new DisciplinaIdentificador(disciplina.toString()),
				disciplinaNome,
				new UsuarioIdentificador(monitor.toString()),
				monitorNome,
				new UsuarioIdentificador(professor.toString()),
				professorNome,
				semestre,
				metaNota,
				metaFrequencia,
				situacao,
				abertoEm,
				Optional.ofNullable(encerradoEm),
				Optional.ofNullable(parecerFinal),
				false,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				TendenciaEvolucao.DADOS_INSUFICIENTES,
				SituacaoDesempenho.DADOS_INSUFICIENTES);
	}

	public AcompanhamentoProjecao comResumo(
			boolean possuiFeedback,
			Optional<Double> frequenciaPercentual,
			Optional<Double> notaInicial,
			Optional<Double> notaAtual,
			TendenciaEvolucao tendencia,
			SituacaoDesempenho situacaoDesempenho) {
		return new AcompanhamentoProjecao(
				identificador,
				aluno,
				alunoNome,
				disciplina,
				disciplinaNome,
				monitor,
				monitorNome,
				professor,
				professorNome,
				semestre,
				metaNota,
				metaFrequencia,
				situacao,
				abertoEm,
				encerradoEm,
				parecerFinal,
				possuiFeedback,
				frequenciaPercentual,
				notaInicial,
				notaAtual,
				tendencia,
				situacaoDesempenho);
	}
}
