package br.com.cem.dominio.monitoria.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.disponibilidade.spi.IDisponibilidadeRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalTime;
import java.util.Optional;

public final class DisponibilidadeStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IDisponibilidadeRepositorio repositorio = mock(IDisponibilidadeRepositorio.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final DisponibilidadeServico servico =
			new DisponibilidadeServico(
					seguranca, repositorio, usuarios, mock(ICampusRepositorio.class));
	private Disponibilidade disponibilidade;
	private RuntimeException excecaoCapturada;

	@Dado("um aluno e a disponibilidade de outro monitor")
	public void prepararDisponibilidade() {
		when(seguranca.getUsuario()).thenReturn("aluno@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
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
		disponibilidade =
				new Disponibilidade(
						Optional.of(new DisponibilidadeIdentificador("1")),
						"Cálculo I",
						new UsuarioIdentificador("2"),
						new CampusIdentificador("1"),
						1,
						LocalTime.of(10, 0),
						LocalTime.of(12, 0));
		when(repositorio.existePorIdentificador(any())).thenReturn(true);
		when(repositorio.buscarPorIdentificador(any())).thenReturn(disponibilidade);
	}

	@Quando("o aluno tenta alterar o horário do outro monitor")
	public void alterarDisponibilidadeAlheia() {
		try {
			servico.alterar(disponibilidade);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Então("a alteração da disponibilidade é recusada")
	public void verificarAutoria() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).alterar(any());
	}

	@Quando("informo disponibilidade com fim anterior ao início")
	public void informarIntervaloInvalido() {
		try {
			new Disponibilidade(
					Optional.empty(),
					"Cálculo I",
					new UsuarioIdentificador("1"),
					new CampusIdentificador("1"),
					1,
					LocalTime.of(12, 0),
					LocalTime.of(10, 0));
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Então("o intervalo de disponibilidade é recusado")
	public void verificarIntervalo() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
	}
}
