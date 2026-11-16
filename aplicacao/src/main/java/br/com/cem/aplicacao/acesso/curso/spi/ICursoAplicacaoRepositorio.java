package br.com.cem.aplicacao.acesso.curso.spi;

import br.com.cem.aplicacao.acesso.curso.CursoConsulta;
import br.com.cem.aplicacao.acesso.curso.CursoProjecao;
import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface ICursoAplicacaoRepositorio extends IAgregadoAplicacaoRepositorio<CursoProjecao, CursoConsulta> {}
