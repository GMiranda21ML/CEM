package br.com.cem.ui.backend.seguranca;

import br.com.cem.dominio.acesso.campus.CampusAutorizacoes;
import br.com.cem.dominio.acesso.convite.ConviteAutorizacoes;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioAutorizacoes;
import br.com.cem.dominio.comunicacao.certificado.CertificadoAutorizacoes;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoAutorizacoes;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoRecursoAbstrato;

import java.util.List;
import java.util.Objects;

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
	private final AgregadoAutorizacoes<br.com.cem.dominio.comunicacao.faq.Faq> faq;
	private final br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes pergunta;
	private final br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes aviso;
	private final br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes falta;
	private final br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes alerta;
	private final br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes sessao;
	private final AgregadoAutorizacoes<br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade>
			disponibilidade;

	public AutenticacaoRecurso(
			String usuarioAutenticado,
			List<String> autorizacoes,
			AgregadoAutorizacoes<DominioEmail> dominioEmail,
			UsuarioAutorizacoes usuarios,
			ConviteAutorizacoes convite,
			CampusAutorizacoes campus,
			AvaliacaoAutorizacoes avaliacao,
			CertificadoAutorizacoes certificado,
			AgregadoAutorizacoes<br.com.cem.dominio.comunicacao.faq.Faq> faq,
			br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes pergunta,
			br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes aviso,
			br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes falta,
			br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes alerta,
			br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes sessao,
			AgregadoAutorizacoes<br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade>
					disponibilidade) {
		super(usuarioAutenticado);

		this.autorizacoes = Objects.requireNonNull(autorizacoes);
		this.dominioEmail = Objects.requireNonNull(dominioEmail);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.convite = Objects.requireNonNull(convite);
		this.campus = Objects.requireNonNull(campus);
		this.avaliacao = Objects.requireNonNull(avaliacao);
		this.certificado = Objects.requireNonNull(certificado);
		this.faq = Objects.requireNonNull(faq);
		this.pergunta = Objects.requireNonNull(pergunta);
		this.aviso = Objects.requireNonNull(aviso);
		this.falta = Objects.requireNonNull(falta);
		this.alerta = Objects.requireNonNull(alerta);
		this.sessao = Objects.requireNonNull(sessao);
		this.disponibilidade = Objects.requireNonNull(disponibilidade);
	}

	public AgregadoAutorizacoes<br.com.cem.dominio.comunicacao.faq.Faq> getFaq() {
		return faq;
	}

	public br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes getPergunta() {
		return pergunta;
	}

	public br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes getAviso() {
		return aviso;
	}

	public br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes getFalta() {
		return falta;
	}

	public br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes getAlerta() {
		return alerta;
	}

	public br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes getSessao() {
		return sessao;
	}

	public AgregadoAutorizacoes<br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade>
			getDisponibilidade() {
		return disponibilidade;
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
