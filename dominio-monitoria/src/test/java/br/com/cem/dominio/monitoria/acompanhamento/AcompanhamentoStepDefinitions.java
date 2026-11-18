package br.com.cem.dominio.monitoria.acompanhamento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.falta.FaltaIdentificador;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.feedback.spi.IFeedbackRepositorio;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AcompanhamentoStepDefinitions {
	private static final UsuarioIdentificador MONITOR_ID = new UsuarioIdentificador("30");
	private static final UsuarioIdentificador ALUNO_ID = new UsuarioIdentificador("31");
	private static final UsuarioIdentificador OUTRO_ALUNO_ID = new UsuarioIdentificador("32");
	private static final UsuarioIdentificador PROFESSOR_ID = new UsuarioIdentificador("33");
	private static final CursoIdentificador CURSO_ID = new CursoIdentificador("1");
	private static final DisciplinaIdentificador DISCIPLINA_ID = new DisciplinaIdentificador("1");
	private static final CampusIdentificador CAMPUS_ID = new CampusIdentificador("1");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 20, 14, 0);

	private final Map<String, Acompanhamento> acompanhamentos = new LinkedHashMap<>();
	private final List<Sessao> sessoes = new ArrayList<>();
	private final List<Falta> faltas = new ArrayList<>();
	private final List<Feedback> feedbacks = new ArrayList<>();

	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IAcompanhamentoRepositorio repositorio = mock(IAcompanhamentoRepositorio.class);
	private final ICandidaturaRepositorio candidaturaRepositorio =
			mock(ICandidaturaRepositorio.class);
	private final IFeedbackRepositorio feedbackRepositorio = mock(IFeedbackRepositorio.class);
	private final ISessaoRepositorio sessaoRepositorio = mock(ISessaoRepositorio.class);
	private final IFaltaRepositorio faltaRepositorio = mock(IFaltaRepositorio.class);
	private final IDisciplinaRepositorio disciplinaRepositorio = mock(IDisciplinaRepositorio.class);
	private final ICursoRepositorio cursoRepositorio = mock(ICursoRepositorio.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);

	private final AcompanhamentoServico servico =
			new AcompanhamentoServico(
					seguranca,
					repositorio,
					candidaturaRepositorio,
					feedbackRepositorio,
					sessaoRepositorio,
					faltaRepositorio,
					disciplinaRepositorio,
					cursoRepositorio,
					usuarioRepositorio,
					relogio);

	private int proximoId = 1;
	private UsuarioIdentificador usuarioAtual = MONITOR_ID;
	private Acompanhamento resultado;
	private RelatorioDesempenho relatorio;
	private RuntimeException excecaoCapturada;
	private RuntimeException segundaExcecao;

	public AcompanhamentoStepDefinitions() {
		when(relogio.agora()).thenReturn(AGORA);
		when(seguranca.getUsuario()).thenAnswer(c -> emailDe(usuarioAtual));
		when(seguranca.possuiAutorizacao(anyString()))
				.thenAnswer(
						c -> {
							String role = c.getArgument(0);
							return usuarioAtual.equals(PROFESSOR_ID)
									? role.equals(Autorizacao.PROFESSOR.name())
									: role.equals(Autorizacao.ALUNO.name());
						});
		when(usuarioRepositorio.existePorIdentificador(any()))
				.thenAnswer(
						c ->
								List.of(MONITOR_ID, ALUNO_ID, OUTRO_ALUNO_ID, PROFESSOR_ID)
										.contains(c.getArgument(0)));
		when(usuarioRepositorio.existePorEmail(any(), any())).thenReturn(true);
		when(usuarioRepositorio.buscarPorEmail(any())).thenAnswer(c -> usuario(usuarioAtual));
		when(usuarioRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> usuario(c.getArgument(0)));
		when(disciplinaRepositorio.existePorIdentificador(DISCIPLINA_ID)).thenReturn(true);
		when(disciplinaRepositorio.buscarPorIdentificador(DISCIPLINA_ID))
				.thenReturn(
						new Disciplina(
								Optional.of(DISCIPLINA_ID),
								new InstituicaoIdentificador("cem.edu.br"),
								CURSO_ID,
								PROFESSOR_ID,
								"Cálculo I",
								new DisciplinaCodigo("MAT1001"),
								"Matemática"));
		when(cursoRepositorio.buscarPorIdentificador(CURSO_ID))
				.thenReturn(
						new Curso(
								Optional.of(CURSO_ID),
								new InstituicaoIdentificador("cem.edu.br"),
								"Computação",
								"CC"));
		when(candidaturaRepositorio.ehMonitorEmAlgumaDisciplina(any()))
				.thenAnswer(c -> c.getArgument(0).equals(MONITOR_ID));
		when(candidaturaRepositorio.ehMonitorAtivo(any(), any()))
				.thenAnswer(
						c ->
								c.getArgument(0).equals(MONITOR_ID)
										&& c.getArgument(1).equals(DISCIPLINA_ID));

		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(c -> acompanhamentos.containsKey(c.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> acompanhamentos.get(c.getArgument(0).toString()));
		when(repositorio.existeAtivoPorAlunoEDisciplina(any(), any()))
				.thenAnswer(
						c -> {
							UsuarioIdentificador aluno = c.getArgument(0);
							DisciplinaIdentificador disc = c.getArgument(1);
							return acompanhamentos.values().stream()
									.anyMatch(
											a ->
													a.getAluno().equals(aluno)
															&& a.getDisciplina().equals(disc)
															&& a.ativo());
						});
		when(repositorio.incluir(any()))
				.thenAnswer(
						c -> {
							Acompanhamento a = c.getArgument(0);
							var id =
									new AcompanhamentoIdentificador(String.valueOf(proximoId++));
							var salvo =
									new Acompanhamento(
											Optional.of(id),
											a.getAluno(),
											a.getDisciplina(),
											a.getMonitor(),
											a.getSemestre(),
											a.getMetaNota(),
											a.getMetaFrequencia(),
											a.getSituacao(),
											a.getAbertoEm(),
											a.getEncerradoEm(),
											a.getParecerFinal());
							acompanhamentos.put(id.toString(), salvo);
							return salvo;
						});
		doAnswer(
						c -> {
							Acompanhamento a = c.getArgument(0);
							acompanhamentos.put(
									a.getIdentificador().orElseThrow().toString(), a);
							return null;
						})
				.when(repositorio)
				.alterar(any());
		doAnswer(
						c -> {
							AcompanhamentoIdentificador id = c.getArgument(0);
							acompanhamentos.remove(id.toString());
							return null;
						})
				.when(repositorio)
				.excluir(any());

		when(feedbackRepositorio.existePorAlunoEDisciplina(any(), any()))
				.thenAnswer(
						c -> {
							UsuarioIdentificador aluno = c.getArgument(0);
							return feedbacks.stream().anyMatch(fb -> fb.getAluno().equals(aluno));
						});
		when(feedbackRepositorio.listarPorAlunoEDisciplina(any(), any()))
				.thenAnswer(
						c -> {
							UsuarioIdentificador aluno = c.getArgument(0);
							return feedbacks.stream()
									.filter(fb -> fb.getAluno().equals(aluno))
									.toList();
						});
		when(sessaoRepositorio.listarPorDisciplina("Cálculo I")).thenAnswer(c -> sessoes);
		when(faltaRepositorio.listarPorDisciplina("Cálculo I")).thenAnswer(c -> faltas);
	}

	private Usuario usuario(UsuarioIdentificador id) {
		return new Usuario(
				Optional.of(id),
				"Usuário " + id,
				new UsuarioEmail(emailDe(id)),
				new UsuarioSenha("Cem@2026!"),
				id.equals(PROFESSOR_ID) ? Autorizacao.PROFESSOR : Autorizacao.ALUNO,
				"Computação",
				null,
				UsuarioSituacao.ATIVO);
	}

	private String emailDe(UsuarioIdentificador id) {
		return "u" + id + "@cem.edu.br";
	}

	@Dado("um monitor ativo e um aluno do mesmo curso da disciplina")
	public void preparar() {
		acompanhamentos.clear();
		sessoes.clear();
		faltas.clear();
		feedbacks.clear();
		proximoId = 1;
		usuarioAtual = MONITOR_ID;
		resultado = null;
		relatorio = null;
		excecaoCapturada = null;
		segundaExcecao = null;
	}

	@Quando("o monitor abre o acompanhamento definindo metas de nota e frequência")
	public void abrirValido() {
		resultado = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.5, 75);
	}

	@Então("o acompanhamento é registrado como ativo")
	public void verificarAtivo() {
		assertNull(excecaoCapturada);
		assertEquals(AcompanhamentoSituacao.ATIVO, resultado.getSituacao());
	}

	@Quando("um aluno sem vínculo de monitor ativo na disciplina tenta abrir acompanhamento")
	public void abrirSemVinculo() {
		usuarioAtual = OUTRO_ALUNO_ID;
		try {
			servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Então("a operação do acompanhamento é recusada")
	public void verificarRecusa() {
		assertNotNull(excecaoCapturada);
	}

	@Quando("o monitor tenta abrir acompanhamento para si mesmo")
	public void abrirParaSiMesmo() {
		try {
			servico.abrir(MONITOR_ID, DISCIPLINA_ID, 7.0, 75);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o monitor tenta abrir um segundo acompanhamento ativo para o mesmo aluno na disciplina")
	public void abrirDuplicado() {
		servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		try {
			servico.abrir(ALUNO_ID, DISCIPLINA_ID, 8.0, 80);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o monitor tenta abrir acompanhamento com meta de frequência menor que cinquenta por cento")
	public void abrirMetaInvalida() {
		try {
			servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 40);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor da disciplina altera as metas do acompanhamento ativo")
	public void professorEditaMetas() {
		var a = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		usuarioAtual = PROFESSOR_ID;
		resultado = servico.editarMetas(a.getIdentificador().orElseThrow(), 8.5, 85);
	}

	@Então("as novas metas do acompanhamento são atualizadas")
	public void verificarMetasAtualizadas() {
		assertEquals(8.5, resultado.getMetaNota());
		assertEquals(85, resultado.getMetaFrequencia());
	}

	@Quando("o monitor tenta encerrar o acompanhamento com parecer curto e depois com parecer válido")
	public void testarEncerramento() {
		var a = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		var id = a.getIdentificador().orElseThrow();
		try {
			servico.encerrar(id, "Muito curto");
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
		resultado =
				servico.encerrar(
						id,
						"Aluno atingiu as metas definidas e evoluiu de forma consistente.");
	}

	@Então("o parecer curto é recusado e o parecer válido encerra o acompanhamento")
	public void verificarEncerramento() {
		assertNotNull(excecaoCapturada);
		assertEquals(AcompanhamentoSituacao.ENCERRADO, resultado.getSituacao());
	}

	@Quando("o monitor tenta excluir acompanhamento com feedback e depois exclui acompanhamento sem feedback")
	public void testarExclusao() {
		var comFeedback = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		feedbacks.add(
				new Feedback(
						Optional.of(new FeedbackIdentificador("1")),
						new SessaoIdentificador("1"),
						ALUNO_ID,
						MONITOR_ID,
						8.0,
						Optional.empty(),
						AGORA.toLocalDate(),
						Optional.empty()));
		try {
			servico.excluir(comFeedback.getIdentificador().orElseThrow());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}

		var semFeedback = servico.abrir(OUTRO_ALUNO_ID, DISCIPLINA_ID, 7.0, 75);
		var semFeedbackId = semFeedback.getIdentificador().orElseThrow();
		try {
			servico.excluir(semFeedbackId);
		} catch (RuntimeException e) {
			segundaExcecao = e;
		}
		resultado = semFeedback;
	}

	@Então("a exclusão com feedback é recusada e a sem feedback é concluída")
	public void verificarExclusao() {
		assertNotNull(excecaoCapturada);
		assertNull(segundaExcecao);
		assertFalse(
				acompanhamentos.containsKey(resultado.getIdentificador().orElseThrow().toString()));
	}

	@Quando("o relatório é emitido com sessões realizadas, falta reposta e feedbacks crescentes")
	public void emitirRelatorioCompleto() {
		var a = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.5, 75);
		var s1 = new SessaoIdentificador("1");
		var s2 = new SessaoIdentificador("2");
		var s3 = new SessaoIdentificador("3");
		var s4 = new SessaoIdentificador("4");
		var s5 = new SessaoIdentificador("5");
		var s6 = new SessaoIdentificador("6");

		sessoes.add(sessao(s1, AGORA.minusDays(20), false));
		sessoes.add(sessao(s2, AGORA.minusDays(15), false));
		sessoes.add(sessao(s3, AGORA.minusDays(12), false));
		sessoes.add(sessao(s4, AGORA.minusDays(10), false));
		sessoes.add(sessao(s5, AGORA.minusDays(5), false));
		sessoes.add(sessao(s6, AGORA.minusDays(2), true));

		faltas.add(
				new Falta(
						Optional.of(new FaltaIdentificador("1")),
						s2,
						ALUNO_ID,
						"ALUNO",
						MONITOR_ID,
						Optional.empty(),
						AGORA.minusDays(15),
						Optional.of(s3)));

		feedbacks.add(feedback("1", s1, 6.0));
		feedbacks.add(feedback("2", s3, 7.0));
		feedbacks.add(feedback("3", s4, 8.0));
		feedbacks.add(feedback("4", s5, 9.0));

		relatorio =
				servico.emitirRelatorio(
						a.getIdentificador().orElseThrow(), Optional.empty(), Optional.empty());
	}

	private Sessao sessao(SessaoIdentificador id, LocalDateTime inicio, boolean cancelada) {
		return Sessao.restaurar(
				Optional.of(id),
				"Cálculo I",
				PROFESSOR_ID,
				MONITOR_ID,
				CAMPUS_ID,
				"Sala 1",
				List.of(ALUNO_ID),
				List.of(),
				inicio,
				inicio.plusHours(1),
				Optional.empty(),
				cancelada);
	}

	private Feedback feedback(String id, SessaoIdentificador sid, double nota) {
		return new Feedback(
				Optional.of(new FeedbackIdentificador(id)),
				sid,
				ALUNO_ID,
				MONITOR_ID,
				nota,
				Optional.empty(),
				AGORA.toLocalDate(),
				Optional.empty());
	}

	@Então("o relatório calcula corretamente a frequência, nota inicial, nota atual, tendência em evolução e situação meta atingida")
	public void verificarRelatorio() {
		assertEquals(4, relatorio.totalSessoesValidas());
		assertEquals(4, relatorio.totalPresencas());
		assertEquals(100.0, relatorio.frequenciaPercentual().orElseThrow(), 1e-6);
		assertEquals(6.5, relatorio.notaInicial().orElseThrow(), 1e-6);
		assertEquals(8.5, relatorio.notaAtual().orElseThrow(), 1e-6);
		assertEquals(2.0, relatorio.variacaoNotas().orElseThrow(), 1e-6);
		assertEquals(TendenciaEvolucao.EM_EVOLUCAO, relatorio.tendencia());
		assertEquals(SituacaoDesempenho.META_ATINGIDA, relatorio.situacao());
	}

	@Quando("um aluno não vinculado tenta consultar o relatório de outro aluno")
	public void alunoNaoVinculadoConsultaRelatorio() {
		var a = servico.abrir(ALUNO_ID, DISCIPLINA_ID, 7.5, 75);
		usuarioAtual = OUTRO_ALUNO_ID;
		try {
			servico.emitirRelatorio(
					a.getIdentificador().orElseThrow(), Optional.empty(), Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Então("o acesso ao relatório é recusado")
	public void verificarAcessoRecusado() {
		assertNotNull(excecaoCapturada);
	}
}
