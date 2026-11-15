package br.com.cem.dominio.monitoria.falta;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class FaltaAutorizacoes extends AgregadoAutorizacoes<Falta> {
	private final boolean registrar;
	private final boolean editar;
	private final boolean sugerir;
	private final boolean agendar;

	public FaltaAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean registrar,
			boolean editar,
			boolean sugerir,
			boolean agendar) {
		super(incluir, buscar, alterar, excluir);
		this.registrar = registrar;
		this.editar = editar;
		this.sugerir = sugerir;
		this.agendar = agendar;
	}

	public boolean getRegistrar() {
		return registrar;
	}

	public boolean getEditar() {
		return editar;
	}

	public boolean getSugerir() {
		return sugerir;
	}

	public boolean getAgendar() {
		return agendar;
	}
}
