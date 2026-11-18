package br.com.cem.aplicacao.monitoria.selecao;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SelecaoProjecao(
		SelecaoIdentificador identificador,
		DisciplinaIdentificador disciplina,
		String disciplinaNome,
		String disciplinaCodigo,
		String cursoNome,
		UsuarioIdentificador professor,
		String professorNome,
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
		Optional<LocalDate> encerradaEm,
		long totalCandidaturas,
		long pendentes,
		long aprovadas,
		List<String> monitoresSelecionados)
		implements IProjecao<Selecao> {
	public SelecaoProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(disciplinaNome);
		Objects.requireNonNull(disciplinaCodigo);
		Objects.requireNonNull(cursoNome);
		Objects.requireNonNull(professor);
		Objects.requireNonNull(professorNome);
		Objects.requireNonNull(semestre);
		Objects.requireNonNull(prazoInscricao);
		Objects.requireNonNull(situacao);
		Objects.requireNonNull(motivoEncerramento);
		Objects.requireNonNull(abertaEm);
		Objects.requireNonNull(encerradaEm);
		monitoresSelecionados = List.copyOf(Objects.requireNonNull(monitoresSelecionados));
	}

	public SelecaoProjecao(
			Long identificador,
			Long disciplina,
			String disciplinaNome,
			String disciplinaCodigo,
			String cursoNome,
			Long professor,
			String professorNome,
			String semestre,
			LocalDate prazoInscricao,
			int vagas,
			double crMinimo,
			int pesoCr,
			int pesoEntrevista,
			double patamarMinimo,
			SelecaoSituacao situacao,
			@Nullable String motivoEncerramento,
			LocalDate abertaEm,
			@Nullable LocalDate encerradaEm,
			long totalCandidaturas,
			long pendentes,
			long aprovadas) {
		this(
				new SelecaoIdentificador(identificador.toString()),
				new DisciplinaIdentificador(disciplina.toString()),
				disciplinaNome,
				disciplinaCodigo,
				cursoNome,
				new UsuarioIdentificador(professor.toString()),
				professorNome,
				semestre,
				prazoInscricao,
				vagas,
				crMinimo,
				pesoCr,
				pesoEntrevista,
				patamarMinimo,
				situacao,
				Optional.ofNullable(motivoEncerramento),
				abertaEm,
				Optional.ofNullable(encerradaEm),
				totalCandidaturas,
				pendentes,
				aprovadas,
				List.of());
	}

	public SelecaoProjecao comMonitores(List<String> monitores) {
		return new SelecaoProjecao(
				identificador,
				disciplina,
				disciplinaNome,
				disciplinaCodigo,
				cursoNome,
				professor,
				professorNome,
				semestre,
				prazoInscricao,
				vagas,
				crMinimo,
				pesoCr,
				pesoEntrevista,
				patamarMinimo,
				situacao,
				motivoEncerramento,
				abertaEm,
				encerradaEm,
				totalCandidaturas,
				pendentes,
				aprovadas,
				monitores);
	}
}
