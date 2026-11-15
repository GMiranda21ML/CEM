package br.com.cem.dominio.monitoria.alerta.spi;

import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import java.util.Collection;

public interface ICoordenadoresAlertaServico {
	Collection<Usuario> buscarPorPessoa(UsuarioIdentificador pessoa);
}
