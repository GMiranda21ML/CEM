package br.com.cem.aplicacao.acesso.campus;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.campus.CampusSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class CampusConsulta extends Consulta<CampusOrdenacao> {
	private final Optional<String> nome;
	private final Optional<CampusSituacao> situacao;

	public CampusConsulta(Optional<CampusOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<String> nome,
			Optional<CampusSituacao> situacao) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.nome = Objects.requireNonNull(nome);
		this.situacao = Objects.requireNonNull(situacao);
	}

	public Optional<String> getNome() {
		return nome;
	}

	public Optional<CampusSituacao> getSituacao() {
		return situacao;
	}
}
