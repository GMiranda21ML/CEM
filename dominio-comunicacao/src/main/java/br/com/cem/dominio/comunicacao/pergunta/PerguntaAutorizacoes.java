package br.com.cem.dominio.comunicacao.pergunta;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class PerguntaAutorizacoes extends AgregadoAutorizacoes<Pergunta> {
	private final boolean enviar;
	private final boolean editar;
	private final boolean responder;
	private final boolean descartar;

	public PerguntaAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean enviar,
			boolean editar,
			boolean responder,
			boolean descartar) {
		super(incluir, buscar, alterar, excluir);
		this.enviar = enviar;
		this.editar = editar;
		this.responder = responder;
		this.descartar = descartar;
	}

	public boolean getEnviar() {
		return enviar;
	}

	public boolean getEditar() {
		return editar;
	}

	public boolean getResponder() {
		return responder;
	}

	public boolean getDescartar() {
		return descartar;
	}
}
