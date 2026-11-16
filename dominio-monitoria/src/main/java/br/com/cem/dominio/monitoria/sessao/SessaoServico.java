package br.com.cem.dominio.monitoria.sessao;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class SessaoServico
		extends AgregadoServicoAbstrato<SessaoIdentificador, Sessao, SessaoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe sessão com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe sessão com o identificador %s";
	private static final String ASSUNTO_SESSAO = "Sessão de monitoria atualizada";
	private final ISessaoRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final ICampusRepositorio campusRepositorio;
	private final IEnviadorEmailServico enviadorEmail;
	private final IRelogioServico relogio;

	public SessaoServico(
			ISegurancaServico seguranca,
			ISessaoRepositorio repositorio,
			IUsuarioRepositorio usuarioRepositorio,
			ICampusRepositorio campusRepositorio,
			IEnviadorEmailServico enviadorEmail,
			IRelogioServico relogio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.campusRepositorio = Objects.requireNonNull(campusRepositorio);
		this.enviadorEmail = Objects.requireNonNull(enviadorEmail);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public SessaoAutorizacoes criarAutorizacoes(Optional<SessaoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean coordenador = papel(Autorizacao.COORDENADOR);
		boolean aluno = papel(Autorizacao.ALUNO);
		var usuario = usuarioAtual();
		var existente =
				identificador
						.filter(repositorio::existePorIdentificador)
						.map(repositorio::buscarPorIdentificador);
		boolean participante = existente.map(s -> s.participa(usuario)).orElse(false);
		boolean monitor = existente.map(s -> s.getMonitor().equals(usuario)).orElse(false);
		return new SessaoAutorizacoes(
				false,
				coordenador || participante || aluno,
				false,
				false,
				coordenador || monitor,
				coordenador || aluno,
				coordenador || monitor,
				!coordenador && aluno && existente.map(s -> s.getAlunos().contains(usuario)).orElse(false),
				coordenador || monitor);
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

	@Override
	protected void validarInclusao(Sessao a) {
		Objects.requireNonNull(a);
		Validate.isTrue(
				a.getIdentificador().isEmpty() && !a.getCancelada(), "Dados iniciais inválidos");
		validarDados(a, Optional.empty());
	}

	@Override
	protected void validarInclusaoAlteracao(Sessao a) {
		validarDados(a, Optional.empty());
	}

	private void validarDados(Sessao sessao, Optional<SessaoIdentificador> ignorada) {
		Objects.requireNonNull(sessao);
		Validate.validState(
				campusRepositorio.buscarPorIdentificador(sessao.getCampus()).estaAtivo(),
				"Campus inativo");
		Usuario professor = usuarioRepositorio.buscarPorIdentificador(sessao.getProfessor());
		Usuario monitor = usuarioRepositorio.buscarPorIdentificador(sessao.getMonitor());
		Validate.isTrue(
				professor.getPapel() == Autorizacao.PROFESSOR
						&& professor.getSituacao() == UsuarioSituacao.ATIVO,
				"Professor inválido ou inativo");
		Validate.isTrue(
				monitor.getPapel() == Autorizacao.ALUNO
						&& monitor.getSituacao() == UsuarioSituacao.ATIVO,
				"Monitor inválido ou inativo");
		Validate.notBlank(monitor.getCurso(), "O monitor deve possuir curso cadastrado");
		Validate.isTrue(sessao.getInicio().isAfter(relogio.agora()), "A sessão deve começar no futuro");
		for (UsuarioIdentificador alunoId : sessao.getAlunos()) {
			Usuario aluno = usuarioRepositorio.buscarPorIdentificador(alunoId);
			Validate.isTrue(
					aluno.getPapel() == Autorizacao.ALUNO
							&& aluno.getSituacao() == UsuarioSituacao.ATIVO,
					"Participante inválido ou inativo");
			Validate.isTrue(
					Objects.equals(monitor.getCurso(), aluno.getCurso()),
					"O aluno deve pertencer ao curso do monitor da sessão");
			Validate.validState(
					!repositorio.conflita(
							sessao.getMonitor(), alunoId, sessao.getInicio(), sessao.getFim(), ignorada),
					"Conflito na agenda");
		}
	}

	/** Registra a sessão e notifica cada aluno participante. */
	public Sessao registrar(Sessao sessao) {
		Objects.requireNonNull(sessao);
		validarAutorizacao(Optional.empty(), SessaoAutorizacoes::getRegistrar);
		Validate.isTrue(
				papel(Autorizacao.COORDENADOR)
						|| sessao.getMonitor().equals(usuarioAtual())
							&& papel(Autorizacao.ALUNO),
				"O monitor só pode registrar as próprias sessões");
		validarInclusao(sessao);
		var cadastrada = repositorio.incluir(sessao);
		notificarParticipantes(cadastrada, "Uma sessão foi marcada para você.");
		return cadastrada;
	}

	public Sessao adicionarAluno(SessaoIdentificador id, UsuarioIdentificador alunoId) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(alunoId);
		validarAutorizacao(Optional.of(id), SessaoAutorizacoes::getAdicionarAluno);
		validarExistencia(id);
		Sessao sessao = repositorio.buscarPorIdentificador(id);
		Validate.isTrue(sessao.getMonitor().equals(usuarioAtual()), "Somente o monitor gerencia os convidados");
		Validate.validState(!sessao.getCancelada(), "Sessão cancelada não aceita novos alunos");
		Validate.validState(relogio.agora().isBefore(sessao.getInicio()), "A sessão já começou");
		Usuario monitor = usuarioRepositorio.buscarPorIdentificador(sessao.getMonitor());
		Usuario aluno = usuarioRepositorio.buscarPorIdentificador(alunoId);
		Validate.isTrue(
				aluno.getPapel() == Autorizacao.ALUNO
						&& aluno.getSituacao() == UsuarioSituacao.ATIVO
						&& Objects.equals(monitor.getCurso(), aluno.getCurso()),
				"O aluno não pertence ao curso do monitor da sessão");
		Validate.validState(
				!repositorio.conflita(
						sessao.getMonitor(), alunoId, sessao.getInicio(), sessao.getFim(), Optional.of(id)),
				"Conflito na agenda do aluno");
		Sessao atualizada = sessao.adicionarAluno(alunoId);
		repositorio.alterar(atualizada);
		notificar(aluno, atualizada, "Você foi incluído em uma sessão de monitoria.");
		return atualizada;
	}

	public Sessao responder(SessaoIdentificador id, RespostaSessao resposta) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(resposta);
		validarAutorizacao(Optional.of(id), SessaoAutorizacoes::getResponder);
		validarExistencia(id);
		var sessao = repositorio.buscarPorIdentificador(id);
		var aluno = usuarioAtual();
		Validate.isTrue(resposta.getAluno().equals(aluno), "Só é possível responder pela própria conta");
		Validate.validState(!sessao.getCancelada(), "Sessão cancelada não aceita respostas");
		resposta.getInicioSugerido().ifPresent(
				inicio -> Validate.isTrue(inicio.isAfter(relogio.agora()), "A sugestão deve estar no futuro"));
		var atualizada = sessao.responder(resposta, relogio.agora());
		repositorio.alterar(atualizada);
		return atualizada;
	}

	public Sessao remarcarParaSugestao(SessaoIdentificador id, UsuarioIdentificador alunoId) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(alunoId);
		validarAutorizacao(Optional.of(id), SessaoAutorizacoes::getRemarcar);
		validarExistencia(id);
		var sessao = repositorio.buscarPorIdentificador(id);
		Validate.isTrue(sessao.getMonitor().equals(usuarioAtual()), "Somente o monitor pode remarcar a sessão");
		Validate.validState(!sessao.getCancelada(), "Sessão cancelada não pode ser remarcada");
		var resposta =
				sessao.getRespostas().stream()
						.filter(r -> r.getAluno().equals(alunoId))
						.findFirst()
						.orElseThrow();
		Validate.isTrue(
				resposta.getSituacao() == SituacaoRespostaSessao.NAO_VAI_PARTICIPAR,
				"O aluno não informou que não poderá participar");
		LocalDateTime novoInicio = resposta.getInicioSugerido().orElseThrow();
		LocalDateTime novoFim = novoInicio.plus(java.time.Duration.between(sessao.getInicio(), sessao.getFim()));
		Validate.isTrue(novoInicio.isAfter(relogio.agora()), "A sugestão precisa estar no futuro");
		validarConflitos(sessao, novoInicio, novoFim, Optional.of(id));
		var remarcada = sessao.remarcar(novoInicio, novoFim);
		repositorio.alterar(remarcada);
		notificarParticipantes(remarcada, "O horário da sessão foi remarcado.");
		return remarcada;
	}

	private void validarConflitos(
			Sessao sessao,
			LocalDateTime inicio,
			LocalDateTime fim,
			Optional<SessaoIdentificador> ignorada) {
		for (UsuarioIdentificador aluno : sessao.getAlunos()) {
			Validate.validState(
					!repositorio.conflita(sessao.getMonitor(), aluno, inicio, fim, ignorada),
					"Conflito na agenda");
		}
	}

	private void notificarParticipantes(Sessao sessao, String mensagem) {
		for (UsuarioIdentificador alunoId : sessao.getAlunos()) {
			notificar(usuarioRepositorio.buscarPorIdentificador(alunoId), sessao, mensagem);
		}
	}

	private void notificar(Usuario usuario, Sessao sessao, String mensagem) {
		enviadorEmail.enviar(
				usuario.getEmailInstitucional().toString(),
				ASSUNTO_SESSAO,
				"Disciplina: "
						+ sessao.getDisciplina()
						+ "\nInício: "
						+ sessao.getInicio()
						+ "\nFim: "
						+ sessao.getFim()
						+ "\n"
						+ mensagem);
	}

	public void cancelar(SessaoIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), SessaoAutorizacoes::getCancelar);
		validarExistencia(id);
		var sessao = repositorio.buscarPorIdentificador(id);
		repositorio.bloquearAgenda(sessao.getMonitor());
		repositorio.alterar(sessao.cancelar());
	}

}
