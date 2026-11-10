package br.com.cem.dominio.acesso.usuario;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class UsuarioIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public UsuarioIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador do usuário em branco"));
	}
}
