package br.com.cem.dominio.monitoria.feedback;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.spi.IAcompanhamentoRepositorio;
import br.com.cem.dominio.monitoria.candidatura.spi.ICandidaturaRepositorio;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FeedbackStepDefinitions {
	private static final UsuarioIdentificador MONITOR_ID = new UsuarioIdentificador("40");
	private static final UsuarioIdentificador OUTRO_MONITOR_ID = new UsuarioIdentificador("41");
	private static final UsuarioIdentificador ALUNO_ID = new UsuarioIdentificador("42");
	private static final UsuarioIdentificador PROFESSOR_ID = new UsuarioIdentificador("43");
	private static final CampusIdentificador CAMPUS_ID = new CampusIdentificador("1");
	private static final SessaoIdentificador SESSAO_ID = new SessaoIdentificador("1");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 20, 16, 0);

	private final Map<String, Feedback> feedbacks = new LinkedHashMap<>();
	private final Map<String, Sessao> sessoes = new LinkedHashMap<>();

	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IFeedbackRepositorio repositorio = mock(IFeedbackRepositorio.class);
	private final IAcompanhamentoRepositorio acompanhamentoRepositorio =
			mock(IAcompanhamentoRepositorio.class);
	private final ICandidaturaRepositorio candidaturaRepositorio =
			mock(ICandidaturaRepositorio.class);
	private final ISessaoRepositorio sessaoRepositorio = mock(ISessaoRepositorio.class);
	private final IFaltaRepositorio faltaRepositorio = mock(IFaltaRepositorio.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);

	private final FeedbackServico servico =
			new FeedbackServico(
					seguranca,
					repositorio,
					acompanhamentoRepositorio,
					candidaturaRepositorio,
					sessaoRepositorio,
					faltaRepositorio,
					usuarioRepositorio,
					relogio);

	private int proximoId = 1;
	private UsuarioIdentificador usuarioAtual = MONITOR_ID;
	private boolean faltaMonitor;
	private boolean faltaAluno;
	private boolean acompanhamentoEncerrado;
	private Feedback resultado;
	private RuntimeException excecaoCapturada;

	public FeedbackStepDefinitions() {
		when(relogio.agora()).thenReturn(AGORA);
		when(seguranca.getUsuario()).thenAnswer(c -> emailDe(usuarioAtual));
		when(seguranca.possuiAutorizacao(anyString())).thenReturn(false);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(usuarioRepositorio.existePorIdentificador(any()))
				.thenAnswer(c -> c.getArgument(0).equals(ALUNO_ID));
		when(usuarioRepositorio.existePorEmail(any(), any())).thenReturn(true);
		when(usuarioRepositorio.buscarPorEmail(any())).thenAnswer(c -> usuario(usuarioAtual));
		when(usuarioRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> usuario(c.getArgument(0)));
		when(candidaturaRepositorio.ehMonitorEmAlgumaDisciplina(any()))
				.thenAnswer(
						c ->
								c.getArgument(0).equals(MONITOR_ID)
										|| c.getArgument(0).equals(OUTRO_MONITOR_ID));
		when(sessaoRepositorio.existePorIdentificador(any()))
				.thenAnswer(c -> sessoes.containsKey(c.getArgument(0).toString()));
		when(sessaoRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> sessoes.get(c.getArgument(0).toString()));
		when(faltaRepositorio.existePorSessaoEPessoa(any(), any()))
				.thenAnswer(
						c -> {
							UsuarioIdentificador pessoa = c.getArgument(1);
							if (pessoa.equals(MONITOR_ID)) return faltaMonitor;
							if (pessoa.equals(ALUNO_ID)) return faltaAluno;
							return false;
						});
		when(acompanhamentoRepositorio.existeBloqueioPorEncerramento(any(), any(), any()))
				.thenAnswer(c -> acompanhamentoEncerrado);
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(c -> feedbacks.containsKey(c.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any()))
				.thenAnswer(c -> feedbacks.get(c.getArgument(0).toString()));
		when(repositorio.existePorSessaoEAluno(any(), any()))
				.thenAnswer(
						c -> {
							SessaoIdentificador sid = c.getArgument(0);
							UsuarioIdentificador aid = c.getArgument(1);
							return feedbacks.values().stream()
									.anyMatch(
											f ->
													f.getSessao().equals(sid)
															&& f.getAluno().equals(aid));
						});
		when(repositorio.incluir(any()))
				.thenAnswer(
						c -> {
							Feedback f = c.getArgument(0);
							var id = new FeedbackIdentificador(String.valueOf(proximoId++));
							var salvo =
									new Feedback(
											Optional.of(id),
											f.getSessao(),
											f.getAluno(),
											f.getAutor(),
											f.getNota(),
											f.getComentario(),
											f.getCriadoEm(),
											f.getAtualizadoEm());
							feedbacks.put(id.toString(), salvo);
							return salvo;
						});
		doAnswer(
						c -> {
							Feedback f = c.getArgument(0);
							feedbacks.put(f.getIdentificador().orElseThrow().toString(), f);
							return null;
						})
				.when(repositorio)
				.alterar(any());
		doAnswer(
						c -> {
							FeedbackIdentificador id = c.getArgument(0);
							feedbacks.remove(id.toString());
							return null;
						})
				.when(repositorio)
				.excluir(any());
	}

	private Usuario usuario(UsuarioIdentificador id) {
		return new Usuario(
				Optional.of(id),
				"Usuário " + id,
				new UsuarioEmail(emailDe(id)),
				new UsuarioSenha("Cem@2026!"),
				Autorizacao.ALUNO,
				"Computação",
				null,
				UsuarioSituacao.ATIVO);
	}

	private String emailDe(UsuarioIdentificador id) {
		return "u" + id + "@cem.edu.br";
	}

	@Dado("um monitor que conduziu uma sessão realizada com um aluno presente")
	public void preparar() {
		feedbacks.clear();
		sessoes.clear();
		proximoId = 1;
		usuarioAtual = MONITOR_ID;
		faltaMonitor = false;
		faltaAluno = false;
		acompanhamentoEncerrado = false;
		resultado = null;
		excecaoCapturada = null;
		sessoes.put(
				SESSAO_ID.toString(),
				Sessao.restaurar(
						Optional.of(SESSAO_ID),
						"Cálculo I",
						PROFESSOR_ID,
						MONITOR_ID,
						CAMPUS_ID,
						"Sala 1",
						List.of(ALUNO_ID),
						List.of(),
						AGORA.minusHours(2),
						AGORA.minusHours(1),
						Optional.empty(),
						false));
	}

	@Quando("o monitor registra o feedback do aluno na sessão com nota e comentário")
	public void registrarValido() {
		resultado = servico.registrar(SESSAO_ID, ALUNO_ID, 8.5, Optional.of("Ótima evolução"));
	}

	@Então("o feedback é registrado com sucesso")
	public void verificarRegistrado() {
		assertNull(excecaoCapturada);
		assertEquals(8.5, resultado.getNota());
	}

	@Quando("outro monitor tenta registrar feedback na sessão")
	public void outroMonitorRegistra() {
		usuarioAtual = OUTRO_MONITOR_ID;
		try {
			servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Então("a operação do feedback é recusada")
	public void verificarRecusa() {
		assertNotNull(excecaoCapturada);
	}

	@Quando("o monitor tenta registrar feedback em sessão ainda não realizada ou com falta do monitor")
	public void registrarComFaltaMonitorOuFutura() {
		faltaMonitor = true;
		try {
			servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o monitor tenta registrar feedback para um aluno que faltou à sessão")
	public void registrarAlunoFaltou() {
		faltaAluno = true;
		try {
			servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o monitor tenta registrar um segundo feedback para o mesmo aluno na sessão")
	public void registrarDuplicado() {
		servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		try {
			servico.registrar(SESSAO_ID, ALUNO_ID, 8.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Quando("o autor edita a nota do feedback e depois o exclui com acompanhamento ativo")
	public void editarEExcluirAtivo() {
		var fb = servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		var id = fb.getIdentificador().orElseThrow();
		resultado = servico.editar(id, 9.0, Optional.of("Revisado"));
		assertEquals(9.0, resultado.getNota());
		servico.excluir(id);
	}

	@Então("o feedback é atualizado e posteriormente removido")
	public void verificarAtualizadoERemovido() {
		assertNull(excecaoCapturada);
		assertFalse(
				feedbacks.containsKey(resultado.getIdentificador().orElseThrow().toString()));
	}

	@Quando("o autor tenta editar ou excluir um feedback após o encerramento do acompanhamento")
	public void editarOuExcluirEncerrado() {
		var fb = servico.registrar(SESSAO_ID, ALUNO_ID, 7.0, Optional.empty());
		acompanhamentoEncerrado = true;
		try {
			servico.editar(fb.getIdentificador().orElseThrow(), 9.0, Optional.empty());
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}
}
