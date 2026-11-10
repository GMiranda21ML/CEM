package br.com.cem.aplicacao.acesso.dominioemail.spi;

import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailConsulta;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IDominioEmailAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<DominioEmailProjecao, DominioEmailConsulta> {
}
