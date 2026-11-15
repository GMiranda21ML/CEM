package br.com.cem.dominio.comunicacao.aviso;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class AvisoAutorizacoes extends AgregadoAutorizacoes<Aviso> {
	private final boolean publicar;
	private final boolean alternar;

	public AvisoAutorizacoes(
			boolean incluir,
			boolean buscar,
			boolean alterar,
			boolean excluir,
			boolean publicar,
			boolean alternar) {
		super(incluir, buscar, alterar, excluir);
		this.publicar = publicar;
		this.alternar = alternar;
	}

	public boolean getPublicar() {
		return publicar;
	}

	public boolean getAlternar() {
		return alternar;
	}
}
