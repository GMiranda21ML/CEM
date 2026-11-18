package br.com.cem.dominio.monitoria.candidatura;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoServico;
import br.com.cem.dominio.monitoria.selecao.spi.ISelecaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class CandidaturaServico
		extends AgregadoServicoAbstrato<
				CandidaturaIdentificador, Candidatura, CandidaturaAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe candidatura com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe candidatura com o identificador %s";
	public static final int LIMITE_MONITORIAS_SEMESTRE = 2;

	private final ICandidaturaRepositorio repositorio;
	private final ISelecaoRepositorio selecaoRepositorio;
	private final IDisciplinaRepositorio disciplinaRepositorio;
	private final ICursoRepositorio cursoRepositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;

	public CandidaturaServico(
			ISegurancaServico seguranca,
			ICandidaturaRepositorio repositorio,
			ISelecaoRepositorio selecaoRepositorio,
			IDisciplinaRepositorio disciplinaRepositorio,
			ICursoRepositorio cursoRepositorio,
			IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.selecaoRepositorio = Objects.requireNonNull(selecaoRepositorio);
		this.disciplinaRepositorio = Objects.requireNonNull(disciplinaRepositorio);
		this.cursoRepositorio = Objects.requireNonNull(cursoRepositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public CandidaturaAutorizacoes criarAutorizacoes(
			Optional<CandidaturaIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean aluno = papel(Autorizacao.ALUNO);
		boolean professor = papel(Autorizacao.PROFESSOR);
		if (identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())) {
			return new CandidaturaAutorizacoes(
					false,
					aluno || professor,
					false,
					false,
					aluno,
					aluno,
					professor,
					professor,
					professor);
		}
		var candidatura = repositorio.buscarPorIdentificador(identificador.get());
		var atual = usuarioAtual();
		boolean dono = aluno && candidatura.getAluno().equals(atual);
		boolean responsavel =
				professor && professorResponsavel(candidatura.getDisciplina()).equals(atual);
		boolean pendente = candidatura.getSituacao() == CandidaturaSituacao.PENDENTE;
		return new CandidaturaAutorizacoes(
				false,
				dono || responsavel,
				false,
				false,
				aluno,
				dono && pendente,
				responsavel && pendente,
				responsavel && pendente,
				responsavel && pendente);
	}

	public Candidatura candidatar(SelecaoIdentificador selecaoId, double cr) {
		Objects.requireNonNull(selecaoId);
		validarAutorizacao(Optional.empty(), CandidaturaAutorizacoes::getCandidatar);
		Validate.isTrue(Double.isFinite(cr) && cr >= 0.0 && cr <= 10.0, "CR deve estar entre 0 e 10");
		validarExistencia(
				selecaoRepositorio::existePorIdentificador,
				selecaoId,
				SelecaoServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		var selecao = selecaoRepositorio.buscarPorIdentificador(selecaoId);
		var aluno = usuarioEntityAtual();
		var alunoId = aluno.getIdentificador().orElseThrow();
		Validate.isTrue(
				aluno.getPapel() == Autorizacao.ALUNO && aluno.estaAtivo(),
				"Somente alunos ativos podem se candidatar");
		var disciplina = disciplinaRepositorio.buscarPorIdentificador(selecao.getDisciplina());
		var curso = cursoRepositorio.buscarPorIdentificador(disciplina.getCurso());
		var cursoAluno = aluno.getCurso();
		Validate.isTrue(
				cursoAluno != null
						&& (cursoAluno.trim().equalsIgnoreCase(curso.getNome().trim())
								|| cursoAluno.trim().equalsIgnoreCase(curso.getSigla().trim())),
				"A seleção pertence a outro curso");
		LocalDateTime agora = relogio.agora();
		LocalDate hoje = agora.toLocalDate();
		Validate.validState(
				selecao.inscricoesAbertas(hoje),
				"As inscrições para esta seleção não estão abertas");
		Validate.isTrue(
				cr >= selecao.getCrMinimo(),
				"O CR informado é inferior ao mínimo exigido pela seleção");
		Validate.validState(
				!repositorio.existeAtivaPorAlunoEDisciplina(alunoId, selecao.getDisciplina()),
				"Já existe candidatura pendente ou aprovada para esta disciplina");
		long ativas =
				repositorio.contarMonitoriasAtivasNoSemestre(
						alunoId, selecao.getSemestre(), selecao.getDisciplina());
		Validate.validState(
				ativas < LIMITE_MONITORIAS_SEMESTRE,
				"Limite de 2 monitorias simultâneas no semestre atingido");
		var candidatura =
				new Candidatura(
						Optional.empty(),
						selecaoId,
						selecao.getDisciplina(),
						alunoId,
						cr,
						CandidaturaSituacao.PENDENTE,
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						agora,
						Optional.empty());
		return repositorio.incluir(candidatura);
	}

	public Candidatura cancelar(CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), CandidaturaAutorizacoes::getCancelar);
		var candidatura = repositorio.buscarPorIdentificador(identificador);
		if (!candidatura.getAluno().equals(usuarioAtual())) {
			throw new NaoAutorizadoExcecao("Somente o próprio aluno pode cancelar sua candidatura");
		}
		var cancelada = candidatura.cancelar(relogio.agora().toLocalDate());
		repositorio.alterar(cancelada);
		return cancelada;
	}

	public Candidatura registrarEntrevista(
			CandidaturaIdentificador identificador, double nota, Optional<String> comentario) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(comentario);
		validarExistencia(identificador);
		validarAutorizacao(
				Optional.of(identificador), CandidaturaAutorizacoes::getRegistrarEntrevista);
		Validate.isTrue(
				Double.isFinite(nota) && nota >= 0.0 && nota <= 10.0,
				"A nota da entrevista deve estar entre 0 e 10");
		var candidatura = repositorio.buscarPorIdentificador(identificador);
		var selecao = selecaoRepositorio.buscarPorIdentificador(candidatura.getSelecao());
		Validate.validState(!selecao.encerrada(), "Seleção já encerrada");
		verificarProfessorResponsavel(candidatura);
		double resultado = selecao.calcularResultadoFinal(candidatura.getCr(), nota);
		var atualizada = candidatura.registrarEntrevista(nota, comentario, resultado);
		repositorio.alterar(atualizada);
		return atualizada;
	}

	public Candidatura aprovar(CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), CandidaturaAutorizacoes::getAprovar);
		var candidatura = repositorio.buscarPorIdentificador(identificador);
		verificarProfessorResponsavel(candidatura);
		var selecao = selecaoRepositorio.buscarPorIdentificador(candidatura.getSelecao());
		LocalDate hoje = relogio.agora().toLocalDate();
		Validate.validState(
				candidatura.getSituacao() == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser aprovadas");
		Validate.validState(!selecao.encerrada(), "Seleção já encerrada");
		Validate.validState(
				!selecao.inscricoesAbertas(hoje),
				"A aprovação só é liberada após o término do prazo de inscrição");
		double notaEntrevista =
				candidatura
						.getNotaEntrevista()
						.orElseThrow(
								() ->
										new IllegalStateException(
												"Registre a nota da entrevista antes de aprovar"));
		double resultado = selecao.calcularResultadoFinal(candidatura.getCr(), notaEntrevista);
		Validate.validState(
				resultado >= selecao.getPatamarMinimo(),
				"Resultado final abaixo do patamar mínimo");

		Collection<Candidatura> todas =
				repositorio.listarPorSelecao(selecao.getIdentificador().orElseThrow());
		long aprovadas =
				todas.stream()
						.filter(c -> c.getSituacao() == CandidaturaSituacao.APROVADA)
						.count();
		Validate.validState(
				aprovadas < selecao.getVagas(), "Todas as vagas da seleção já foram preenchidas");
		boolean pendenteSemEntrevista =
				todas.stream()
						.anyMatch(
								c ->
										c.getSituacao() == CandidaturaSituacao.PENDENTE
												&& c.getNotaEntrevista().isEmpty());
		Validate.validState(
				!pendenteSemEntrevista,
				"Há candidaturas pendentes sem nota de entrevista registrada");

		Candidatura candidatoComResultado =
				candidatura.registrarEntrevista(
						notaEntrevista, candidatura.getComentarioEntrevista(), resultado);
		boolean existeMelhorPendente =
				todas.stream()
						.filter(
								c ->
										c.getSituacao() == CandidaturaSituacao.PENDENTE
												&& !Objects.equals(
														c.getIdentificador(),
														candidatura.getIdentificador()))
						.anyMatch(
								outra -> {
									double outraNota = outra.getNotaEntrevista().orElseThrow();
									double outroResultado =
											selecao.calcularResultadoFinal(
													outra.getCr(), outraNota);
									Candidatura outraComResultado =
											outra.registrarEntrevista(
													outraNota,
													outra.getComentarioEntrevista(),
													outroResultado);
									return outroResultado >= selecao.getPatamarMinimo()
											&& Candidatura.comparadorClassificacao()
															.compare(
																	outraComResultado,
																	candidatoComResultado)
													< 0;
								});
		Validate.validState(
				!existeMelhorPendente,
				"Há candidatura mais bem classificada e acima do patamar ainda pendente");

		String motivo =
				String.format(
						Locale.forLanguageTag("pt-BR"),
						"Aprovada com resultado %.2f",
						resultado);
		var aprovada = candidatura.aprovar(resultado, hoje, motivo);
		repositorio.alterar(aprovada);

		if (aprovadas + 1 >= selecao.getVagas()) {
			selecaoRepositorio.alterar(selecao.encerrar("vagas preenchidas", hoje));
			for (var outra : todas) {
				if (outra.getSituacao() == CandidaturaSituacao.PENDENTE
						&& !Objects.equals(
								outra.getIdentificador(), candidatura.getIdentificador())) {
					var resOutra =
							outra.getNotaEntrevista()
									.map(
											n ->
													selecao.calcularResultadoFinal(
															outra.getCr(), n));
					repositorio.alterar(
							outra.reprovarAutomaticamente(
									resOutra,
									"Reprovada automaticamente: vagas preenchidas",
									hoje));
				}
			}
		}
		return aprovada;
	}

	public Candidatura reprovar(CandidaturaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), CandidaturaAutorizacoes::getReprovar);
		var candidatura = repositorio.buscarPorIdentificador(identificador);
		verificarProfessorResponsavel(candidatura);
		var selecao = selecaoRepositorio.buscarPorIdentificador(candidatura.getSelecao());
		LocalDate hoje = relogio.agora().toLocalDate();
		Validate.validState(
				candidatura.getSituacao() == CandidaturaSituacao.PENDENTE,
				"Somente candidaturas pendentes podem ser reprovadas");
		Validate.validState(!selecao.encerrada(), "Seleção já encerrada");
		Validate.validState(
				!selecao.inscricoesAbertas(hoje),
				"A reprovação só é liberada após o término do prazo de inscrição");
		double notaEntrevista =
				candidatura
						.getNotaEntrevista()
						.orElseThrow(
								() ->
										new IllegalStateException(
												"Registre a nota da entrevista antes de reprovar"));
		double resultado = selecao.calcularResultadoFinal(candidatura.getCr(), notaEntrevista);
		String motivo =
				String.format(
						Locale.forLanguageTag("pt-BR"),
						"Reprovada pelo professor · resultado %.2f",
						resultado);
		var reprovada = candidatura.reprovar(resultado, hoje, motivo);
		repositorio.alterar(reprovada);
		return reprovada;
	}

	private void verificarProfessorResponsavel(Candidatura candidatura) {
		if (!professorResponsavel(candidatura.getDisciplina()).equals(usuarioAtual())) {
			throw new NaoAutorizadoExcecao(
					"Somente o professor responsável pela disciplina pode avaliar esta candidatura");
		}
	}

	private UsuarioIdentificador professorResponsavel(DisciplinaIdentificador disciplinaId) {
		return disciplinaRepositorio.buscarPorIdentificador(disciplinaId).getProfessor();
	}

	private boolean papel(Autorizacao papel) {
		return getSegurancaServico().possuiAutorizacao(papel.name());
	}

	private Usuario usuarioEntityAtual() {
		return usuarioRepositorio.buscarPorEmail(
				new UsuarioEmail(getSegurancaServico().getUsuario()));
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioEntityAtual().getIdentificador().orElseThrow();
	}
}
