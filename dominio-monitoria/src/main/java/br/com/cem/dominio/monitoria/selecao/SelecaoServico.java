package br.com.cem.dominio.monitoria.selecao;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.selecao.spi.ISelecaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class SelecaoServico
		extends AgregadoServicoAbstrato<SelecaoIdentificador, Selecao, SelecaoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe seleção com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe seleção com o identificador %s";

	private final ISelecaoRepositorio repositorio;
	private final ICandidaturaRepositorio candidaturaRepositorio;
	private final IDisciplinaRepositorio disciplinaRepositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;

	public SelecaoServico(
			ISegurancaServico seguranca,
			ISelecaoRepositorio repositorio,
			ICandidaturaRepositorio candidaturaRepositorio,
			IDisciplinaRepositorio disciplinaRepositorio,
			IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogio) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.candidaturaRepositorio = Objects.requireNonNull(candidaturaRepositorio);
		this.disciplinaRepositorio = Objects.requireNonNull(disciplinaRepositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public SelecaoAutorizacoes criarAutorizacoes(Optional<SelecaoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean professor = papel(Autorizacao.PROFESSOR);
		boolean aluno = papel(Autorizacao.ALUNO);
		boolean coordenador = papel(Autorizacao.COORDENADOR);
		if (identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())) {
			return new SelecaoAutorizacoes(
					false,
					professor || aluno || coordenador,
					false,
					false,
					professor,
					professor,
					professor);
		}
		var selecao = repositorio.buscarPorIdentificador(identificador.get());
		boolean responsavel =
				professor && professorResponsavel(selecao).equals(usuarioAtual());
		boolean aberta = !selecao.encerrada();
		return new SelecaoAutorizacoes(
				false,
				responsavel || aluno || coordenador,
				false,
				false,
				professor,
				responsavel && aberta,
				responsavel && aberta);
	}

	public Selecao abrir(
			DisciplinaIdentificador disciplinaId,
			LocalDate prazoInscricao,
			int vagas,
			double crMinimo,
			int pesoCr,
			int pesoEntrevista,
			double patamarMinimo) {
		Objects.requireNonNull(disciplinaId);
		Objects.requireNonNull(prazoInscricao);
		validarAutorizacao(Optional.empty(), SelecaoAutorizacoes::getAbrir);
		validarExistencia(
				disciplinaRepositorio::existePorIdentificador,
				disciplinaId,
				DisciplinaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		var disciplina = disciplinaRepositorio.buscarPorIdentificador(disciplinaId);
		var atual = usuarioAtual();
		if (!disciplina.getProfessor().equals(atual)) {
			throw new NaoAutorizadoExcecao(
					"Somente o professor responsável pela disciplina pode abrir seleção");
		}
		Validate.validState(
				!repositorio.existeAbertaPorDisciplina(disciplinaId, Optional.empty()),
				"Já existe uma seleção aberta para esta disciplina");
		LocalDate hoje = relogio.agora().toLocalDate();
		Validate.isTrue(
				!prazoInscricao.isBefore(hoje),
				"O prazo de inscrição deve ser igual ou posterior à data atual");
		var selecao =
				new Selecao(
						Optional.empty(),
						disciplinaId,
						atual,
						Selecao.semestreAtual(hoje),
						prazoInscricao,
						vagas,
						crMinimo,
						pesoCr,
						pesoEntrevista,
						patamarMinimo,
						SelecaoSituacao.ABERTA,
						Optional.empty(),
						hoje,
						Optional.empty());
		return repositorio.incluir(selecao);
	}

	public Selecao editar(
			SelecaoIdentificador identificador,
			LocalDate prazoInscricao,
			int vagas,
			double crMinimo,
			int pesoCr,
			int pesoEntrevista,
			double patamarMinimo) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(prazoInscricao);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), SelecaoAutorizacoes::getEditar);
		var selecao = repositorio.buscarPorIdentificador(identificador);
		Validate.validState(!selecao.encerrada(), "Seleção encerrada não pode ser editada");
		Validate.validState(
				!candidaturaRepositorio.existePorSelecao(identificador),
				"Os critérios não podem ser alterados depois que a seleção recebe a primeira candidatura");
		LocalDate hoje = relogio.agora().toLocalDate();
		Validate.isTrue(
				!prazoInscricao.isBefore(hoje),
				"O prazo de inscrição deve ser igual ou posterior à data atual");
		var atualizada =
				selecao.atualizarCriterios(
						prazoInscricao,
						vagas,
						crMinimo,
						pesoCr,
						pesoEntrevista,
						patamarMinimo);
		repositorio.alterar(atualizada);
		return atualizada;
	}

	public Selecao encerrar(SelecaoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), SelecaoAutorizacoes::getEncerrar);
		var selecao = repositorio.buscarPorIdentificador(identificador);
		LocalDate hoje = relogio.agora().toLocalDate();
		var encerrada = selecao.encerrar("seleção encerrada", hoje);
		for (var candidatura : candidaturaRepositorio.listarPorSelecao(identificador)) {
			if (candidatura.getSituacao() == CandidaturaSituacao.PENDENTE) {
				var resultado =
						candidatura
								.getNotaEntrevista()
								.map(
										nota ->
												selecao.calcularResultadoFinal(
														candidatura.getCr(), nota));
				candidaturaRepositorio.alterar(
						candidatura.reprovarAutomaticamente(
								resultado,
								"Reprovada automaticamente: seleção encerrada",
								hoje));
			}
		}
		repositorio.alterar(encerrada);
		return encerrada;
	}

	private UsuarioIdentificador professorResponsavel(Selecao selecao) {
		if (disciplinaRepositorio.existePorIdentificador(selecao.getDisciplina())) {
			return disciplinaRepositorio.buscarPorIdentificador(selecao.getDisciplina()).getProfessor();
		}
		return selecao.getProfessor();
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
