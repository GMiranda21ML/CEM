package br.com.cem.ui.backend.seguranca;

import java.util.List;
import java.util.Objects;

import br.com.cem.dominio.acesso.convite.ConviteAutorizacoes;
import br.com.cem.dominio.acesso.campus.CampusAutorizacoes;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioAutorizacoes;
import br.com.cem.dominio.comunicacao.certificado.CertificadoAutorizacoes;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoAutorizacoes;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoRecursoAbstrato;

public class AutenticacaoRecurso extends AutenticacaoRecursoAbstrato {
	private final List<String> autorizacoes;
	private final AgregadoAutorizacoes<DominioEmail> dominioEmail;
	// Chamado no plural porque "usuario", no singular, já é o atributo herdado com o e-mail de
	// quem está logado (ver AutenticacaoRecursoAbstrato.getUsuario()).
	private final UsuarioAutorizacoes usuarios;
	private final ConviteAutorizacoes convite;
	private final CampusAutorizacoes campus;
	private final AvaliacaoAutorizacoes avaliacao;
	private final CertificadoAutorizacoes certificado;

	public AutenticacaoRecurso(String usuarioAutenticado, List<String> autorizacoes,
			AgregadoAutorizacoes<DominioEmail> dominioEmail, UsuarioAutorizacoes usuarios, ConviteAutorizacoes convite,
			CampusAutorizacoes campus, AvaliacaoAutorizacoes avaliacao, CertificadoAutorizacoes certificado) {
		super(usuarioAutenticado);

		this.autorizacoes = Objects.requireNonNull(autorizacoes);
		this.dominioEmail = Objects.requireNonNull(dominioEmail);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.convite = Objects.requireNonNull(convite);
		this.campus = Objects.requireNonNull(campus);
		this.avaliacao = Objects.requireNonNull(avaliacao);
		this.certificado = Objects.requireNonNull(certificado);
	}

	public List<String> getAutorizacoes() {
		return autorizacoes;
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

	public CampusAutorizacoes getCampus() {
		return campus;
	}

	public AvaliacaoAutorizacoes getAvaliacao() {
		return avaliacao;
	}

	public CertificadoAutorizacoes getCertificado() {
		return certificado;
	}
}
