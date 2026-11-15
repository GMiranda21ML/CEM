package br.com.cem.aplicacao.monitoria.sessao;

import br.com.cem.aplicacao.monitoria.sessao.spi.ISessaoAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class SessaoAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				SessaoIdentificador, Sessao, SessaoAutorizacoes, SessaoProjecao, SessaoConsulta> {
	private final ISessaoAplicacaoRepositorio repositorio;

	public SessaoAplicacaoServico(ISessaoAplicacaoRepositorio repositorio, SessaoServico servico) {
		super(repositorio, servico);
		this.repositorio = java.util.Objects.requireNonNull(repositorio);
	}

	public ContextoAgenda contexto() {
		return repositorio.contexto();
	}
}
