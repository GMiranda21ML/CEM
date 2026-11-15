package br.com.cem.aplicacao.monitoria.sessao;

import java.util.List;
import java.util.Objects;

public record ContextoAgenda(
		String usuario, List<PessoaAgendaProjecao> pessoas, List<CampusAgendaProjecao> campus) {
	public ContextoAgenda {
		Objects.requireNonNull(usuario);
		pessoas = List.copyOf(pessoas);
		campus = List.copyOf(campus);
	}
}
