package br.com.cem.ui.backend.seguranca;

import br.com.cem.dominio.acesso.campus.CampusServico;
import br.com.cem.dominio.acesso.curso.CursoServico;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import br.com.cem.dominio.acesso.convite.ConviteServico;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;
import br.com.cem.dominio.acesso.usuario.UsuarioServico;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.ui.Backend;
import dev.sauloaraujo.d5.framework.ui.seguranca.AutenticacaoControladorAbstrato;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(Backend.PREFIXO + "autenticacao")
public class AutenticacaoControlador extends AutenticacaoControladorAbstrato {
	private final DominioEmailServico dominioEmailServico;
	private final UsuarioServico usuarioServico;
	private final ConviteServico conviteServico;
	private final CampusServico campusServico;
	private final CursoServico cursoServico;
	private final DisciplinaServico disciplinaServico;
	private final AvaliacaoServico avaliacaoServico;
	private final CertificadoServico certificadoServico;
	private final br.com.cem.dominio.comunicacao.faq.FaqServico faqServico;
	private final br.com.cem.dominio.comunicacao.pergunta.PerguntaServico perguntaServico;
	private final br.com.cem.dominio.comunicacao.aviso.AvisoServico avisoServico;
	private final br.com.cem.dominio.monitoria.falta.FaltaServico faltaServico;
	private final br.com.cem.dominio.monitoria.alerta.AlertaServico alertaServico;
	private final br.com.cem.dominio.monitoria.sessao.SessaoServico sessaoServico;
	private final br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeServico
			disponibilidadeServico;
	private final br.com.cem.dominio.monitoria.selecao.SelecaoServico selecaoServico;
	private final br.com.cem.dominio.monitoria.candidatura.CandidaturaServico
			candidaturaServico;
	private final br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoServico
			acompanhamentoServico;
	private final br.com.cem.dominio.monitoria.feedback.FeedbackServico feedbackServico;

	public AutenticacaoControlador(
			ISegurancaServico segurancaServico,
			DominioEmailServico dominioEmailServico,
			UsuarioServico usuarioServico,
			ConviteServico conviteServico,
			CampusServico campusServico,
			CursoServico cursoServico,
			DisciplinaServico disciplinaServico,
			AvaliacaoServico avaliacaoServico,
			CertificadoServico certificadoServico,
			br.com.cem.dominio.comunicacao.faq.FaqServico faqServico,
			br.com.cem.dominio.comunicacao.pergunta.PerguntaServico perguntaServico,
			br.com.cem.dominio.comunicacao.aviso.AvisoServico avisoServico,
			br.com.cem.dominio.monitoria.falta.FaltaServico faltaServico,
			br.com.cem.dominio.monitoria.alerta.AlertaServico alertaServico,
			br.com.cem.dominio.monitoria.sessao.SessaoServico sessaoServico,
			br.com.cem.dominio.monitoria.disponibilidade.DisponibilidadeServico
					disponibilidadeServico,
			br.com.cem.dominio.monitoria.selecao.SelecaoServico selecaoServico,
			br.com.cem.dominio.monitoria.candidatura.CandidaturaServico candidaturaServico,
			br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoServico
					acompanhamentoServico,
			br.com.cem.dominio.monitoria.feedback.FeedbackServico feedbackServico) {
		super(segurancaServico);

		this.dominioEmailServico = Objects.requireNonNull(dominioEmailServico);
		this.usuarioServico = Objects.requireNonNull(usuarioServico);
		this.conviteServico = Objects.requireNonNull(conviteServico);
		this.campusServico = Objects.requireNonNull(campusServico);
		this.cursoServico = Objects.requireNonNull(cursoServico);
		this.disciplinaServico = Objects.requireNonNull(disciplinaServico);
		this.avaliacaoServico = Objects.requireNonNull(avaliacaoServico);
		this.certificadoServico = Objects.requireNonNull(certificadoServico);
		this.faqServico = Objects.requireNonNull(faqServico);
		this.perguntaServico = Objects.requireNonNull(perguntaServico);
		this.avisoServico = Objects.requireNonNull(avisoServico);
		this.faltaServico = Objects.requireNonNull(faltaServico);
		this.alertaServico = Objects.requireNonNull(alertaServico);
		this.sessaoServico = Objects.requireNonNull(sessaoServico);
		this.disponibilidadeServico = Objects.requireNonNull(disponibilidadeServico);
		this.selecaoServico = Objects.requireNonNull(selecaoServico);
		this.candidaturaServico = Objects.requireNonNull(candidaturaServico);
		this.acompanhamentoServico = Objects.requireNonNull(acompanhamentoServico);
		this.feedbackServico = Objects.requireNonNull(feedbackServico);
	}

	@Override
	protected AutenticacaoRecurso recurso() {
		var dominioEmail = dominioEmailServico.criarAutorizacoes(Optional.empty());
		var usuarios = usuarioServico.criarAutorizacoes(Optional.empty());
		var convite = conviteServico.criarAutorizacoes(Optional.empty());
		var campus = campusServico.criarAutorizacoes(Optional.empty());
		var curso = cursoServico.criarAutorizacoes(Optional.empty());
		var disciplina = disciplinaServico.criarAutorizacoes(Optional.empty());
		var avaliacao = avaliacaoServico.criarAutorizacoes(Optional.empty());
		var certificado = certificadoServico.criarAutorizacoes(Optional.empty());
		return new AutenticacaoRecurso(
				segurancaServico.getUsuario(),
				segurancaServico.getAutorizacoes(),
				dominioEmail,
				usuarios,
				convite,
				campus,
				curso,
				disciplina,
				avaliacao,
				certificado,
				faqServico.criarAutorizacoes(Optional.empty()),
				perguntaServico.criarAutorizacoes(Optional.empty()),
				avisoServico.criarAutorizacoes(Optional.empty()),
				faltaServico.criarAutorizacoes(Optional.empty()),
				alertaServico.criarAutorizacoes(Optional.empty()),
				sessaoServico.criarAutorizacoes(Optional.empty()),
				disponibilidadeServico.criarAutorizacoes(Optional.empty()),
				selecaoServico.criarAutorizacoes(Optional.empty()),
				candidaturaServico.criarAutorizacoes(Optional.empty()),
				acompanhamentoServico.criarAutorizacoes(Optional.empty()),
				feedbackServico.criarAutorizacoes(Optional.empty()));
	}
}
