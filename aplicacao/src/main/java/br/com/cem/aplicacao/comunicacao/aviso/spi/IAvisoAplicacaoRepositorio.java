package br.com.cem.aplicacao.comunicacao.aviso.spi;

import br.com.cem.aplicacao.comunicacao.aviso.AvisoConsulta;
import br.com.cem.aplicacao.comunicacao.aviso.AvisoProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IAvisoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<AvisoProjecao, AvisoConsulta> {}
