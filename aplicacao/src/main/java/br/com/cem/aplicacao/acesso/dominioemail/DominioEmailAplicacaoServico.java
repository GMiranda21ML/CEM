package br.com.cem.aplicacao.acesso.dominioemail;

import br.com.cem.aplicacao.acesso.dominioemail.spi.IDominioEmailAplicacaoRepositorio;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class DominioEmailAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<DominioEmailDominio, DominioEmail, AgregadoAutorizacoes<DominioEmail>, DominioEmailProjecao, DominioEmailConsulta> {
	public DominioEmailAplicacaoServico(IDominioEmailAplicacaoRepositorio repositorio, DominioEmailServico servico) {
		super(repositorio, servico);
	}
}
