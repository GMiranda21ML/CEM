package br.com.cem.aplicacao.acesso.campus;

import br.com.cem.aplicacao.acesso.campus.spi.ICampusAplicacaoRepositorio;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusAutorizacoes;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class CampusAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<CampusIdentificador, Campus, CampusAutorizacoes, CampusProjecao, CampusConsulta> {
	public CampusAplicacaoServico(ICampusAplicacaoRepositorio repositorio, CampusServico servico) {
		super(repositorio, servico);
	}
}
