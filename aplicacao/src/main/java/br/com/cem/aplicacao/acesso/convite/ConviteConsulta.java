package br.com.cem.aplicacao.acesso.convite;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.convite.ConviteSituacao;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class ConviteConsulta extends Consulta<ConviteOrdenacao> {
	private final Optional<ConviteSituacao> situacao;

	public ConviteConsulta(Optional<ConviteOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<ConviteSituacao> situacao) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);

		this.situacao = Objects.requireNonNull(situacao);
	}

	public Optional<ConviteSituacao> getSituacao() {
		return situacao;
	}
}
