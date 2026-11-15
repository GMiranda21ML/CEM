package br.com.cem.aplicacao.monitoria.sessao;

import br.com.cem.dominio.Autorizacao;

import java.util.Objects;

public record PessoaAgendaProjecao(String identificador, String nome, Autorizacao papel) {
	public PessoaAgendaProjecao {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(nome);
		Objects.requireNonNull(papel);
	}

	public PessoaAgendaProjecao(Long identificador, String nome, Autorizacao papel) {
		this(identificador.toString(), nome, papel);
	}
}
