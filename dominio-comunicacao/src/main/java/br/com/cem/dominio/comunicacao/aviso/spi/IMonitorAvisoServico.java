package br.com.cem.dominio.comunicacao.aviso.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

public interface IMonitorAvisoServico {
	boolean eMonitor(UsuarioIdentificador usuario);
}
