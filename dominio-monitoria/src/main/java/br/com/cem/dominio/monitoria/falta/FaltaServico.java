package br.com.cem.dominio.monitoria.falta;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.alerta.Alerta;
import br.com.cem.dominio.monitoria.alerta.spi.IAlertaRepositorio;
import br.com.cem.dominio.monitoria.alerta.spi.ICoordenadoresAlertaServico;
import br.com.cem.dominio.monitoria.disponibilidade.Disponibilidade;
import br.com.cem.dominio.monitoria.disponibilidade.spi.IDisponibilidadeRepositorio;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import org.apache.commons.lang3.Validate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class FaltaServico
		extends AgregadoServicoAbstrato<FaltaIdentificador, Falta, FaltaAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO =
			"Não existe falta com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO =
			"Já existe falta com o identificador %s";
	public static final String FALTA_EXISTENTE_MODELO = "A pessoa %s já tem falta nesta sessão";
	public static final int LIMITE_FALTAS = 3;
	public static final int PRAZO_SUGESTOES_DIAS = 14;
	public static final int QUANTIDADE_SUGESTOES = 3;
	private final IFaltaRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IRelogioServico relogio;
	private final ISessaoRepositorio sessoes;
	private final IDisponibilidadeRepositorio disponibilidades;
	private final ICampusRepositorio campusRepositorio;
	private final IAlertaRepositorio alertas;
	private final IEnviadorEmailServico emailServico;
	private final ICoordenadoresAlertaServico coordenadores;

	public FaltaServico(
			ISegurancaServico seguranca,
			IFaltaRepositorio repositorio,
			IUsuarioRepositorio usuarioRepositorio,
			IRelogioServico relogio,
			ISessaoRepositorio sessoes,
			IDisponibilidadeRepositorio disponibilidades,
			ICampusRepositorio campusRepositorio,
			IAlertaRepositorio alertas,
			IEnviadorEmailServico emailServico,
			ICoordenadoresAlertaServico coordenadores) {
		super(
				seguranca,
				repositorio,
				IDENTIFICADOR_EXISTENTE_MODELO,
				IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.relogio = Objects.requireNonNull(relogio);
		this.sessoes = Objects.requireNonNull(sessoes);
		this.disponibilidades = Objects.requireNonNull(disponibilidades);
		this.campusRepositorio = Objects.requireNonNull(campusRepositorio);
		this.alertas = Objects.requireNonNull(alertas);
		this.emailServico = Objects.requireNonNull(emailServico);
		this.coordenadores = Objects.requireNonNull(coordenadores);
	}

	@Override
	public FaltaAutorizacoes criarAutorizacoes(Optional<FaltaIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		boolean permitido =
				papel(Autorizacao.ALUNO)
						|| papel(Autorizacao.PROFESSOR)
						|| papel(Autorizacao.COORDENADOR);
		var falta =
				identificador
						.filter(repositorio::existePorIdentificador)
						.map(repositorio::buscarPorIdentificador);
		boolean gerenciar = permitido && falta.map(this::gerencia).orElse(true);
		boolean consultar = permitido && falta.map(this::podeVer).orElse(true);
		boolean participante = papel(Autorizacao.ALUNO) || papel(Autorizacao.PROFESSOR);
		boolean reposicao = falta.map(Falta::deAluno).orElse(true);
		boolean agendar =
				participante
						&& falta.map(
										a ->
												sessoes.buscarPorIdentificador(a.getSessao())
														.gerenciadaPor(usuarioAtual()))
								.orElse(true);
		return new FaltaAutorizacoes(
				false,
				consultar,
				false,
				gerenciar,
				participante,
				gerenciar,
				consultar && reposicao,
				agendar && reposicao);
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

	private void bloquearPessoas(UsuarioIdentificador monitor, UsuarioIdentificador aluno) {
		java.util.stream.Stream.of(monitor, aluno)
				.distinct()
				.sorted(Comparator.comparing(UsuarioIdentificador::toString))
				.forEach(sessoes::bloquearAgenda);
	}

	private boolean gerencia(Falta a) {
		var s = sessoes.buscarPorIdentificador(a.getSessao());
		return a.getAutor().equals(usuarioAtual()) || s.getProfessor().equals(usuarioAtual());
	}

	private boolean podeVer(Falta a) {
		var s = sessoes.buscarPorIdentificador(a.getSessao());
		return papel(Autorizacao.COORDENADOR)
				|| s.getMonitor().equals(usuarioAtual())
				|| s.getProfessor().equals(usuarioAtual())
				|| a.getAutor().equals(usuarioAtual())
				|| a.getPapel().equals("MONITOR") && s.getAlunos().contains(usuarioAtual());
	}

	public Falta registrar(
			SessaoIdentificador sessao,
			UsuarioIdentificador pessoa,
			String papel,
			Optional<String> observacao) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(pessoa);
		Objects.requireNonNull(papel);
		Objects.requireNonNull(observacao);
		validarAutorizacao(Optional.empty(), FaltaAutorizacoes::getRegistrar);
		var s = sessoes.buscarPorIdentificador(sessao);
		var atual = usuarioAtual();
		boolean gestor = s.gerenciadaPor(atual);
		boolean relato = papel.equals("MONITOR") && s.podeInformarAusencia(atual, relogio.agora());
		if (!(gestor || relato))
			throw new NaoAutorizadoExcecao("Sem permissão para registrar falta nesta sessão");
		s.validarFalta(pessoa, papel, relogio.agora());
		bloquearPessoas(s.getMonitor(), pessoa);
		validarInexistencia(
				x -> repositorio.existePorSessaoEPessoa(sessao, x), pessoa, FALTA_EXISTENTE_MODELO);
		var falta =
				repositorio.incluir(
						new Falta(
								Optional.empty(),
								sessao,
								pessoa,
								papel,
								atual,
								observacao,
								relogio.agora(),
								Optional.empty()));
		var totalFaltas = repositorio.contar(pessoa, papel, s.getDisciplina());
		if (totalFaltas >= LIMITE_FALTAS
				&& !alertas.existeNovo(pessoa, papel, s.getDisciplina())) {
			alertas.incluir(
					new Alerta(
							Optional.empty(),
							pessoa,
							papel,
							s.getDisciplina(),
							false,
							relogio.agora()));
			var responsaveis = coordenadores.buscarPorPessoa(pessoa);
			if (!responsaveis.isEmpty()) {
				var faltoso = usuarioRepositorio.buscarPorIdentificador(pessoa);
				for (var coordenador : responsaveis) {
					emailServico.enviar(
							coordenador.getEmailInstitucional().toString(),
							"Alerta de recorrência de faltas",
							"O " + (papel.equals("MONITOR") ? "monitor " : "aluno ")
									+ faltoso.getNome() + " atingiu "
									+ totalFaltas
									+ " faltas na disciplina " + s.getDisciplina()
									+ ". Consulte o alerta no painel de acompanhamento de faltas do CEM.");
				}
			}
		}
		return falta;
	}

	public void editar(FaltaIdentificador id, Optional<String> observacao) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), FaltaAutorizacoes::getEditar);
		validarExistencia(id);
		var a = repositorio.buscarPorIdentificador(id);
		repositorio.alterar(a.editar(observacao));
	}

	@Override
	protected void validarExclusao(FaltaIdentificador id) {
		Objects.requireNonNull(id);
		var a = repositorio.buscarPorIdentificador(id);
		sessoes.bloquearAgenda(sessoes.buscarPorIdentificador(a.getSessao()).getMonitor());
		Validate.validState(
				!possuiReposicaoAtiva(a), "Cancele a reposição antes de excluir a falta");
	}

	public List<SugestaoReposicao> sugerir(FaltaIdentificador id) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), FaltaAutorizacoes::getSugerir);
		validarExistencia(id);
		var a = repositorio.buscarPorIdentificador(id);
		var s = sessoes.buscarPorIdentificador(a.getSessao());
		Validate.validState(a.deAluno(), "Reposição disponível somente para falta de aluno");
		if (possuiReposicaoAtiva(a)) return List.of();
		var duracao = Duration.between(s.getInicio(), s.getFim());
		var resultado = new ArrayList<SugestaoReposicao>();
		var disponiveis =
				disponibilidades.buscarDoMonitor(s.getMonitor(), s.getDisciplina()).stream()
						.sorted(
								Comparator.comparing(Disponibilidade::getInicio)
										.thenComparing(d -> d.getCampus().toString()))
						.toList();
		for (int dia = 1;
				dia <= PRAZO_SUGESTOES_DIAS && resultado.size() < QUANTIDADE_SUGESTOES;
				dia++) {
			var data = relogio.agora().toLocalDate().plusDays(dia);
			for (var d : disponiveis) {
				if (resultado.size() == QUANTIDADE_SUGESTOES) break;
				var inicio = data.atTime(d.getInicio());
				var fim = inicio.plus(duracao);
				if (data.getDayOfWeek().getValue() == d.getDiaSemana()
						&& !fim.isAfter(data.atTime(d.getFim()))
						&& campusRepositorio.buscarPorIdentificador(d.getCampus()).estaAtivo()
						&& !sessoes.conflita(s.getMonitor(), a.getPessoa(), inicio, fim)) {
					var sugestao = new SugestaoReposicao(inicio, fim, d.getCampus());
					if (!resultado.contains(sugestao)) resultado.add(sugestao);
				}
			}
		}
		return List.copyOf(resultado);
	}

	public Sessao agendar(FaltaIdentificador id, SugestaoReposicao escolha) {
		Objects.requireNonNull(id);
		validarAutorizacao(Optional.of(id), FaltaAutorizacoes::getAgendar);
		validarExistencia(id);
		Objects.requireNonNull(escolha);
		var a = repositorio.buscarPorIdentificador(id);
		var s = sessoes.buscarPorIdentificador(a.getSessao());
		bloquearPessoas(s.getMonitor(), a.getPessoa());
		Validate.validState(sugerir(id).contains(escolha), "Horário não está mais disponível");
		var reposicao =
				sessoes.incluir(
						new Sessao(
								Optional.empty(),
								s.getDisciplina(),
								s.getProfessor(),
								s.getMonitor(),
								escolha.campus(),
								List.of(a.getPessoa()),
								escolha.inicio(),
								escolha.fim(),
								false));
		repositorio.alterar(a.agendarReposicao(reposicao.getIdentificador().orElseThrow()));
		for (var pessoa : List.of(s.getMonitor(), a.getPessoa())) {
			var u = usuarioRepositorio.buscarPorIdentificador(pessoa);
			emailServico.enviar(
					u.getEmailInstitucional().toString(),
					"Reposição agendada",
					"Reposição de " + s.getDisciplina() + " em " + escolha.inicio());
		}
		return reposicao;
	}

	private boolean possuiReposicaoAtiva(Falta falta) {
		return falta.getReposicao()
				.map(sessoes::buscarPorIdentificador)
				.map(sessao -> !sessao.getCancelada())
				.orElse(false);
	}
}
