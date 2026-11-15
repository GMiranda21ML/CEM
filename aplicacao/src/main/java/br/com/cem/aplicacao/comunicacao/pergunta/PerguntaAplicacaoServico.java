package br.com.cem.aplicacao.comunicacao.pergunta;

import br.com.cem.aplicacao.comunicacao.pergunta.spi.IPerguntaAplicacaoRepositorio;
import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class PerguntaAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				PerguntaIdentificador,
				Pergunta,
				PerguntaAutorizacoes,
				PerguntaProjecao,
				PerguntaConsulta> {
	public PerguntaAplicacaoServico(
			IPerguntaAplicacaoRepositorio repositorio, PerguntaServico servico) {
		super(repositorio, servico);
	}
}
