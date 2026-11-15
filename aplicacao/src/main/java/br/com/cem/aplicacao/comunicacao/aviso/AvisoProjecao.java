package br.com.cem.aplicacao.comunicacao.aviso;

import br.com.cem.dominio.comunicacao.aviso.Aviso;
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public record AvisoProjecao(
		AvisoIdentificador identificador,
		String titulo,
		String texto,
		LocalDate validade,
		boolean ativo,
		LocalDateTime publicadoEm)
		implements IProjecao<Aviso> {
	public AvisoProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(titulo);
		Objects.requireNonNull(texto);
		Objects.requireNonNull(validade);
		Objects.requireNonNull(publicadoEm);
	}

	public AvisoProjecao(
			Long identificador,
			String titulo,
			String texto,
			LocalDate validade,
			boolean ativo,
			LocalDateTime publicadoEm) {
		this(
				new AvisoIdentificador(identificador.toString()),
				titulo,
				texto,
				validade,
				ativo,
				publicadoEm);
	}
}
