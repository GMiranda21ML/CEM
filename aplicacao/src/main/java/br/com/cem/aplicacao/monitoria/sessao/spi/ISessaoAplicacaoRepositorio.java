package br.com.cem.aplicacao.monitoria.sessao.spi;

import br.com.cem.aplicacao.monitoria.sessao.ContextoAgenda;
import br.com.cem.aplicacao.monitoria.sessao.SessaoConsulta;
import br.com.cem.aplicacao.monitoria.sessao.SessaoProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface ISessaoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<SessaoProjecao, SessaoConsulta> {
	ContextoAgenda contexto();
}
