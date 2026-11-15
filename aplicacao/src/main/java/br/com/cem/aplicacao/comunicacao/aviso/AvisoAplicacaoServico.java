package br.com.cem.aplicacao.comunicacao.aviso;

import br.com.cem.aplicacao.comunicacao.aviso.spi.IAvisoAplicacaoRepositorio;
import br.com.cem.dominio.comunicacao.aviso.Aviso;
import br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes;
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;
import br.com.cem.dominio.comunicacao.aviso.AvisoServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class AvisoAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				AvisoIdentificador, Aviso, AvisoAutorizacoes, AvisoProjecao, AvisoConsulta> {
	public AvisoAplicacaoServico(IAvisoAplicacaoRepositorio repositorio, AvisoServico servico) {
		super(repositorio, servico);
	}
}
