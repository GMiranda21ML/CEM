package br.com.cem.dominio.monitoria.sessao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SessaoStepDefinitions {
	private static final UsuarioIdentificador MONITOR = new UsuarioIdentificador("1");
	private static final UsuarioIdentificador ALUNO = new UsuarioIdentificador("2");
	private static final UsuarioIdentificador OUTRO_ALUNO = new UsuarioIdentificador("4");
	private static final UsuarioIdentificador PROFESSOR = new UsuarioIdentificador("3");
	private static final CampusIdentificador CAMPUS = new CampusIdentificador("1");
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 13, 9, 0);
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final ISessaoRepositorio repositorio = mock(ISessaoRepositorio.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final ICampusRepositorio campi = mock(ICampusRepositorio.class);
	private final IEnviadorEmailServico email = mock(IEnviadorEmailServico.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final SessaoServico servico = new SessaoServico(seguranca, repositorio, usuarios, campi, email, relogio);
	private final SessaoIdentificador identificador = new SessaoIdentificador("1");
	private Sessao sessao;
	private RuntimeException excecaoCapturada;
	private LocalDateTime agora = AGORA;
	private UsuarioIdentificador usuarioAtual = MONITOR;

	public SessaoStepDefinitions() {
		when(relogio.agora()).thenAnswer(chamada -> agora);
		when(seguranca.getUsuario()).thenAnswer(chamada -> emailDe(usuarioAtual));
		when(seguranca.possuiAutorizacao(anyString())).thenReturn(false);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(usuarios.buscarPorEmail(any())).thenAnswer(chamada -> usuario(usuarioAtual));
		when(usuarios.buscarPorIdentificador(any())).thenAnswer(chamada -> usuario(chamada.getArgument(0)));
		Campus campus = mock(Campus.class);
		when(campus.estaAtivo()).thenReturn(true);
		when(campi.buscarPorIdentificador(CAMPUS)).thenReturn(campus);
		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Sessao recebida = chamada.getArgument(0);
			return Sessao.restaurar(Optional.of(identificador), recebida.getDisciplina(), recebida.getProfessor(), recebida.getMonitor(), recebida.getCampus(), recebida.getSala(), recebida.getAlunos(), recebida.getRespostas(), recebida.getInicio(), recebida.getFim(), recebida.getInicioAnterior(), recebida.getCancelada());
		});
		when(repositorio.existePorIdentificador(any())).thenReturn(true);
		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> sessao);
		doAnswer(chamada -> { sessao = chamada.getArgument(0); return null; }).when(repositorio).alterar(any());
	}

	private Usuario usuario(UsuarioIdentificador id) {
		Usuario usuario = mock(Usuario.class);
		when(usuario.getIdentificador()).thenReturn(Optional.of(id));
		when(usuario.getEmailInstitucional()).thenReturn(new UsuarioEmail(emailDe(id)));
		when(usuario.getPapel()).thenReturn(id.equals(PROFESSOR) ? Autorizacao.PROFESSOR : Autorizacao.ALUNO);
		when(usuario.getSituacao()).thenReturn(UsuarioSituacao.ATIVO);
		when(usuario.getCurso()).thenReturn("Computação");
		return usuario;
	}

	private String emailDe(UsuarioIdentificador id) { return id.toString() + "@aluno.cem.edu.br"; }

	@Dado("um aluno e uma sessão sob sua monitoria")
	public void prepararSessao() {
		agora = AGORA;
		usuarioAtual = MONITOR;
		sessao = new Sessao(Optional.of(identificador), "Cálculo I", PROFESSOR, MONITOR, CAMPUS, "Sala 1", List.of(ALUNO), AGORA.plusDays(1), AGORA.plusDays(1).plusHours(1), false);
	}

	@Quando("o aluno tenta cadastrar uma sessão")
	public void cadastrarComoAluno() {
		usuarioAtual = ALUNO;
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
		try { servico.incluir(sessao); } catch (RuntimeException e) { excecaoCapturada = e; }
	}
	@Então("o cadastro da sessão é recusado")
	public void cadastroRecusado() { assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada); verify(repositorio, never()).incluir(any()); }
	@Quando("o monitor cancela sua sessão") public void cancelar() { servico.cancelar(identificador); }
	@Então("a sessão permanece cadastrada e cancelada") public void cancelada() { assertEquals(Optional.of(identificador), sessao.getIdentificador()); assertTrue(sessao.getCancelada()); verify(repositorio, never()).excluir(any()); }

	@Quando("o monitor registra uma nova sessão")
	public void registrarSessao() { sessao = new Sessao(Optional.empty(), "Cálculo I", PROFESSOR, MONITOR, CAMPUS, "Sala 1", List.of(ALUNO), AGORA.plusDays(1), AGORA.plusDays(1).plusHours(1), false); sessao = servico.registrar(sessao); }
	@Então("a sessão é cadastrada com participantes sem resposta")
	public void cadastradaSemResposta() { assertTrue(sessao.getIdentificador().isPresent()); assertEquals(SituacaoRespostaSessao.SEM_RESPOSTA, sessao.getRespostas().iterator().next().getSituacao()); verify(email).enviar(anyString(), anyString(), anyString()); }

	@Quando("o monitor inclui um aluno elegível na sessão")
	public void incluirAluno() { servico.adicionarAluno(identificador, OUTRO_ALUNO); }
	@Então("o aluno fica sem resposta e recebe uma notificação")
	public void alunoNotificado() { assertTrue(sessao.getAlunos().contains(OUTRO_ALUNO)); assertTrue(sessao.getRespostas().stream().anyMatch(r -> r.getAluno().equals(OUTRO_ALUNO) && r.getSituacao() == SituacaoRespostaSessao.SEM_RESPOSTA)); verify(email).enviar(eq(emailDe(OUTRO_ALUNO)), anyString(), anyString()); }

	@Quando("o aluno confirma participação antes do início")
	public void alunoConfirma() { usuarioAtual = ALUNO; sessao = servico.responder(identificador, new RespostaSessao(ALUNO, SituacaoRespostaSessao.VAI_PARTICIPAR, Optional.empty(), Optional.empty())); }
	@Então("a resposta fica como participação confirmada")
	public void respostaConfirmada() { assertEquals(SituacaoRespostaSessao.VAI_PARTICIPAR, sessao.getRespostas().iterator().next().getSituacao()); }
	@Quando("o aluno tenta responder depois do início")
	public void responderTarde() { usuarioAtual = ALUNO; agora = sessao.getInicio(); try { servico.responder(identificador, new RespostaSessao(ALUNO, SituacaoRespostaSessao.VAI_PARTICIPAR, Optional.empty(), Optional.empty())); } catch (RuntimeException e) { excecaoCapturada = e; } }
	@Então("a resposta é recusada") public void respostaRecusada() { assertNotNull(excecaoCapturada); }

	@Quando("o monitor remarca a sessão para uma sugestão de aluno")
	public void remarcar() { sessao = sessao.responder(new RespostaSessao(ALUNO, SituacaoRespostaSessao.NAO_VAI_PARTICIPAR, Optional.empty(), Optional.of(AGORA.plusDays(2))), AGORA); usuarioAtual = MONITOR; servico.remarcarParaSugestao(identificador, ALUNO); }
	@Então("o novo horário mantém a duração e as respostas voltam para sem resposta")
	public void verificarRemarcacao() { assertEquals(1, java.time.Duration.between(sessao.getInicio(), sessao.getFim()).toHours()); assertEquals(SituacaoRespostaSessao.SEM_RESPOSTA, sessao.getRespostas().iterator().next().getSituacao()); assertEquals(Optional.of(AGORA.plusDays(1)), sessao.getInicioAnterior()); }
	@Quando("o monitor remarca a sessão para um horário conflitante")
	public void remarcarConflito() { sessao = sessao.responder(new RespostaSessao(ALUNO, SituacaoRespostaSessao.NAO_VAI_PARTICIPAR, Optional.empty(), Optional.of(AGORA.plusDays(2))), AGORA); usuarioAtual = MONITOR; when(repositorio.conflita(eq(MONITOR), eq(ALUNO), any(), any(), any())).thenReturn(true); try { servico.remarcarParaSugestao(identificador, ALUNO); } catch (RuntimeException e) { excecaoCapturada = e; } }
	@Então("a remarcação é recusada") public void remarcacaoRecusada() { assertNotNull(excecaoCapturada); }
}
