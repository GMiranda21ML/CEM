package br.com.cem.dominio.monitoria.falta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.*;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.*;
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

import dev.sauloaraujo.d5.framework.dominio.AgregadoExistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public final class FaltaStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final IFaltaRepositorio repositorio = mock(IFaltaRepositorio.class);
	private final ISessaoRepositorio sessoes = mock(ISessaoRepositorio.class);
	private final IDisponibilidadeRepositorio disponibilidades =
			mock(IDisponibilidadeRepositorio.class);
	private final IAlertaRepositorio alertas = mock(IAlertaRepositorio.class);
	private final ICampusRepositorio campus = mock(ICampusRepositorio.class);
	private final IEnviadorEmailServico email = mock(IEnviadorEmailServico.class);
	private final ICoordenadoresAlertaServico coordenadores = mock(ICoordenadoresAlertaServico.class);
	private final FaltaServico servico =
			new FaltaServico(
					seguranca,
					repositorio,
					usuarios,
					relogio,
					sessoes,
					disponibilidades,
					campus,
					alertas,
					email,
					coordenadores);
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	private final UsuarioIdentificador monitor = new UsuarioIdentificador("1");
	private final UsuarioIdentificador aluno = new UsuarioIdentificador("2");
	private final SessaoIdentificador sessaoId = new SessaoIdentificador("1");
	private final FaltaIdentificador faltaId = new FaltaIdentificador("1");
	private RuntimeException excecaoCapturada;
	private Falta falta;
	private Sessao sessao;
	private List<SugestaoReposicao> sugestoesLivres;
	private List<SugestaoReposicao> sugestoesOcupadas;

	@Dado("o ambiente de falta preparado")
	public void preparar() {
		when(relogio.agora()).thenReturn(agora);
		when(seguranca.getUsuario()).thenReturn("monitor@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(usuarios.buscarPorEmail(any())).thenReturn(usuario(monitor));
		sessao =
				new Sessao(
						Optional.of(sessaoId),
						"Cálculo I",
						new UsuarioIdentificador("3"),
						monitor,
						new CampusIdentificador("1"),
						List.of(aluno),
						agora.minusDays(1),
						agora.minusDays(1).plusHours(1),
						false);
		when(sessoes.buscarPorIdentificador(sessaoId)).thenReturn(sessao);
		when(repositorio.incluir(any()))
				.thenAnswer(
						chamada -> {
							Falta registrada = chamada.getArgument(0);
							falta =
									new Falta(
											Optional.of(faltaId),
											registrada.getSessao(),
											registrada.getPessoa(),
											registrada.getPapel(),
											registrada.getAutor(),
											registrada.getObservacao(),
											registrada.getRegistradaEm(),
											registrada.getReposicao());
							return falta;
						});
	}

	@Quando("registro duas faltas da mesma pessoa na mesma sessão")
	public void registrarDuplicidade() {
		registrar();
		when(repositorio.existePorSessaoEPessoa(sessaoId, aluno)).thenReturn(true);
		capturar(this::registrar);
	}

	@Então("a segunda falta é recusada")
	public void verificarDuplicidade() {
		assertInstanceOf(AgregadoExistenteExcecao.class, excecaoCapturada);
		verify(repositorio, times(1)).incluir(any());
	}

	@Quando("registro falta antes do encerramento da sessão")
	public void registrarAntesDaSessao() {
		when(relogio.agora()).thenReturn(agora.minusDays(2));
		capturar(this::registrar);
	}

	@Então("a falta da sessão futura é recusada")
	public void verificarSessaoFutura() {
		assertInstanceOf(IllegalStateException.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("excluo uma falta com reposição ativa")
	public void excluirComReposicao() {
		prepararFalta();
		var reposicao = new SessaoIdentificador("2");
		falta = falta.agendarReposicao(reposicao);
		when(sessoes.buscarPorIdentificador(reposicao)).thenReturn(sessao);
		capturar(() -> servico.excluir(faltaId));
	}

	@Então("a falta com reposição permanece registrada")
	public void verificarExclusaoRecusada() {
		assertInstanceOf(IllegalStateException.class, excecaoCapturada);
		verify(repositorio, never()).excluir(any());
	}

	@Quando("um aluno relata ausência do monitor depois de trinta dias")
	public void relatarDepoisDoPrazo() {
		when(usuarios.buscarPorEmail(any())).thenReturn(usuario(aluno));
		when(relogio.agora()).thenReturn(agora.plusDays(31));
		capturar(
				() ->
						servico.registrar(
								sessaoId, monitor, "MONITOR", Optional.of("Monitor ausente")));
	}

	@Então("o relato fora do prazo é recusado")
	public void verificarPrazoRelato() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("registro a terceira falta e outra falta na mesma disciplina")
	public void registrarRecorrencia() {
		prepararCoordenador();
		when(repositorio.contar(aluno, "ALUNO", "Cálculo I")).thenReturn(3L, 4L);
		registrar();
		when(alertas.existeNovo(aluno, "ALUNO", "Cálculo I")).thenReturn(true);
		registrar();
	}

	@Então("a coordenação recebe um único alerta novo")
	public void verificarAlertaUnico() {
		verify(alertas, times(1)).incluir(any(Alerta.class));
		verify(email, times(1)).enviar(eq("coordenador@cem.edu.br"),
				eq("Alerta de recorrência de faltas"), anyString());
	}

	private void prepararCoordenador() {
		var coordenador = new Usuario(Optional.of(new UsuarioIdentificador("4")),
				"Coordenador", new UsuarioEmail("coordenador@cem.edu.br"),
				new UsuarioSenha("Cem@2026!"), Autorizacao.COORDENADOR,
				"Computação", null, UsuarioSituacao.ATIVO);
		when(coordenadores.buscarPorPessoa(aluno)).thenReturn(List.of(coordenador));
		when(usuarios.buscarPorIdentificador(aluno)).thenReturn(usuario(aluno));
	}

	@Quando("registro a terceira falta com coordenador responsável cadastrado")
	public void registrarComCoordenador() {
		prepararCoordenador();
		when(repositorio.contar(aluno, "ALUNO", "Cálculo I")).thenReturn(3L);
		registrar();
	}

	@Então("o coordenador responsável recebe um e-mail com a pessoa e a disciplina")
	public void verificarEmailCoordenador() {
		verify(email).enviar(eq("coordenador@cem.edu.br"),
				eq("Alerta de recorrência de faltas"),
				argThat(texto -> texto.contains("Participante")
						&& texto.contains("Cálculo I") && texto.contains("3 faltas")));
	}

	@Quando("registro a terceira falta sem coordenador responsável cadastrado")
	public void registrarSemCoordenador() {
		when(coordenadores.buscarPorPessoa(aluno)).thenReturn(List.of());
		when(repositorio.contar(aluno, "ALUNO", "Cálculo I")).thenReturn(3L);
		registrar();
	}

	@Então("a falta e o alerta são registrados sem envio de e-mail")
	public void verificarSemCoordenador() {
		verify(repositorio).incluir(any(Falta.class));
		verify(alertas).incluir(any(Alerta.class));
		verifyNoInteractions(email);
	}

	@Quando("consulto sugestões na disponibilidade e depois com agenda ocupada")
	public void consultarHorarios() {
		prepararFalta();
		prepararDisponibilidade();
		sugestoesLivres = servico.sugerir(faltaId);
		when(sessoes.conflita(any(), any(), any(), any())).thenReturn(true);
		sugestoesOcupadas = servico.sugerir(faltaId);
	}

	@Então("somente os horários livres são sugeridos")
	public void verificarSugestoes() {
		assertFalse(sugestoesLivres.isEmpty());
		assertTrue(sugestoesLivres.size() <= FaltaServico.QUANTIDADE_SUGESTOES);
		assertEquals(
				agora.toLocalDate().plusDays(1).atTime(10, 0), sugestoesLivres.getFirst().inicio());
		assertTrue(sugestoesOcupadas.isEmpty());
	}

	@Quando("agendo um horário fora das sugestões disponíveis")
	public void agendarHorarioAdulterado() {
		prepararFalta();
		when(disponibilidades.buscarDoMonitor(any(), anyString())).thenReturn(List.of());
		capturar(
				() ->
						servico.agendar(
								faltaId,
								new SugestaoReposicao(
										agora.plusDays(1),
										agora.plusDays(1).plusHours(1),
										new CampusIdentificador("1"))));
	}

	@Então("nenhuma reposição é gravada")
	public void verificarAgendamentoRecusado() {
		assertInstanceOf(IllegalStateException.class, excecaoCapturada);
		verify(sessoes, never()).incluir(any());
		verify(repositorio, never()).alterar(any());
	}

	@Quando("cadastro uma sessão com início igual ao fim")
	public void cadastrarHorarioInvalido() {
		capturar(
				() ->
						new Sessao(
								Optional.empty(),
								"Cálculo I",
								new UsuarioIdentificador("3"),
								monitor,
								new CampusIdentificador("1"),
								List.of(aluno),
								agora,
								agora,
								false));
	}

	@Então("a sessão sem duração é recusada")
	public void verificarDuracao() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
	}

	@Quando("agendo uma sugestão compatível para a falta")
	public void agendarSugestao() {
		prepararFalta();
		prepararDisponibilidade();
		when(sessoes.incluir(any()))
				.thenAnswer(
						chamada -> {
							Sessao nova = chamada.getArgument(0);
							return new Sessao(
									Optional.of(new SessaoIdentificador("2")),
									nova.getDisciplina(),
									nova.getProfessor(),
									nova.getMonitor(),
									nova.getCampus(),
									nova.getAlunos(),
									nova.getInicio(),
									nova.getFim(),
									false);
						});
		when(usuarios.buscarPorIdentificador(any()))
				.thenAnswer(chamada -> usuario(chamada.getArgument(0)));
		doAnswer(
						chamada -> {
							falta = chamada.getArgument(0);
							return null;
						})
				.when(repositorio)
				.alterar(any());
		servico.agendar(faltaId, servico.sugerir(faltaId).getFirst());
	}

	@Então("a falta referencia a sessão de reposição pelo identificador")
	public void verificarVinculoReposicao() {
		assertEquals(Optional.of(new SessaoIdentificador("2")), falta.getReposicao());
		verify(sessoes, times(1)).incluir(any());
		verify(email, times(2)).enviar(anyString(), eq("Reposição agendada"), anyString());
	}

	private void registrar() {
		servico.registrar(sessaoId, aluno, "ALUNO", Optional.empty());
	}

	private Usuario usuario(UsuarioIdentificador id) {
		return new Usuario(
				Optional.of(id),
				"Participante",
				new UsuarioEmail("p" + id + "@aluno.cem.edu.br"),
				new UsuarioSenha("Cem@2026!"),
				Autorizacao.ALUNO,
				null,
				null,
				UsuarioSituacao.ATIVO);
	}

	private void prepararFalta() {
		falta =
				new Falta(
						Optional.of(faltaId),
						sessaoId,
						aluno,
						"ALUNO",
						monitor,
						Optional.empty(),
						agora,
						Optional.empty());
		when(repositorio.existePorIdentificador(faltaId)).thenReturn(true);
		when(repositorio.buscarPorIdentificador(faltaId)).thenAnswer(chamada -> falta);
	}

	private void prepararDisponibilidade() {
		var data = agora.toLocalDate().plusDays(1);
		when(disponibilidades.buscarDoMonitor(any(), anyString()))
				.thenReturn(
						List.of(
								new Disponibilidade(
										Optional.empty(),
										"Cálculo I",
										monitor,
										new CampusIdentificador("1"),
										data.getDayOfWeek().getValue(),
										LocalTime.of(10, 0),
										LocalTime.of(12, 0))));
		when(campus.buscarPorIdentificador(any()))
				.thenReturn(
						new Campus(
								Optional.of(new CampusIdentificador("1")),
								new InstituicaoIdentificador("cem.edu.br"),
								"Central",
								null,
								CampusSituacao.ATIVO));
	}

	private void capturar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}
}
