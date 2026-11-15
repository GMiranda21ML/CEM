package br.com.cem.dominio.comunicacao.aviso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.aviso.spi.IAvisoRepositorio;
import br.com.cem.dominio.comunicacao.aviso.spi.IMonitorAvisoServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.Optional;

public final class AvisoStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IAvisoRepositorio repositorio = mock(IAvisoRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final IMonitorAvisoServico monitores = mock(IMonitorAvisoServico.class);
	private final AvisoServico servico =
			new AvisoServico(seguranca, repositorio, relogio, usuarios, monitores);
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	private RuntimeException excecaoCapturada;
	private boolean visivelNoUltimoDia;
	private boolean visivelDepois;
	private Aviso publicado;
	private AvisoAutorizacoes autorizacoes;

	@Dado("o ambiente de aviso preparado")
	public void preparar() {
		when(relogio.agora()).thenReturn(agora);
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	@Quando("publico um aviso com título de 101 caracteres")
	public void publicarTituloLongo() {
		capturar(() -> servico.publicar("x".repeat(101), "Texto", agora.toLocalDate()));
	}

	@Então("o aviso com título longo é recusado")
	public void verificarLimiteTitulo() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("um aluno tenta publicar um aviso")
	public void publicarComoAluno() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		prepararAluno();
		capturar(() -> servico.publicar("Título", "Texto", agora.toLocalDate()));
	}

	@Quando("publico um aviso como {string}")
	public void publicarComo(String perfil) {
		when(seguranca.possuiAutorizacao(anyString())).thenReturn(false);
		if (perfil.equals("MONITOR")) {
			prepararMonitor();
		} else {
			when(seguranca.possuiAutorizacao(perfil)).thenReturn(true);
		}
		when(repositorio.incluir(any())).thenAnswer(chamada -> chamada.getArgument(0));
		publicado =
				servico.publicar(
						"Encontro de monitoria", "Aviso aos participantes", agora.toLocalDate());
	}

	@Então("o aviso é publicado")
	public void verificarPublicacao() {
		assertEquals("Encontro de monitoria", publicado.getTitulo());
		assertTrue(publicado.getAtivo());
		verify(repositorio).incluir(publicado);
	}

	@Quando("consulto as permissões do mural como monitor")
	public void consultarComoMonitor() {
		when(seguranca.possuiAutorizacao(anyString())).thenReturn(false);
		prepararMonitor();
		autorizacoes = servico.criarAutorizacoes(Optional.empty());
	}

	@Então("posso publicar e não posso editar ou inativar avisos")
	public void verificarPermissoesMonitor() {
		assertTrue(autorizacoes.getPublicar());
		assertFalse(autorizacoes.getAlterar());
		assertFalse(autorizacoes.getAlternar());
	}

	private void prepararMonitor() {
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		prepararAluno();
		when(monitores.eMonitor(new UsuarioIdentificador("1"))).thenReturn(true);
	}

	private void prepararAluno() {
		when(seguranca.getUsuario()).thenReturn("aluno@aluno.cem.edu.br");
		when(usuarios.buscarPorEmail(any()))
				.thenReturn(
						new Usuario(
								Optional.of(new UsuarioIdentificador("1")),
								"Aluno",
								new UsuarioEmail("aluno@aluno.cem.edu.br"),
								new UsuarioSenha("Cem@2026!"),
								Autorizacao.ALUNO,
								null,
								null,
								UsuarioSituacao.ATIVO));
	}

	@Então("a publicação sem autorização é recusada")
	public void verificarAutorizacaoPublicacao() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("publico um aviso com validade de ontem")
	public void publicarValidadePassada() {
		capturar(() -> servico.publicar("Título", "Texto", agora.toLocalDate().minusDays(1)));
	}

	@Então("a validade passada é recusada")
	public void verificarValidadePassada() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("consulto um aviso no último dia e no dia seguinte")
	public void consultarValidade() {
		var aviso =
				new Aviso(Optional.empty(), "Título", "Texto", agora.toLocalDate(), true, agora);
		visivelNoUltimoDia = aviso.visivel(agora.toLocalDate());
		visivelDepois = aviso.visivel(agora.toLocalDate().plusDays(1));
	}

	@Então("o aviso só é exibido até o último dia de validade")
	public void verificarUltimoDia() {
		assertTrue(visivelNoUltimoDia);
		assertFalse(visivelDepois);
	}

	private void capturar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}
}
