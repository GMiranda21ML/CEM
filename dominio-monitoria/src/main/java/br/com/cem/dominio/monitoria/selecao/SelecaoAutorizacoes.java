package br.com.cem.dominio.monitoria.selecao;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class SelecaoAutorizacoes extends AgregadoAutorizacoes<Selecao> {
	private final boolean abrir;
	private final boolean editar;
	private final boolean encerrar;

	public SelecaoAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean abrir,
			boolean editar,
			boolean encerrar) {
		super(incluir, buscar, alterar, excluir);
		this.abrir = abrir;
		this.editar = editar;
		this.encerrar = encerrar;
	}

	public boolean getAbrir() {
		return abrir;
	}

	public boolean getEditar() {
		return editar;
	}

	public boolean getEncerrar() {
		return encerrar;
	}
}
