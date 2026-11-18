package br.com.cem.aplicacao.monitoria.feedback.spi;

import br.com.cem.aplicacao.monitoria.feedback.FeedbackConsulta;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IFeedbackAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<FeedbackProjecao, FeedbackConsulta> {}
