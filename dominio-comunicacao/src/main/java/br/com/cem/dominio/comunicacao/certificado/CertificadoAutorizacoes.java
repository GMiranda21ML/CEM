package br.com.cem.dominio.comunicacao.certificado;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;

public final class CertificadoAutorizacoes extends AgregadoAutorizacoes<Certificado> {
	private final boolean emitir;

	public CertificadoAutorizacoes(boolean incluir, boolean buscar, boolean alterar, boolean excluir, boolean emitir) {
		super(incluir, buscar, alterar, excluir);
		this.emitir = emitir;
	}

	public boolean getEmitir() {
		return emitir;
	}
}
