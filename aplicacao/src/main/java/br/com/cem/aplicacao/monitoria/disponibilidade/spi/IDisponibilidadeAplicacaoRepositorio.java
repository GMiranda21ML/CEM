package br.com.cem.aplicacao.monitoria.disponibilidade.spi;

import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeConsulta;
import br.com.cem.aplicacao.monitoria.disponibilidade.DisponibilidadeProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IDisponibilidadeAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<DisponibilidadeProjecao, DisponibilidadeConsulta> {}
