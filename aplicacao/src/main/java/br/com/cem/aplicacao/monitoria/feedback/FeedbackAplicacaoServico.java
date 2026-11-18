package br.com.cem.aplicacao.monitoria.feedback;

import br.com.cem.aplicacao.monitoria.feedback.spi.IFeedbackAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackAutorizacoes;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.feedback.FeedbackServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class FeedbackAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				FeedbackIdentificador,
				Feedback,
				FeedbackAutorizacoes,
				FeedbackProjecao,
				FeedbackConsulta> {
	public FeedbackAplicacaoServico(
			IFeedbackAplicacaoRepositorio repositorio, FeedbackServico servico) {
		super(repositorio, servico);
	}
}
