package br.com.cem.infraestrutura.acesso.curso;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoServico;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoRepositorioAbstrato;

public class CursoRepositorio extends AgregadoRepositorioAbstrato<CursoIdentificador, Curso, Long, CursoJpa>
		implements ICursoRepositorio {
	private final ICursoRepositorioJpa repositorioJpa;

	public CursoRepositorio(ICursoRepositorioJpa repositorioJpa) {
		super(repositorioJpa, CursoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override protected CursoJpa paraJpa(Curso curso) {
		Objects.requireNonNull(curso);
		return new CursoJpa(curso.getIdentificador().map(this::paraIdentificadorJpa).orElse(null),
				curso.getInstituicao().toString(), curso.getNome(), curso.getSigla());
	}

	@Override protected Curso paraAgregado(CursoJpa jpa) {
		Objects.requireNonNull(jpa);
		return new Curso(Optional.of(new CursoIdentificador(Objects.requireNonNull(jpa.getIdentificador()).toString())),
				new InstituicaoIdentificador(jpa.getInstituicao()), jpa.getNome(), jpa.getSigla());
	}

	@Override protected Long paraIdentificadorJpa(CursoIdentificador id) {
		Objects.requireNonNull(id);
		return converter(id).orElseThrow(() -> new AgregadoInexistenteExcecao(
				CursoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id)));
	}

	@Override public boolean existePorIdentificador(CursoIdentificador id) {
		Objects.requireNonNull(id);
		return converter(id).isPresent() && super.existePorIdentificador(id);
	}

	@Override public boolean existePorNome(String nome, InstituicaoIdentificador instituicao,
			Optional<CursoIdentificador> ignorado) {
		Objects.requireNonNull(nome); Objects.requireNonNull(instituicao); Objects.requireNonNull(ignorado);
		return repositorioJpa.existePorNome(nome, instituicao.toString(), ignorado.flatMap(CursoRepositorio::converter).orElse(null));
	}

	@Override public boolean existePorSigla(String sigla, InstituicaoIdentificador instituicao,
			Optional<CursoIdentificador> ignorado) {
		Objects.requireNonNull(sigla); Objects.requireNonNull(instituicao); Objects.requireNonNull(ignorado);
		return repositorioJpa.existePorSigla(sigla, instituicao.toString(), ignorado.flatMap(CursoRepositorio::converter).orElse(null));
	}

	@Override public boolean possuiDisciplinasVinculadas(CursoIdentificador id) {
		Objects.requireNonNull(id);
		return converter(id).map(repositorioJpa::possuiDisciplinas).orElse(false);
	}

	@Override public boolean possuiUsuariosVinculados(CursoIdentificador id) {
		Objects.requireNonNull(id);
		return converter(id).map(repositorioJpa::possuiUsuarios).orElse(false);
	}

	private static Optional<Long> converter(CursoIdentificador id) {
		try { return Optional.of(Long.valueOf(id.toString())); }
		catch (NumberFormatException excecao) { return Optional.empty(); }
	}
}
