package br.com.cem.dominio.monitoria.feedback;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.feedback.spi.IFeedbackRepositorio;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoServico;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class FeedbackServico
		extends AgregadoServicoAbstrato<FeedbackIdentificador, Feedback, FeedbackAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe feedback com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe feedback com o identificador %s";

	private final IFeedbackRepositorio repositorio;
	private final IAcompanhamentoRepositorio acompanhamentoRepositorio;
	private final ICandidaturaRepositorio candidaturaRepositorio;
	private final ISessaoRepositorio sessaoRepositorio;
	private final IFaltaRepositorio faltaRepositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;

	public FeedbackServico(
			ISegurancaServico seguranca,
			IFeedbackRepositorio repositorio,
			IAcompanhamentoRepositorio acompanhamentoRepositorio,
			ICandidaturaRepositorio candidaturaRepositorio,
			ISessaoRepositorio sessaoRepositorio,
			IFaltaRepositorio faltaRepositorio,
			IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.acompanhamentoRepositorio = Objects.requireNonNull(acompanhamentoRepositorio);
		this.candidaturaRepositorio = Objects.requireNonNull(candidaturaRepositorio);
		this.sessaoRepositorio = Objects.requireNonNull(sessaoRepositorio);
		this.faltaRepositorio = Objects.requireNonNull(faltaRepositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public FeedbackAutorizacoes criarAutorizacoes(Optional<FeedbackIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean ehMonitor =
				papel(Autorizacao.ALUNO)
						&& usuarioRepositorio.existePorEmail(
								new UsuarioEmail(getSegurancaServico().getUsuario()),
								Optional.empty())
						&& candidaturaRepositorio.ehMonitorEmAlgumaDisciplina(usuarioAtual());
		boolean professor = papel(Autorizacao.PROFESSOR);
		boolean aluno = papel(Autorizacao.ALUNO);
		if (identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())) {
			return new FeedbackAutorizacoes(
					false,
					ehMonitor || professor || aluno,
					false,
					ehMonitor,
					ehMonitor,
					ehMonitor);
		}
		var feedback = repositorio.buscarPorIdentificador(identificador.get());
		var atual = usuarioAtual();
		boolean autor = ehMonitor && feedback.getAutor().equals(atual);
		return new FeedbackAutorizacoes(
				false,
				autor || feedback.getAluno().equals(atual) || professor,
				false,
				autor,
				ehMonitor,
				autor);
	}

	public Feedback registrar(
			SessaoIdentificador sessaoId,
			UsuarioIdentificador alunoId,
			double nota,
			Optional<String> comentario) {
		Objects.requireNonNull(sessaoId);
		Objects.requireNonNull(alunoId);
		Objects.requireNonNull(comentario);
		validarAutorizacao(Optional.empty(), FeedbackAutorizacoes::getRegistrar);
		validarExistencia(
				sessaoRepositorio::existePorIdentificador,
				sessaoId,
				SessaoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		Validate.isTrue(
				usuarioRepositorio.existePorIdentificador(alunoId),
				"Não existe aluno com o identificador informado");
		var sessao = sessaoRepositorio.buscarPorIdentificador(sessaoId);
		var atual = usuarioAtual();
		if (!sessao.getMonitor().equals(atual)) {
			throw new NaoAutorizadoExcecao(
					"Somente o monitor que conduziu a sessão pode registrar feedback");
		}
		LocalDateTime agora = relogio.agora();
		Validate.validState(
				!sessao.getCancelada() && sessao.realizada(agora),
				"Somente sessões realizadas podem receber feedback");
		Validate.isTrue(
				sessao.getAlunos().contains(alunoId),
				"O aluno não participou desta sessão");
		Validate.validState(
				!faltaRepositorio.existePorSessaoEPessoa(sessaoId, sessao.getMonitor()),
				"Sessão com falta do monitor não permite registro de feedback");
		Validate.validState(
				!faltaRepositorio.existePorSessaoEPessoa(sessaoId, alunoId),
				"Aluno com falta registrada na sessão não pode receber feedback");
		Validate.validState(
				!acompanhamentoRepositorio.existeBloqueioPorEncerramento(
						alunoId, sessao.getDisciplina(), sessao.getProfessor()),
				"O acompanhamento do aluno nesta disciplina está encerrado");
		Validate.validState(
				!repositorio.existePorSessaoEAluno(sessaoId, alunoId),
				"Este aluno já recebeu feedback nesta sessão");
		var feedback =
				new Feedback(
						Optional.empty(),
						sessaoId,
						alunoId,
						atual,
						nota,
						comentario,
						agora.toLocalDate(),
						Optional.empty());
		return repositorio.incluir(feedback);
	}

	public Feedback editar(
			FeedbackIdentificador identificador, double nota, Optional<String> comentario) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(comentario);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), FeedbackAutorizacoes::getEditar);
		var feedback = repositorio.buscarPorIdentificador(identificador);
		var sessao = sessaoRepositorio.buscarPorIdentificador(feedback.getSessao());
		Validate.validState(
				!acompanhamentoRepositorio.existeBloqueioPorEncerramento(
						feedback.getAluno(), sessao.getDisciplina(), sessao.getProfessor()),
				"Acompanhamento encerrado: feedback bloqueado");
		var editado = feedback.editar(nota, comentario, relogio.agora().toLocalDate());
		repositorio.alterar(editado);
		return editado;
	}

	@Override
	protected void validarExclusao(FeedbackIdentificador identificador) {
		Objects.requireNonNull(identificador);
		var feedback = repositorio.buscarPorIdentificador(identificador);
		var sessao = sessaoRepositorio.buscarPorIdentificador(feedback.getSessao());
		Validate.validState(
				!acompanhamentoRepositorio.existeBloqueioPorEncerramento(
						feedback.getAluno(), sessao.getDisciplina(), sessao.getProfessor()),
				"Acompanhamento encerrado: feedback não pode ser excluído");
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio
				.buscarPorEmail(new UsuarioEmail(getSegurancaServico().getUsuario()))
				.getIdentificador()
				.orElseThrow();
	}
}
