package br.com.cem.infraestrutura.comunicacao.certificado;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.spi.IGeradorCodigoVerificacao;

@Component
public final class GeradorCodigoVerificacao implements IGeradorCodigoVerificacao {
	@Override
	public CodigoVerificacao gerar() {
		var aleatorio = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		return new CodigoVerificacao("CEM-" + aleatorio.substring(0, 20));
	}
}
