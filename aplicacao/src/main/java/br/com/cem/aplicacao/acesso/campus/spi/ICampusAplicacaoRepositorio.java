package br.com.cem.aplicacao.acesso.campus.spi;

import br.com.cem.aplicacao.acesso.campus.CampusConsulta;
import br.com.cem.aplicacao.acesso.campus.CampusProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface ICampusAplicacaoRepositorio extends IAgregadoAplicacaoRepositorio<CampusProjecao, CampusConsulta> {
}
