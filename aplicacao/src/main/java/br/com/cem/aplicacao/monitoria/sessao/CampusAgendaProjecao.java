package br.com.cem.aplicacao.monitoria.sessao;

import java.util.Objects;

public record CampusAgendaProjecao(String identificador, String nome) {
	public CampusAgendaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(nome);
	}

	public CampusAgendaProjecao(Long identificador, String nome) {
		this(identificador.toString(), nome);
	}
}
