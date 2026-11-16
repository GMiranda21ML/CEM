package br.com.cem.dominio.monitoria.sessao;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class SessaoAutorizacoes extends AgregadoAutorizacoes<Sessao> {
	private final boolean cancelar;
	private final boolean registrar;
	private final boolean adicionarAluno;
	private final boolean responder;
	private final boolean remarcar;

	public SessaoAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean cancelar,
			boolean registrar,
			boolean adicionarAluno,
			boolean responder,
			boolean remarcar) {
		super(incluir, buscar, alterar, excluir);
		this.cancelar = cancelar;
		this.registrar = registrar;
		this.adicionarAluno = adicionarAluno;
		this.responder = responder;
		this.remarcar = remarcar;
	}

	public boolean getCancelar() {
		return cancelar;
	}

	public boolean getRegistrar() {
		return registrar;
	}

	public boolean getAdicionarAluno() {
		return adicionarAluno;
	}

	public boolean getResponder() {
		return responder;
	}

	public boolean getRemarcar() {
		return remarcar;
	}
}
