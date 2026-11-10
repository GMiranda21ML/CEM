package br.com.cem.aplicacao.acesso.convite.spi;

import br.com.cem.aplicacao.acesso.convite.ConviteConsulta;
import br.com.cem.aplicacao.acesso.convite.ConviteProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IConviteAplicacaoRepositorio extends IAgregadoAplicacaoRepositorio<ConviteProjecao, ConviteConsulta> {
}
