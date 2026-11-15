package br.com.cem.aplicacao.monitoria.alerta.spi;

import br.com.cem.aplicacao.monitoria.alerta.AlertaConsulta;
import br.com.cem.aplicacao.monitoria.alerta.AlertaProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IAlertaAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<AlertaProjecao, AlertaConsulta> {}
