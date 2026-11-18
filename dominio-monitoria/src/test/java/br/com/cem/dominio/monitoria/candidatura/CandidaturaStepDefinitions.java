package br.com.cem.dominio.monitoria.candidatura;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
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
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.dominio.monitoria.selecao.spi.ISelecaoRepositorio;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CandidaturaStepDefinitions {
	private static final UsuarioIdentificador PROFESSOR_ID = new UsuarioIdentificador("10");
	private static final UsuarioIdentificador OUTRO_PROFESSOR_ID = new UsuarioIdentificador("11");
	private static final UsuarioIdentificador ALUNO_1 = new UsuarioIdentificador("20");
	private static final UsuarioIdentificador ALUNO_2 = new UsuarioIdentificador("21");
	private static final UsuarioIdentificador ALUNO_3 = new UsuarioIdentificador("22");
	private static final UsuarioIdentificador ALUNO_OUTRO_CURSO = new UsuarioIdentificador("23");
	private static final CursoIdentificador CURSO_ID = new CursoIdentificador("1");
	private static final DisciplinaIdentificador DISCIPLINA_ID = new DisciplinaIdentificador("1");
	private static final SelecaoIdentificador SELECAO_ID = new SelecaoIdentificador("1");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 10, 10, 0);

	private final Map<String, Selecao> selecoes = new LinkedHashMap<>();
	private final Map<String, Candidatura> candidaturas = new LinkedHashMap<>();
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final ICandidaturaRepositorio repositorio = mock(ICandidaturaRepositorio.class);
	private final ISelecaoRepositorio selecaoRepositorio = mock(ISelecaoRepositorio.class);
	private final IDisciplinaRepositorio disciplinaRepositorio = mock(IDisciplinaRepositorio.class);
	private final ICursoRepositorio cursoRepositorio = mock(ICursoRepositorio.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final CandidaturaServico servico =
			new CandidaturaServico(
					seguranca,
					repositorio,
					selecaoRepositorio,
					disciplinaRepositorio,
					cursoRepositorio,
					usuarioRepositorio,
					relogio);

	private int proximoId = 1;
	private long monitoriasSimultaneas = 0;
	private LocalDateTime agora = AGORA;
	private UsuarioIdentificador usuarioAtual = ALUNO_1;
	private Candidatura resultado;
	private Candidatura c1;
	private Candidatura c2;
	private Candidatura c3;
	private RuntimeException excecaoCapturada;

	public CandidaturaStepDefinitions() {
		when(relogio.agora()).thenAnswer(c -> agora);
		when(seguranca.getUsuario()).thenAnswer(c -> emailDe(usuarioAtual));
		when(seguranca.possuiAutorizacao(anyString()))
				.thenAnswer(
						c -> {
							String role = c.getArgument(0);
							boolean prof =
									usuarioAtual.equals(PROFESSOR_ID)
											|| usuarioAtual.equals(OUTRO_PROFESSOR_ID);
							return prof
									? role.equals(Autorizacao.PROFESSOR.name())
									: role.equals(Autorizacao.ALUNO.name());
						});
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
		when(selecaoRepositorio.existePorIdentificador(any()))
				.thenAnswer(c -> selecoes.containsKey(c.getArgument(0).toString()));
		when(selecaoRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> selecoes.get(c.getArgument(0).toString()));
		doAnswer(
						c -> {
							Selecao s = c.getArgument(0);
							selecoes.put(s.getIdentificador().orElseThrow().toString(), s);
							return null;
						})
				.when(selecaoRepositorio)
				.alterar(any());

		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(c -> candidaturas.containsKey(c.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> candidaturas.get(c.getArgument(0).toString()));
		when(repositorio.existeAtivaPorAlunoEDisciplina(any(), any()))
				.thenAnswer(
						c -> {
							UsuarioIdentificador aluno = c.getArgument(0);
							DisciplinaIdentificador disc = c.getArgument(1);
							return candidaturas.values().stream()
									.anyMatch(
											x ->
													x.getAluno().equals(aluno)
															&& x.getDisciplina().equals(disc)
															&& x.ativa());
						});
		when(repositorio.contarMonitoriasAtivasNoSemestre(any(), any(), any()))
				.thenAnswer(c -> monitoriasSimultaneas);
		when(repositorio.listarPorSelecao(any()))
				.thenAnswer(
						c -> {
							SelecaoIdentificador sid = c.getArgument(0);
							return candidaturas.values().stream()
									.filter(x -> x.getSelecao().equals(sid))
									.toList();
						});
		when(repositorio.incluir(any()))
				.thenAnswer(
						c -> {
							Candidatura cand = c.getArgument(0);
							var id = new CandidaturaIdentificador(String.valueOf(proximoId++));
							var salva =
									new Candidatura(
											Optional.of(id),
											cand.getSelecao(),
											cand.getDisciplina(),
											cand.getAluno(),
											cand.getCr(),
											cand.getSituacao(),
											cand.getNotaEntrevista(),
											cand.getComentarioEntrevista(),
											cand.getResultadoFinal(),
											cand.getMotivoDecisao(),
											cand.getCriadaEm(),
											cand.getDecididaEm());
							candidaturas.put(id.toString(), salva);
							return salva;
						});
		doAnswer(
						c -> {
							Candidatura cand = c.getArgument(0);
							candidaturas.put(
									cand.getIdentificador().orElseThrow().toString(), cand);
							return null;
						})
				.when(repositorio)
				.alterar(any());
	}

	private Usuario usuario(UsuarioIdentificador id) {
		boolean prof = id.equals(PROFESSOR_ID) || id.equals(OUTRO_PROFESSOR_ID);
		String curso = id.equals(ALUNO_OUTRO_CURSO) ? "Engenharia Civil" : "Computação";
		return new Usuario(
				Optional.of(id),
				"Usuário " + id,
				new UsuarioEmail(emailDe(id)),
				new UsuarioSenha("Cem@2026!"),
				prof ? Autorizacao.PROFESSOR : Autorizacao.ALUNO,
				curso,
				null,
				UsuarioSituacao.ATIVO);
	}

	private String emailDe(UsuarioIdentificador id) {
		return "u" + id + "@cem.edu.br";
	}

	@Dado("uma seleção de monitoria aberta pelo professor responsável")
	public void prepararSelecao() {
		selecoes.clear();
		candidaturas.clear();
		proximoId = 1;
		monitoriasSimultaneas = 0;
		agora = AGORA;
		usuarioAtual = ALUNO_1;
		resultado = null;
		excecaoCapturada = null;
		selecoes.put(
				SELECAO_ID.toString(),
				new Selecao(
						Optional.of(SELECAO_ID),
						DISCIPLINA_ID,
						PROFESSOR_ID,
						"2026.2",
						AGORA.toLocalDate().plusDays(10),
						2,
						7.0,
						60,
						40,
						7.5,
						SelecaoSituacao.ABERTA,
						Optional.empty(),
						AGORA.toLocalDate(),
						Optional.empty()));
	}

	@Quando("um aluno elegível se candidata informando CR dentro do mínimo")
	public void candidatarElegivel() {
		resultado = servico.candidatar(SELECAO_ID, 8.5);
	}

	@Então("a candidatura é registrada com a situação pendente")
	public void verificarPendente() {
		assertNull(excecaoCapturada);
		assertEquals(CandidaturaSituacao.PENDENTE, resultado.getSituacao());
	}

	@Quando("um aluno de outro curso tenta se candidatar à seleção")
	public void candidatarOutroCurso() {
		usuarioAtual = ALUNO_OUTRO_CURSO;
		try {
			servico.candidatar(SELECAO_ID, 8.5);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Então("a operação da candidatura é recusada")
	public void verificarRecusa() {
		assertNotNull(excecaoCapturada);
	}

	@Quando("um aluno informa CR inferior ao mínimo da seleção")
	public void candidatarCrBaixo() {
		try {
			servico.candidatar(SELECAO_ID, 6.5);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("um aluno com duas monitorias ativas no semestre tenta se candidatar")
	public void candidatarLimiteMonitorias() {
		monitoriasSimultaneas = 2;
		try {
			servico.candidatar(SELECAO_ID, 8.5);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("um aluno já com candidatura pendente ou aprovada na disciplina tenta se candidatar novamente")
	public void candidatarDuplicado() {
		servico.candidatar(SELECAO_ID, 8.5);
		try {
			servico.candidatar(SELECAO_ID, 9.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("um aluno tenta se candidatar após o término do prazo de inscrição")
	public void candidatarForaPrazo() {
		agora = AGORA.plusDays(11);
		try {
			servico.candidatar(SELECAO_ID, 8.5);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o aluno cancela sua candidatura pendente")
	public void cancelarPropriaPendente() {
		var c = servico.candidatar(SELECAO_ID, 8.5);
		resultado = servico.cancelar(c.getIdentificador().orElseThrow());
	}

	@Então("a candidatura fica com a situação cancelada")
	public void verificarCancelada() {
		assertEquals(CandidaturaSituacao.CANCELADA, resultado.getSituacao());
	}

	@Quando("o aluno tenta cancelar uma candidatura que não está pendente")
	public void cancelarNaoPendente() {
		var c = servico.candidatar(SELECAO_ID, 8.5);
		var id = c.getIdentificador().orElseThrow();
		servico.cancelar(id);
		try {
			servico.cancelar(id);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor responsável registra a nota da entrevista do candidato")
	public void registrarEntrevista() {
		var c = servico.candidatar(SELECAO_ID, 8.0);
		usuarioAtual = PROFESSOR_ID;
		resultado =
				servico.registrarEntrevista(
						c.getIdentificador().orElseThrow(), 9.0, Optional.of("Boa postura"));
	}

	@Então("a entrevista e o resultado final ponderado são gravados na candidatura")
	public void verificarEntrevista() {
		assertEquals(9.0, resultado.getNotaEntrevista().orElseThrow());
		assertEquals(8.4, resultado.getResultadoFinal().orElseThrow(), 1e-6);
	}

	@Quando("outro professor tenta registrar entrevista ou avaliar a candidatura")
	public void outroProfessorAvalia() {
		var c = servico.candidatar(SELECAO_ID, 8.0);
		usuarioAtual = OUTRO_PROFESSOR_ID;
		try {
			servico.registrarEntrevista(
					c.getIdentificador().orElseThrow(), 9.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta aprovar candidato antes do fim do prazo de inscrição")
	public void aprovarAntesPrazo() {
		var c = servico.candidatar(SELECAO_ID, 8.5);
		usuarioAtual = PROFESSOR_ID;
		var id = c.getIdentificador().orElseThrow();
		servico.registrarEntrevista(id, 9.0, Optional.empty());
		try {
			servico.aprovar(id);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta aprovar um candidato enquanto outro pendente está sem entrevista")
	public void aprovarComOutroSemEntrevista() {
		usuarioAtual = ALUNO_1;
		c1 = servico.candidatar(SELECAO_ID, 9.0);
		usuarioAtual = ALUNO_2;
		c2 = servico.candidatar(SELECAO_ID, 8.0);
		usuarioAtual = PROFESSOR_ID;
		servico.registrarEntrevista(c1.getIdentificador().orElseThrow(), 9.0, Optional.empty());
		agora = AGORA.plusDays(11);
		try {
			servico.aprovar(c1.getIdentificador().orElseThrow());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta aprovar um candidato com resultado abaixo do patamar mínimo")
	public void aprovarAbaixoPatamar() {
		var c = servico.candidatar(SELECAO_ID, 7.0);
		usuarioAtual = PROFESSOR_ID;
		var id = c.getIdentificador().orElseThrow();
		servico.registrarEntrevista(id, 5.0, Optional.empty());
		agora = AGORA.plusDays(11);
		try {
			servico.aprovar(id);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta aprovar um candidato havendo outro mais bem classificado pendente")
	public void aprovarForaOrdem() {
		usuarioAtual = ALUNO_1;
		c1 = servico.candidatar(SELECAO_ID, 9.0);
		usuarioAtual = ALUNO_2;
		c2 = servico.candidatar(SELECAO_ID, 8.0);
		usuarioAtual = PROFESSOR_ID;
		servico.registrarEntrevista(c1.getIdentificador().orElseThrow(), 9.0, Optional.empty());
		servico.registrarEntrevista(c2.getIdentificador().orElseThrow(), 8.0, Optional.empty());
		agora = AGORA.plusDays(11);
		try {
			servico.aprovar(c2.getIdentificador().orElseThrow());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor aprova os candidatos melhores classificados até preencher as vagas")
	public void aprovarEmOrdemEPreencherVagas() {
		usuarioAtual = ALUNO_1;
		c1 = servico.candidatar(SELECAO_ID, 9.0);
		agora = agora.plusMinutes(1);
		usuarioAtual = ALUNO_2;
		c2 = servico.candidatar(SELECAO_ID, 8.5);
		agora = agora.plusMinutes(1);
		usuarioAtual = ALUNO_3;
		c3 = servico.candidatar(SELECAO_ID, 7.5);
		usuarioAtual = PROFESSOR_ID;
		servico.registrarEntrevista(c1.getIdentificador().orElseThrow(), 9.0, Optional.empty());
		servico.registrarEntrevista(c2.getIdentificador().orElseThrow(), 8.5, Optional.empty());
		servico.registrarEntrevista(c3.getIdentificador().orElseThrow(), 8.0, Optional.empty());
		agora = AGORA.plusDays(11);
		c1 = servico.aprovar(c1.getIdentificador().orElseThrow());
		c2 = servico.aprovar(c2.getIdentificador().orElseThrow());
	}

	@Então("os aprovados assumem a vaga, a seleção é encerrada e os demais pendentes são reprovados")
	public void verificarPreenchimentoVagas() {
		assertEquals(CandidaturaSituacao.APROVADA, c1.getSituacao());
		assertEquals(CandidaturaSituacao.APROVADA, c2.getSituacao());
		assertEquals(
				CandidaturaSituacao.REPROVADA,
				candidaturas.get(c3.getIdentificador().orElseThrow().toString()).getSituacao());
		assertEquals(SelecaoSituacao.ENCERRADA, selecoes.get(SELECAO_ID.toString()).getSituacao());
	}
}
