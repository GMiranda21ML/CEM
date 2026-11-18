package br.com.cem.aplicacao.monitoria.selecao.spi;

import br.com.cem.aplicacao.monitoria.selecao.DisciplinaProfessorProjecao;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoConsulta;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

import java.util.List;

public interface ISelecaoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<SelecaoProjecao, SelecaoConsulta> {
	List<DisciplinaProfessorProjecao> listarDisciplinasDoProfessor();
}
