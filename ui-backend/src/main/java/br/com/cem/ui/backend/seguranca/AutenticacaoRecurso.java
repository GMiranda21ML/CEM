package br.com.cem.ui.backend.seguranca;

import br.com.cem.dominio.acesso.campus.CampusAutorizacoes;
import br.com.cem.dominio.acesso.curso.CursoAutorizacoes;
import br.com.cem.dominio.acesso.disciplina.DisciplinaAutorizacoes;
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
	private final CursoAutorizacoes curso;
	private final DisciplinaAutorizacoes disciplina;
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
	private final br.com.cem.dominio.monitoria.selecao.SelecaoAutorizacoes selecao;
	private final br.com.cem.dominio.monitoria.candidatura.CandidaturaAutorizacoes candidatura;
	private final br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoAutorizacoes
			acompanhamento;
	private final br.com.cem.dominio.monitoria.feedback.FeedbackAutorizacoes feedback;

	public AutenticacaoRecurso(
			String usuarioAutenticado,
			List<String> autorizacoes,
			AgregadoAutorizacoes<DominioEmail> dominioEmail,
			UsuarioAutorizacoes usuarios,
			ConviteAutorizacoes convite,
			CampusAutorizacoes campus,
			CursoAutorizacoes curso,
			DisciplinaAutorizacoes disciplina,
			AvaliacaoAutorizacoes avaliacao,
			CertificadoAutorizacoes certificado,
			AgregadoAutorizacoes<br.com.cem.dominio.comunicacao.faq.Faq> faq,
			br.com.cem.dominio.comunicacao.pergunta.PerguntaAutorizacoes pergunta,
			br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes aviso,
			br.com.cem.dominio.monitoria.falta.FaltaAutorizacoes falta,
			br.com.cem.dominio.monitoria.alerta.AlertaAutorizacoes alerta,
			br.com.cem.dominio.monitoria.sessao.SessaoAutorizacoes sessao,
			AgregadoAutorizacoes<br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade>
					disponibilidade,
			br.com.cem.dominio.monitoria.selecao.SelecaoAutorizacoes selecao,
			br.com.cem.dominio.monitoria.candidatura.CandidaturaAutorizacoes candidatura,
			br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoAutorizacoes acompanhamento,
			br.com.cem.dominio.monitoria.feedback.FeedbackAutorizacoes feedback) {
		super(usuarioAutenticado);

		this.autorizacoes = Objects.requireNonNull(autorizacoes);
		this.dominioEmail = Objects.requireNonNull(dominioEmail);
		this.usuarios = Objects.requireNonNull(usuarios);
		this.convite = Objects.requireNonNull(convite);
		this.campus = Objects.requireNonNull(campus);
		this.curso = Objects.requireNonNull(curso);
		this.disciplina = Objects.requireNonNull(disciplina);
		this.avaliacao = Objects.requireNonNull(avaliacao);
		this.certificado = Objects.requireNonNull(certificado);
		this.faq = Objects.requireNonNull(faq);
		this.pergunta = Objects.requireNonNull(pergunta);
		this.aviso = Objects.requireNonNull(aviso);
		this.falta = Objects.requireNonNull(falta);
		this.alerta = Objects.requireNonNull(alerta);
		this.sessao = Objects.requireNonNull(sessao);
		this.disponibilidade = Objects.requireNonNull(disponibilidade);
		this.selecao = Objects.requireNonNull(selecao);
		this.candidatura = Objects.requireNonNull(candidatura);
		this.acompanhamento = Objects.requireNonNull(acompanhamento);
		this.feedback = Objects.requireNonNull(feedback);
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

	public br.com.cem.dominio.monitoria.selecao.SelecaoAutorizacoes getSelecao() {
		return selecao;
	}

	public br.com.cem.dominio.monitoria.candidatura.CandidaturaAutorizacoes getCandidatura() {
		return candidatura;
	}

	public br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoAutorizacoes
			getAcompanhamento() {
		return acompanhamento;
	}

	public br.com.cem.dominio.monitoria.feedback.FeedbackAutorizacoes getFeedback() {
		return feedback;
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

	public CursoAutorizacoes getCurso() { return curso; }

	public DisciplinaAutorizacoes getDisciplina() { return disciplina; }

	public AvaliacaoAutorizacoes getAvaliacao() {
		return avaliacao;
	}

	public CertificadoAutorizacoes getCertificado() {
		return certificado;
	}
}
