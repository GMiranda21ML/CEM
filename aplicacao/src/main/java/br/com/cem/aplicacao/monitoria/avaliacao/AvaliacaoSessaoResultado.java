package br.com.cem.aplicacao.monitoria.avaliacao;

import java.util.Objects;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;

public final class AvaliacaoSessaoResultado {
	private final IConsultaResultado<AvaliacaoProjecao> avaliacoes;
	private final double media;

	public AvaliacaoSessaoResultado(IConsultaResultado<AvaliacaoProjecao> avaliacoes, double media) {
		this.avaliacoes = Objects.requireNonNull(avaliacoes);
		this.media = media;
	}

	public IConsultaResultado<AvaliacaoProjecao> getAvaliacoes() {
		return avaliacoes;
	}

	public double getMedia() {
		return media;
	}
}
