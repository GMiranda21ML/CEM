package br.com.cem.dominio.comunicacao.certificado;

import org.apache.commons.lang3.Validate;

import dev.sauloaraujo.d5.framework.dominio.IIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class CertificadoIdentificador extends ObjetoValorStringAbstrato implements IIdentificador {
	public CertificadoIdentificador(String valor) {
		super(Validate.notBlank(valor, "Identificador do certificado em branco"));
	}
}
