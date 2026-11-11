package br.com.cem.dominio.comunicacao.certificado;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import dev.sauloaraujo.d5.framework.dominio.IObjetoValor;

public final class Periodo implements IObjetoValor {
	private final LocalDate inicio;
	private final LocalDate fim;

	public Periodo(LocalDate inicio, LocalDate fim) {
		this.inicio = Objects.requireNonNull(inicio);
		this.fim = Objects.requireNonNull(fim);
		Validate.isTrue(!fim.isBefore(inicio), "A data final do período não pode ser anterior à data inicial");
	}

	public LocalDate getInicio() {
		return inicio;
	}

	public LocalDate getFim() {
		return fim;
	}

	@Override
	public boolean equals(@Nullable Object objeto) {
		return this == objeto || objeto instanceof Periodo outro && inicio.equals(outro.inicio) && fim.equals(outro.fim);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inicio, fim);
	}
}
