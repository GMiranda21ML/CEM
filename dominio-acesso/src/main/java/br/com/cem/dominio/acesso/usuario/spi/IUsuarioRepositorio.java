package br.com.cem.dominio.acesso.usuario.spi;

import java.util.Optional;

import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IUsuarioRepositorio extends IAgregadoRepositorio<UsuarioIdentificador, Usuario> {
	boolean existePorEmail(UsuarioEmail emailInstitucional, Optional<UsuarioIdentificador> identificadorIgnorado);

	Usuario buscarPorEmail(UsuarioEmail emailInstitucional);
}
