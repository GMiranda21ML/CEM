package br.com.cem.aplicacao.monitoria.falta.spi;

import br.com.cem.aplicacao.monitoria.falta.FaltaConsulta;
import br.com.cem.aplicacao.monitoria.falta.FaltaProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IFaltaAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<FaltaProjecao, FaltaConsulta> {}
