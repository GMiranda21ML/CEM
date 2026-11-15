package br.com.cem.dominio.monitoria.alerta;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class AlertaAutorizacoes extends AgregadoAutorizacoes<Alerta> {
	private final boolean acompanhar;

	public AlertaAutorizacoes(
			boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean acompanhar) {
		super(incluir, buscar, alterar, excluir);
		this.acompanhar = acompanhar;
	}

	public boolean getAcompanhar() {
		return acompanhar;
	}
}
