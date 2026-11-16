package br.com.cem.dominio.acesso.curso.spi;

import java.util.Optional;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface ICursoRepositorio extends IAgregadoRepositorio<CursoIdentificador, Curso> {
	boolean existePorNome(String nome, InstituicaoIdentificador instituicao,
			Optional<CursoIdentificador> identificadorIgnorado);

	boolean existePorSigla(String sigla, InstituicaoIdentificador instituicao,
			Optional<CursoIdentificador> identificadorIgnorado);

	boolean possuiDisciplinasVinculadas(CursoIdentificador identificador);

	boolean possuiUsuariosVinculados(CursoIdentificador identificador);
}
