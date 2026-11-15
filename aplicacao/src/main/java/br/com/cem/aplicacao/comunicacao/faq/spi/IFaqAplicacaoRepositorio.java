package br.com.cem.aplicacao.comunicacao.faq.spi;

import br.com.cem.aplicacao.comunicacao.faq.FaqConsulta;
import br.com.cem.aplicacao.comunicacao.faq.FaqProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IFaqAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<FaqProjecao, FaqConsulta> {}
