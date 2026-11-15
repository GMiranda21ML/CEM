package br.com.cem.aplicacao.monitoria.falta;

import br.com.cem.aplicacao.monitoria.falta.spi.IFaltaAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.FaltaServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class FaltaAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				FaltaIdentificador, Falta, FaltaAutorizacoes, FaltaProjecao, FaltaConsulta> {
	public FaltaAplicacaoServico(IFaltaAplicacaoRepositorio repositorio, FaltaServico servico) {
		super(repositorio, servico);
	}
}
