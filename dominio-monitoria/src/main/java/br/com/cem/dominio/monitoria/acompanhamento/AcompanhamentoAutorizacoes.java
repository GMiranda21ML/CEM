package br.com.cem.dominio.monitoria.acompanhamento;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class AcompanhamentoAutorizacoes extends AgregadoAutorizacoes<Acompanhamento> {
	private final boolean abrir;
	private final boolean editarMetas;
	private final boolean encerrar;
	private final boolean relatorio;

	public AcompanhamentoAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean abrir,
			boolean editarMetas,
			boolean encerrar,
			boolean relatorio) {
		super(incluir, buscar, alterar, excluir);
		this.abrir = abrir;
		this.editarMetas = editarMetas;
		this.encerrar = encerrar;
		this.relatorio = relatorio;
	}

	public boolean getAbrir() {
		return abrir;
	}

	public boolean getEditarMetas() {
		return editarMetas;
	}

	public boolean getEncerrar() {
		return encerrar;
	}

	public boolean getRelatorio() {
		return relatorio;
	}
}
