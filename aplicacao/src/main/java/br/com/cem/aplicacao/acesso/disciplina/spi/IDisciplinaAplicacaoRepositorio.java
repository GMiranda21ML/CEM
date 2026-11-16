package br.com.cem.aplicacao.acesso.disciplina.spi;

import br.com.cem.aplicacao.acesso.disciplina.DisciplinaConsulta;
import br.com.cem.aplicacao.acesso.disciplina.DisciplinaProjecao;
import br.com.cem.aplicacao.acesso.disciplina.ProfessorProjecao;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import java.util.List;
import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IDisciplinaAplicacaoRepositorio extends IAgregadoAplicacaoRepositorio<DisciplinaProjecao, DisciplinaConsulta> {
	List<ProfessorProjecao> buscarProfessores(CursoIdentificador curso);
}
