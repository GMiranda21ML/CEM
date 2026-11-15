package br.com.cem.aplicacao.monitoria.alerta;

import br.com.cem.aplicacao.monitoria.alerta.spi.IAlertaAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes;
import br.com.cem.dominio.monitoria.alerta.AlertaIdentificador;
import br.com.cem.dominio.monitoria.alerta.AlertaServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class AlertaAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				AlertaIdentificador, Alerta, AlertaAutorizacoes, AlertaProjecao, AlertaConsulta> {
	public AlertaAplicacaoServico(IAlertaAplicacaoRepositorio repositorio, AlertaServico servico) {
		super(repositorio, servico);
	}
}
