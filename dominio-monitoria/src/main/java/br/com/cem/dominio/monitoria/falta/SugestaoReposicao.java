package br.com.cem.dominio.monitoria.falta;

import br.com.cem.dominio.acesso.campus.CampusIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IObjetoValor;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;

public record SugestaoReposicao(LocalDateTime inicio, LocalDateTime fim, CampusIdentificador campus)
		implements IObjetoValor {
	public SugestaoReposicao {
		Objects.requireNonNull(inicio);
		Objects.requireNonNull(fim);
		Objects.requireNonNull(campus);
		Validate.isTrue(fim.isAfter(inicio), "Horário inválido");
	}
}
