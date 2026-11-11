package br.com.cem.aplicacao.monitoria.avaliacao;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class AvaliacaoConsulta extends Consulta<AvaliacaoOrdenacao> {
	private final Optional<SessaoIdentificador> sessao;

	public AvaliacaoConsulta(Optional<AvaliacaoOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<SessaoIdentificador> sessao) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.sessao = Objects.requireNonNull(sessao);
	}

	public Optional<SessaoIdentificador> getSessao() {
		return sessao;
	}
}
