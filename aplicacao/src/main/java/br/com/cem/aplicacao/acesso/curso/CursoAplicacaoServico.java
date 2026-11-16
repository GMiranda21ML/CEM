package br.com.cem.aplicacao.acesso.curso;

import br.com.cem.aplicacao.acesso.curso.spi.ICursoAplicacaoRepositorio;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoAutorizacoes;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoServico;
import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class CursoAplicacaoServico extends AgregadoAplicacaoServicoAbstrato<CursoIdentificador, Curso,
		CursoAutorizacoes, CursoProjecao, CursoConsulta> {
	public CursoAplicacaoServico(ICursoAplicacaoRepositorio repositorio, CursoServico servico) {
		super(repositorio, servico);
	}
}
