package br.com.cem.aplicacao.comunicacao.pergunta.spi;

import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaConsulta;
import br.com.cem.aplicacao.comunicacao.pergunta.PerguntaProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IPerguntaAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<PerguntaProjecao, PerguntaConsulta> {}
