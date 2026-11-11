package br.com.cem.dominio.monitoria.avaliacao;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class AvaliacaoAutorizacoes extends AgregadoAutorizacoes<Avaliacao> {
	private final boolean registrar;
	private final boolean editar;

	public AvaliacaoAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean registrar,
			boolean editar) {
		super(incluir, buscar, alterar, excluir);
		this.registrar = registrar;
		this.editar = editar;
	}

	public boolean getRegistrar() {
		return registrar;
	}

	public boolean getEditar() {
		return editar;
	}
}
