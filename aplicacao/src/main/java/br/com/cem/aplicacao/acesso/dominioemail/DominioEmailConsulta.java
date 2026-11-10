package br.com.cem.aplicacao.acesso.dominioemail;

import java.util.Optional;

import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class DominioEmailConsulta extends Consulta<DominioEmailOrdenacao> {
	public DominioEmailConsulta(Optional<DominioEmailOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
	}
}
