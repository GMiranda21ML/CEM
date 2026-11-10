package br.com.cem.dominio.acesso.convite;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class ConviteAutorizacoes extends AgregadoAutorizacoes<Convite> {
	private final boolean reenviar;
	private final boolean cancelar;

	public ConviteAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean reenviar,
			boolean cancelar) {
		super(incluir, buscar, alterar, excluir);

		this.reenviar = reenviar;
		this.cancelar = cancelar;
	}

	public boolean getReenviar() {
		return reenviar;
	}

	public boolean getCancelar() {
		return cancelar;
	}
}
