package br.com.cem.dominio.monitoria.feedback;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class FeedbackAutorizacoes extends AgregadoAutorizacoes<Feedback> {
	private final boolean registrar;
	private final boolean editar;

	public FeedbackAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean registrar,
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
