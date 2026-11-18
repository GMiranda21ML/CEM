package br.com.cem.dominio.monitoria.selecao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
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
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
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

public final class SelecaoStepDefinitions {
	private static final UsuarioIdentificador PROFESSOR_ID = new UsuarioIdentificador("10");
	private static final UsuarioIdentificador OUTRO_PROFESSOR_ID = new UsuarioIdentificador("11");
	private static final UsuarioIdentificador ALUNO_ID = new UsuarioIdentificador("20");
	private static final DisciplinaIdentificador DISCIPLINA_ID = new DisciplinaIdentificador("1");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 10, 10, 0);

	private final Map<String, Selecao> selecoes = new LinkedHashMap<>();
	private final Map<String, Candidatura> candidaturas = new LinkedHashMap<>();
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final ISelecaoRepositorio repositorio = mock(ISelecaoRepositorio.class);
	private final ICandidaturaRepositorio candidaturaRepositorio =
			mock(ICandidaturaRepositorio.class);
	private final IDisciplinaRepositorio disciplinaRepositorio = mock(IDisciplinaRepositorio.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final SelecaoServico servico =
			new SelecaoServico(
					seguranca,
					repositorio,
					candidaturaRepositorio,
					disciplinaRepositorio,
					usuarioRepositorio,
					relogio);

	private int proximoId = 1;
	private UsuarioIdentificador usuarioAtual = PROFESSOR_ID;
	private Selecao resultado;
	private RuntimeException excecaoCapturada;

	public SelecaoStepDefinitions() {
		when(relogio.agora()).thenReturn(AGORA);
		when(seguranca.getUsuario()).thenAnswer(c -> emailDe(usuarioAtual));
		when(seguranca.possuiAutorizacao(anyString())).thenReturn(false);
		when(seguranca.possuiAutorizacao(Autorizacao.PROFESSOR.name())).thenReturn(true);
		when(usuarioRepositorio.buscarPorEmail(any())).thenAnswer(c -> usuario(usuarioAtual));
		when(usuarioRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> usuario(c.getArgument(0)));
		when(disciplinaRepositorio.existePorIdentificador(DISCIPLINA_ID)).thenReturn(true);
		when(disciplinaRepositorio.buscarPorIdentificador(DISCIPLINA_ID))
				.thenReturn(
						new Disciplina(
								Optional.of(DISCIPLINA_ID),
								new InstituicaoIdentificador("cem.edu.br"),
								new CursoIdentificador("1"),
								PROFESSOR_ID,
								"Cálculo I",
								new DisciplinaCodigo("MAT1001"),
								"Matemática"));
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(c -> selecoes.containsKey(c.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> selecoes.get(c.getArgument(0).toString()));
		when(repositorio.existeAbertaPorDisciplina(any(), any()))
				.thenAnswer(
						c -> {
							DisciplinaIdentificador disc = c.getArgument(0);
							Optional<SelecaoIdentificador> ignorado = c.getArgument(1);
							return selecoes.values().stream()
									.anyMatch(
											s ->
													s.getDisciplina().equals(disc)
															&& !s.encerrada()
															&& !s.getIdentificador()
																	.equals(ignorado));
						});
		when(repositorio.incluir(any()))
				.thenAnswer(
						c -> {
							Selecao s = c.getArgument(0);
							var id = new SelecaoIdentificador(String.valueOf(proximoId++));
							var salva =
									new Selecao(
											Optional.of(id),
											s.getDisciplina(),
											s.getProfessor(),
											s.getSemestre(),
											s.getPrazoInscricao(),
											s.getVagas(),
											s.getCrMinimo(),
											s.getPesoCr(),
											s.getPesoEntrevista(),
											s.getPatamarMinimo(),
											s.getSituacao(),
											s.getMotivoEncerramento(),
											s.getAbertaEm(),
											s.getEncerradaEm());
							selecoes.put(id.toString(), salva);
							return salva;
						});
		doAnswer(
						c -> {
							Selecao s = c.getArgument(0);
							selecoes.put(s.getIdentificador().orElseThrow().toString(), s);
							return null;
						})
				.when(repositorio)
				.alterar(any());

		when(candidaturaRepositorio.existePorSelecao(any()))
				.thenAnswer(
						c -> {
							SelecaoIdentificador sid = c.getArgument(0);
							return candidaturas.values().stream()
									.anyMatch(x -> x.getSelecao().equals(sid));
						});
		when(candidaturaRepositorio.listarPorSelecao(any()))
				.thenAnswer(
						c -> {
							SelecaoIdentificador sid = c.getArgument(0);
							return candidaturas.values().stream()
									.filter(x -> x.getSelecao().equals(sid))
									.toList();
						});
		doAnswer(
						c -> {
							Candidatura cand = c.getArgument(0);
							candidaturas.put(
									cand.getIdentificador().orElseThrow().toString(), cand);
							return null;
						})
				.when(candidaturaRepositorio)
				.alterar(any());
	}

	private Usuario usuario(UsuarioIdentificador id) {
		return new Usuario(
				Optional.of(id),
				"Usuário " + id,
				new UsuarioEmail(emailDe(id)),
				new UsuarioSenha("Cem@2026!"),
				Autorizacao.PROFESSOR,
				"Computação",
				null,
				UsuarioSituacao.ATIVO);
	}

	private String emailDe(UsuarioIdentificador id) {
		return "u" + id + "@cem.edu.br";
	}

	@Dado("um professor responsável por uma disciplina")
	public void prepararContexto() {
		selecoes.clear();
		candidaturas.clear();
		usuarioAtual = PROFESSOR_ID;
		resultado = null;
		excecaoCapturada = null;
	}

	@Quando("o professor abre uma seleção com critérios válidos para sua disciplina")
	public void abrirSelecaoValida() {
		resultado =
				servico.abrir(
						DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 2, 7.0, 60, 40, 7.5);
	}

	@Então("a seleção é aberta com a situação aberta")
	public void verificarSelecaoAberta() {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(SelecaoSituacao.ABERTA, resultado.getSituacao());
	}

	@Quando("outro professor tenta abrir uma seleção para a disciplina")
	public void outroProfessorAbreSelecao() {
		usuarioAtual = OUTRO_PROFESSOR_ID;
		try {
			servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 60, 40, 7.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Então("a operação da seleção é recusada")
	public void verificarRecusa() {
		assertNotNull(excecaoCapturada);
	}

	@Quando("o professor abre uma seleção e tenta abrir outra na mesma disciplina")
	public void abrirDuasSelecoes() {
		servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 60, 40, 7.0);
		try {
			servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(20), 2, 7.0, 50, 50, 7.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta abrir uma seleção com pesos somando diferente de cem por cento")
	public void abrirComPesosInvalidos() {
		try {
			servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 50, 30, 7.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor tenta abrir uma seleção com prazo anterior à data atual")
	public void abrirComPrazoPassado() {
		try {
			servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().minusDays(1), 1, 7.0, 60, 40, 7.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor edita os critérios da seleção sem candidaturas registradas")
	public void editarSemCandidaturas() {
		var aberta =
				servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 60, 40, 7.0);
		resultado =
				servico.editar(
						aberta.getIdentificador().orElseThrow(),
						AGORA.toLocalDate().plusDays(21),
						3,
						7.5,
						50,
						50,
						8.0);
	}

	@Então("os novos critérios da seleção são salvos")
	public void verificarEdicao() {
		assertNull(excecaoCapturada);
		assertEquals(3, resultado.getVagas());
		assertEquals(7.5, resultado.getCrMinimo());
		assertEquals(50, resultado.getPesoCr());
		assertEquals(50, resultado.getPesoEntrevista());
		assertEquals(8.0, resultado.getPatamarMinimo());
	}

	@Quando("o professor tenta editar uma seleção que já possui candidaturas")
	public void editarComCandidaturas() {
		var aberta =
				servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 60, 40, 7.0);
		var sid = aberta.getIdentificador().orElseThrow();
		candidaturas.put(
				"1",
				new Candidatura(
						Optional.of(new CandidaturaIdentificador("1")),
						sid,
						DISCIPLINA_ID,
						ALUNO_ID,
						8.0,
						CandidaturaSituacao.PENDENTE,
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						AGORA,
						Optional.empty()));
		try {
			servico.editar(sid, AGORA.toLocalDate().plusDays(20), 2, 7.0, 60, 40, 7.0);
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o professor encerra a seleção com candidatura pendente")
	public void encerrarComPendente() {
		var aberta =
				servico.abrir(DISCIPLINA_ID, AGORA.toLocalDate().plusDays(14), 1, 7.0, 60, 40, 7.0);
		var sid = aberta.getIdentificador().orElseThrow();
		candidaturas.put(
				"1",
				new Candidatura(
						Optional.of(new CandidaturaIdentificador("1")),
						sid,
						DISCIPLINA_ID,
						ALUNO_ID,
						8.0,
						CandidaturaSituacao.PENDENTE,
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						AGORA,
						Optional.empty()));
		resultado = servico.encerrar(sid);
	}

	@Então("a seleção fica encerrada e a candidatura pendente é reprovada automaticamente")
	public void verificarEncerramento() {
		assertEquals(SelecaoSituacao.ENCERRADA, resultado.getSituacao());
		assertEquals(CandidaturaSituacao.REPROVADA, candidaturas.get("1").getSituacao());
		assertTrue(
				candidaturas
						.get("1")
						.getMotivoDecisao()
						.orElseThrow()
						.contains("seleção encerrada"));
	}
}
