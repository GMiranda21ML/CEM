package br.com.cem.dominio.monitoria.acompanhamento;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.spi.IFeedbackRepositorio;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AcompanhamentoServico
		extends AgregadoServicoAbstrato<
				AcompanhamentoIdentificador, Acompanhamento, AcompanhamentoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe acompanhamento com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe acompanhamento com o identificador %s";

	private final IAcompanhamentoRepositorio repositorio;
	private final ICandidaturaRepositorio candidaturaRepositorio;
	private final IFeedbackRepositorio feedbackRepositorio;
	private final ISessaoRepositorio sessaoRepositorio;
	private final IFaltaRepositorio faltaRepositorio;
	private final IDisciplinaRepositorio disciplinaRepositorio;
	private final ICursoRepositorio cursoRepositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;

	public AcompanhamentoServico(
			ISegurancaServico seguranca,
			IAcompanhamentoRepositorio repositorio,
			ICandidaturaRepositorio candidaturaRepositorio,
			IFeedbackRepositorio feedbackRepositorio,
			ISessaoRepositorio sessaoRepositorio,
			IFaltaRepositorio faltaRepositorio,
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
		this.candidaturaRepositorio = Objects.requireNonNull(candidaturaRepositorio);
		this.feedbackRepositorio = Objects.requireNonNull(feedbackRepositorio);
		this.sessaoRepositorio = Objects.requireNonNull(sessaoRepositorio);
		this.faltaRepositorio = Objects.requireNonNull(faltaRepositorio);
		this.disciplinaRepositorio = Objects.requireNonNull(disciplinaRepositorio);
		this.cursoRepositorio = Objects.requireNonNull(cursoRepositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
	}

	@Override
	public AcompanhamentoAutorizacoes criarAutorizacoes(
			Optional<AcompanhamentoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean aluno = papel(Autorizacao.ALUNO);
		boolean professor = papel(Autorizacao.PROFESSOR);
		boolean coordenador = papel(Autorizacao.COORDENADOR);
		boolean ehMonitor =
				aluno
						&& usuarioRepositorio.existePorEmail(
								new UsuarioEmail(getSegurancaServico().getUsuario()),
								Optional.empty())
						&& candidaturaRepositorio.ehMonitorEmAlgumaDisciplina(usuarioAtual());
		if (identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())) {
			return new AcompanhamentoAutorizacoes(
					false,
					aluno || professor || coordenador,
					false,
					ehMonitor || professor,
					ehMonitor,
					ehMonitor || professor,
					ehMonitor || professor,
					aluno || professor || coordenador);
		}
		var acomp = repositorio.buscarPorIdentificador(identificador.get());
		var atual = usuarioAtual();
		var profDisciplina = professorResponsavel(acomp.getDisciplina());
		boolean gerencia = acomp.podeGerenciar(atual, profDisciplina) && acomp.ativo();
		boolean consulta = acomp.podeConsultar(atual, profDisciplina, coordenador);
		return new AcompanhamentoAutorizacoes(
				false,
				consulta,
				false,
				gerencia,
				ehMonitor,
				gerencia,
				gerencia,
				consulta);
	}

	public Acompanhamento abrir(
			UsuarioIdentificador alunoId,
			DisciplinaIdentificador disciplinaId,
			double metaNota,
			int metaFrequencia) {
		Objects.requireNonNull(alunoId);
		Objects.requireNonNull(disciplinaId);
		validarAutorizacao(Optional.empty(), AcompanhamentoAutorizacoes::getAbrir);
		validarExistencia(
				disciplinaRepositorio::existePorIdentificador,
				disciplinaId,
				DisciplinaServico.IDENTIFICADOR_INEXISTENTE_MODELO);
		Validate.isTrue(
				usuarioRepositorio.existePorIdentificador(alunoId),
				"Não existe aluno com o identificador informado");
		var monitorId = usuarioAtual();
		if (!candidaturaRepositorio.ehMonitorAtivo(monitorId, disciplinaId)) {
			throw new NaoAutorizadoExcecao(
					"Somente aluno com vínculo de monitor ativo na disciplina pode abrir acompanhamento");
		}
		Validate.isTrue(
				!alunoId.equals(monitorId),
				"O monitor não pode abrir acompanhamento para si mesmo");
		var aluno = usuarioRepositorio.buscarPorIdentificador(alunoId);
		Validate.isTrue(
				aluno.getPapel() == Autorizacao.ALUNO && aluno.estaAtivo(),
				"O acompanhamento só pode ser aberto para aluno ativo");
		var disciplina = disciplinaRepositorio.buscarPorIdentificador(disciplinaId);
		var curso = cursoRepositorio.buscarPorIdentificador(disciplina.getCurso());
		var cursoAluno = aluno.getCurso();
		Validate.isTrue(
				cursoAluno != null
						&& (cursoAluno.trim().equalsIgnoreCase(curso.getNome().trim())
								|| cursoAluno.trim().equalsIgnoreCase(curso.getSigla().trim())),
				"O aluno deve pertencer ao curso da disciplina");
		Validate.validState(
				!repositorio.existeAtivoPorAlunoEDisciplina(alunoId, disciplinaId),
				"Este aluno já possui um acompanhamento ativo nesta disciplina");
		LocalDate hoje = relogio.agora().toLocalDate();
		var acompanhamento =
				new Acompanhamento(
						Optional.empty(),
						alunoId,
						disciplinaId,
						monitorId,
						Selecao.semestreAtual(hoje),
						metaNota,
						metaFrequencia,
						AcompanhamentoSituacao.ATIVO,
						hoje,
						Optional.empty(),
						Optional.empty());
		return repositorio.incluir(acompanhamento);
	}

	public Acompanhamento editarMetas(
			AcompanhamentoIdentificador identificador, double metaNota, int metaFrequencia) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		validarAutorizacao(
				Optional.of(identificador), AcompanhamentoAutorizacoes::getEditarMetas);
		var acomp = repositorio.buscarPorIdentificador(identificador);
		var atualizado = acomp.alterarMetas(metaNota, metaFrequencia);
		repositorio.alterar(atualizado);
		return atualizado;
	}

	public Acompanhamento encerrar(
			AcompanhamentoIdentificador identificador, String parecerFinal) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(parecerFinal);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), AcompanhamentoAutorizacoes::getEncerrar);
		var acomp = repositorio.buscarPorIdentificador(identificador);
		var encerrado = acomp.encerrar(parecerFinal, relogio.agora().toLocalDate());
		repositorio.alterar(encerrado);
		return encerrado;
	}

	@Override
	protected void validarExclusao(AcompanhamentoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		var acomp = repositorio.buscarPorIdentificador(identificador);
		Validate.validState(acomp.ativo(), "Somente acompanhamentos ativos podem ser excluídos");
		Validate.validState(
				!feedbackRepositorio.existePorAlunoEDisciplina(
						acomp.getAluno(), acomp.getDisciplina()),
				"O aluno já possui feedback registrado nesta disciplina; utilize o encerramento");
	}

	public RelatorioDesempenho emitirRelatorio(
			AcompanhamentoIdentificador identificador,
			Optional<LocalDate> de,
			Optional<LocalDate> ate) {
		Objects.requireNonNull(identificador);
		Objects.requireNonNull(de);
		Objects.requireNonNull(ate);
		validarExistencia(identificador);
		validarAutorizacao(Optional.of(identificador), AcompanhamentoAutorizacoes::getRelatorio);
		var acomp = repositorio.buscarPorIdentificador(identificador);
		var disciplina = disciplinaRepositorio.buscarPorIdentificador(acomp.getDisciplina());
		Collection<Sessao> sessoes = listarSessoesDisciplina(disciplina);
		Collection<Falta> faltas = listarFaltasDisciplina(disciplina);
		Collection<Feedback> feedbacks =
				feedbackRepositorio.listarPorAlunoEDisciplina(
						acomp.getAluno(), acomp.getDisciplina());
		return RelatorioDesempenho.calcular(
				acomp, sessoes, faltas, feedbacks, de, ate, relogio.agora());
	}

	private Collection<Sessao> listarSessoesDisciplina(Disciplina disciplina) {
		Map<SessaoIdentificador, Sessao> mapa = new LinkedHashMap<>();
		for (Sessao s : sessaoRepositorio.listarPorDisciplina(disciplina.getNome())) {
			s.getIdentificador().ifPresent(id -> mapa.put(id, s));
		}
		for (Sessao s :
				sessaoRepositorio.listarPorDisciplina(disciplina.getCodigo().toString())) {
			s.getIdentificador().ifPresent(id -> mapa.put(id, s));
		}
		return mapa.values();
	}

	private Collection<Falta> listarFaltasDisciplina(Disciplina disciplina) {
		Map<FaltaIdentificador, Falta> mapa = new LinkedHashMap<>();
		for (Falta f : faltaRepositorio.listarPorDisciplina(disciplina.getNome())) {
			f.getIdentificador().ifPresent(id -> mapa.put(id, f));
		}
		for (Falta f :
				faltaRepositorio.listarPorDisciplina(disciplina.getCodigo().toString())) {
			f.getIdentificador().ifPresent(id -> mapa.put(id, f));
		}
		return mapa.values();
	}

	private UsuarioIdentificador professorResponsavel(DisciplinaIdentificador disciplinaId) {
		return disciplinaRepositorio.buscarPorIdentificador(disciplinaId).getProfessor();
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
