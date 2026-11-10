package br.com.cem.aplicacao.acesso.convite;

import br.com.cem.aplicacao.acesso.convite.spi.IConviteAplicacaoRepositorio;
import br.com.cem.dominio.acesso.convite.Convite;
import br.com.cem.dominio.acesso.convite.ConviteAutorizacoes;
import br.com.cem.dominio.acesso.convite.ConviteIdentificador;
import br.com.cem.dominio.acesso.convite.ConviteServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class ConviteAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<ConviteIdentificador, Convite, ConviteAutorizacoes, ConviteProjecao, ConviteConsulta> {
	public ConviteAplicacaoServico(IConviteAplicacaoRepositorio repositorio, ConviteServico servico) {
		super(repositorio, servico);
	}
}
