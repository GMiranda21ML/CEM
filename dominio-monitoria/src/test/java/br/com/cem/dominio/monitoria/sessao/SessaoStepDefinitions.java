package br.com.cem.dominio.monitoria.sessao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.sessao.spi.ISessaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class SessaoStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final ISessaoRepositorio repositorio = mock(ISessaoRepositorio.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final SessaoServico servico =
			new SessaoServico(seguranca, repositorio, usuarios, mock(ICampusRepositorio.class));
	private final SessaoIdentificador identificador = new SessaoIdentificador("1");
	private Sessao sessao;
	private RuntimeException excecaoCapturada;

	@Dado("um aluno e uma sessão sob sua monitoria")
	public void prepararSessao() {
		var monitor = new UsuarioIdentificador("1");
		when(seguranca.getUsuario()).thenReturn("monitor@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(usuarios.buscarPorEmail(any()))
				.thenReturn(
						new Usuario(
								Optional.of(monitor),
								"Monitor",
								new UsuarioEmail("monitor@aluno.cem.edu.br"),
								new UsuarioSenha("Cem@2026!"),
								Autorizacao.ALUNO,
								null,
								null,
								UsuarioSituacao.ATIVO));
		var inicio = LocalDateTime.of(2026, 11, 14, 10, 0);
		sessao =
				new Sessao(
						Optional.of(identificador),
						"Cálculo I",
						new UsuarioIdentificador("3"),
						monitor,
						new CampusIdentificador("1"),
						List.of(new UsuarioIdentificador("2")),
						inicio,
						inicio.plusHours(1),
						false);
		when(repositorio.existePorIdentificador(identificador)).thenReturn(true);
		when(repositorio.buscarPorIdentificador(identificador)).thenAnswer(chamada -> sessao);
		doAnswer(
						chamada -> {
							sessao = chamada.getArgument(0);
							return null;
						})
				.when(repositorio)
				.alterar(any());
	}

	@Quando("o aluno tenta cadastrar uma sessão")
	public void cadastrarComoAluno() {
		try {
			servico.incluir(sessao);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Então("o cadastro da sessão é recusado")
	public void verificarCadastroRecusado() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("o monitor cancela sua sessão")
	public void cancelarSessao() {
		servico.cancelar(identificador);
	}

	@Então("a sessão permanece cadastrada e cancelada")
	public void verificarCancelamento() {
		assertEquals(Optional.of(identificador), sessao.getIdentificador());
		assertTrue(sessao.getCancelada());
		verify(repositorio, never()).excluir(any());
	}
}
