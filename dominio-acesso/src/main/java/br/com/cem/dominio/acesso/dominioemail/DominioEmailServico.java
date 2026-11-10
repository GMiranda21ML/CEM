package br.com.cem.dominio.acesso.dominioemail;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.dominioemail.spi.IDominioEmailRepositorio;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class DominioEmailServico
		extends AgregadoServicoAbstrato<DominioEmailDominio, DominioEmail, AgregadoAutorizacoes<DominioEmail>> {
	public static final String DOMINIO_EXISTENTE_MODELO = "O domínio de e-mail %s já está liberado";
	public static final String DOMINIO_INEXISTENTE_MODELO = "O domínio de e-mail %s não está liberado";

	public DominioEmailServico(ISegurancaServico segurancaServico, IDominioEmailRepositorio repositorio) {
		super(segurancaServico, repositorio, DOMINIO_EXISTENTE_MODELO, DOMINIO_INEXISTENTE_MODELO);
	}

	@Override
	public AgregadoAutorizacoes<DominioEmail> criarAutorizacoes(Optional<DominioEmailDominio> identificador) {
		Objects.requireNonNull(identificador);

		var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
		return new AgregadoAutorizacoes<>(coordenador, coordenador, coordenador, coordenador);
	}
}
