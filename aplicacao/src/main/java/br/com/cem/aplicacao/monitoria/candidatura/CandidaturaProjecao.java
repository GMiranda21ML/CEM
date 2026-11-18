package br.com.cem.aplicacao.monitoria.candidatura;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record CandidaturaProjecao(
		CandidaturaIdentificador identificador,
		SelecaoIdentificador selecao,
		DisciplinaIdentificador disciplina,
		String disciplinaNome,
		String semestre,
		UsuarioIdentificador aluno,
		String alunoNome,
		double cr,
		CandidaturaSituacao situacao,
		Optional<Double> notaEntrevista,
		Optional<String> comentarioEntrevista,
		Optional<Double> resultadoFinal,
		Optional<String> motivoDecisao,
		LocalDateTime criadaEm,
		Optional<LocalDate> decididaEm)
		implements IProjecao<Candidatura> {
	public CandidaturaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(selecao);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(disciplinaNome);
		Objects.requireNonNull(semestre);
		Objects.requireNonNull(aluno);
		Objects.requireNonNull(alunoNome);
		Objects.requireNonNull(situacao);
		Objects.requireNonNull(notaEntrevista);
		Objects.requireNonNull(comentarioEntrevista);
		Objects.requireNonNull(resultadoFinal);
		Objects.requireNonNull(motivoDecisao);
		Objects.requireNonNull(criadaEm);
		Objects.requireNonNull(decididaEm);
	}

	public CandidaturaProjecao(
			Long identificador,
			Long selecao,
			Long disciplina,
			String disciplinaNome,
			String semestre,
			Long aluno,
			String alunoNome,
			double cr,
			CandidaturaSituacao situacao,
			@Nullable Double notaEntrevista,
			@Nullable String comentarioEntrevista,
			@Nullable Double resultadoFinal,
			@Nullable String motivoDecisao,
			LocalDateTime criadaEm,
			@Nullable LocalDate decididaEm) {
		this(
				new CandidaturaIdentificador(identificador.toString()),
				new SelecaoIdentificador(selecao.toString()),
				new DisciplinaIdentificador(disciplina.toString()),
				disciplinaNome,
				semestre,
				new UsuarioIdentificador(aluno.toString()),
				alunoNome,
				cr,
				situacao,
				Optional.ofNullable(notaEntrevista),
				Optional.ofNullable(comentarioEntrevista),
				Optional.ofNullable(resultadoFinal),
				Optional.ofNullable(motivoDecisao),
				criadaEm,
				Optional.ofNullable(decididaEm));
	}
}
