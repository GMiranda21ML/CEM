package br.com.cem.aplicacao.acesso.usuario;

import br.com.cem.aplicacao.acesso.usuario.spi.IUsuarioAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioAutorizacoes;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;

public final class UsuarioAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<UsuarioIdentificador, Usuario, UsuarioAutorizacoes, UsuarioProjecao, UsuarioConsulta> {
	public UsuarioAplicacaoServico(IUsuarioAplicacaoRepositorio repositorio, UsuarioServico servico) {
		super(repositorio, servico);
	}
}
