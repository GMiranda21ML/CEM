package br.com.cem.aplicacao.monitoria.avaliacao.spi;

import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoConsulta;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IAvaliacaoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<AvaliacaoProjecao, AvaliacaoConsulta> {
	IConsultaResultado<AvaliacaoProjecao> pesquisarDaSessao(AvaliacaoConsulta consulta, SessaoIdentificador sessao);

	double calcularMedia(SessaoIdentificador sessao);
}
