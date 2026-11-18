package br.com.cem.dominio.monitoria.acompanhamento;

import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IObjetoValor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public record SessaoRelatorioItem(
		SessaoIdentificador sessao,
		LocalDate data,
		String situacao,
		boolean valida,
		boolean presente,
		Optional<Double> nota,
		Optional<String> comentario)
		implements IObjetoValor {
	public SessaoRelatorioItem {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(data);
		Objects.requireNonNull(situacao);
		Objects.requireNonNull(nota);
		Objects.requireNonNull(comentario);
	}
}
