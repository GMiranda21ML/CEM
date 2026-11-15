package br.com.cem.dominio.monitoria.alerta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.alerta.spi.IAlertaRepositorio;
import br.com.cem.dominio.monitoria.alerta.spi.ICoordenadoresAlertaServico;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.monitoria.falta.spi.IFaltaRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public final class AlertaStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IAlertaRepositorio repositorio = mock(IAlertaRepositorio.class);
	private final ICoordenadoresAlertaServico coordenadores = mock(ICoordenadoresAlertaServico.class);
	private final AlertaServico servico =
			new AlertaServico(seguranca, repositorio, mock(IFaltaRepositorio.class), coordenadores);
	private final AlertaIdentificador identificador = new AlertaIdentificador("1");
	private Alerta alerta;
	private RuntimeException excecaoCapturada;

	@Dado("um alerta de três faltas ainda não acompanhado")
	public void prepararAlerta() {
		when(seguranca.getUsuario()).thenReturn("coordenador@cem.edu.br");
		when(coordenadores.buscarPorPessoa(any())).thenReturn(List.of(new Usuario(
				Optional.of(new UsuarioIdentificador("2")), "Coordenador",
				new UsuarioEmail("coordenador@cem.edu.br"), new UsuarioSenha("Cem@2026!"),
				Autorizacao.COORDENADOR, "Computação", null, UsuarioSituacao.ATIVO)));
		alerta =
				new Alerta(
						Optional.of(identificador),
						new UsuarioIdentificador("1"),
						"ALUNO",
						"Cálculo I",
						false,
						LocalDateTime.of(2026, 11, 13, 12, 0));
		when(repositorio.existePorIdentificador(identificador)).thenReturn(true);
		when(repositorio.buscarPorIdentificador(identificador)).thenAnswer(chamada -> alerta);
		doAnswer(
						chamada -> {
							alerta = chamada.getArgument(0);
							return null;
						})
				.when(repositorio)
				.alterar(any());
	}

	@Quando("a coordenação acompanha o alerta")
	public void acompanharComoCoordenacao() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		servico.acompanhar(identificador);
	}

	@Então("o alerta fica acompanhado")
	public void verificarAcompanhamento() {
		assertTrue(alerta.getAcompanhado());
	}

	@Quando("um aluno tenta acompanhar o alerta")
	public void acompanharComoAluno() {
		try {
			servico.acompanhar(identificador);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Então("o acompanhamento é recusado")
	public void verificarAcesso() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).alterar(any());
	}

	@Quando("um coordenador sem responsabilidade tenta consultar e acompanhar o alerta")
	public void acessarComoOutroCoordenador() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		when(seguranca.getUsuario()).thenReturn("outro@cem.edu.br");
		assertThrows(NaoAutorizadoExcecao.class, () -> servico.buscar(identificador));
		assertThrows(NaoAutorizadoExcecao.class, () -> servico.contar(identificador));
		assertThrows(NaoAutorizadoExcecao.class, () -> servico.acompanhar(identificador));
	}

	@Então("a consulta e o acompanhamento são recusados")
	public void verificarOutroCoordenador() {
		assertFalse(alerta.getAcompanhado());
		verify(repositorio, never()).alterar(any());
	}
}
