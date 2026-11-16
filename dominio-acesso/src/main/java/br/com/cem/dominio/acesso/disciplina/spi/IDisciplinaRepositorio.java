package br.com.cem.dominio.acesso.disciplina.spi;

import java.util.Optional;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IDisciplinaRepositorio extends IAgregadoRepositorio<DisciplinaIdentificador, Disciplina> {
	boolean existePorCodigo(DisciplinaCodigo codigo, InstituicaoIdentificador instituicao,
			Optional<DisciplinaIdentificador> identificadorIgnorado);

	boolean existePorNomeNoCurso(String nome, CursoIdentificador curso,
			Optional<DisciplinaIdentificador> identificadorIgnorado);

	boolean possuiVinculosMonitoria(DisciplinaIdentificador identificador);
}
