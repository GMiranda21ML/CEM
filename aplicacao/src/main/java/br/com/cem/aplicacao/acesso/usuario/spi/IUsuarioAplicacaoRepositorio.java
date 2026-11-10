package br.com.cem.aplicacao.acesso.usuario.spi;

import br.com.cem.aplicacao.acesso.usuario.UsuarioConsulta;
import br.com.cem.aplicacao.acesso.usuario.UsuarioProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface IUsuarioAplicacaoRepositorio extends IAgregadoAplicacaoRepositorio<UsuarioProjecao, UsuarioConsulta> {
}
