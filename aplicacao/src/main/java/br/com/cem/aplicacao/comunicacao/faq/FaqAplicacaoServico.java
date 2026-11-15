package br.com.cem.aplicacao.comunicacao.faq;

import br.com.cem.aplicacao.comunicacao.faq.spi.IFaqAplicacaoRepositorio;
import br.com.cem.dominio.comunicacao.faq.Faq;
import br.com.cem.dominio.comunicacao.faq.FaqIdentificador;
import br.com.cem.dominio.comunicacao.faq.FaqServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class FaqAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				FaqIdentificador, Faq, AgregadoAutorizacoes<Faq>, FaqProjecao, FaqConsulta> {
	public FaqAplicacaoServico(IFaqAplicacaoRepositorio repositorio, FaqServico servico) {
		super(repositorio, servico);
	}
}
