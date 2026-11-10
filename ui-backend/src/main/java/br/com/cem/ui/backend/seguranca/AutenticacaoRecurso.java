package br.com.cem.ui.backend.seguranca;

import java.util.Objects;

import br.com.cem.dominio.acesso.convite.ConviteAutorizacoes;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioAutorizacoes;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoRecursoAbstrato;

public class AutenticacaoRecurso extends AutenticacaoRecursoAbstrato {
	private final AgregadoAutorizacoes<DominioEmail> dominioEmail;
	// Chamado no plural porque "usuario", no singular, já é o atributo herdado com o e-mail de
	// quem está logado (ver AutenticacaoRecursoAbstrato.getUsuario()).
	private final UsuarioAutorizacoes usuarios;
	private final ConviteAutorizacoes convite;

	public AutenticacaoRecurso(String usuarioAutenticado, AgregadoAutorizacoes<DominioEmail> dominioEmail,
			UsuarioAutorizacoes usuarios, ConviteAutorizacoes convite) {
		super(usuarioAutenticado);

		this.dominioEmail = Objects.requireNonNull(dominioEmail);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.convite = Objects.requireNonNull(convite);
	}

	public AgregadoAutorizacoes<DominioEmail> getDominioEmail() {
		return dominioEmail;
	}

	public UsuarioAutorizacoes getUsuarios() {
		return usuarios;
	}

	public ConviteAutorizacoes getConvite() {
		return convite;
	}
}
