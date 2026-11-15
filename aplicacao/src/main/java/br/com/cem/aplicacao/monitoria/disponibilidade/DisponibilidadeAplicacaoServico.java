package br.com.cem.aplicacao.monitoria.disponibilidade;

import br.com.cem.aplicacao.monitoria.disponibilidade.spi.IDisponibilidadeAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeIdentificador;
import br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class DisponibilidadeAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				DisponibilidadeIdentificador,
				Disponibilidade,
				AgregadoAutorizacoes<Disponibilidade>,
				DisponibilidadeProjecao,
				DisponibilidadeConsulta> {
	public DisponibilidadeAplicacaoServico(
			IDisponibilidadeAplicacaoRepositorio repositorio, DisponibilidadeServico servico) {
		super(repositorio, servico);
	}
}
