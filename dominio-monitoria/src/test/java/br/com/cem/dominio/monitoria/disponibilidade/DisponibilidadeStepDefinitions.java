package br.com.cem.dominio.monitoria.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.Campus;
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
import java.util.List;
import java.util.Optional;

public final class DisponibilidadeStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IDisponibilidadeRepositorio repositorio = mock(IDisponibilidadeRepositorio.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final ICampusRepositorio campi = mock(ICampusRepositorio.class);
	private final Campus campus = mock(Campus.class);
	private final DisponibilidadeServico servico =
			new DisponibilidadeServico(seguranca, repositorio, usuarios, campi);
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
		Usuario usuario =
				new Usuario(
						Optional.of(new UsuarioIdentificador("1")),
						"Aluno",
						new UsuarioEmail("aluno@aluno.cem.edu.br"),
						new UsuarioSenha("Cem@2026!"),
						Autorizacao.ALUNO,
						null,
						null,
						UsuarioSituacao.ATIVO);
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
		when(usuarios.buscarPorIdentificador(any()))
				.thenReturn(usuario);
		when(campi.buscarPorIdentificador(any())).thenReturn(campus);
		when(campus.estaAtivo()).thenReturn(true);
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

	@Quando("o monitor tenta incluir a própria disponibilidade")
	public void incluirPropriaDisponibilidade() {
		try {
			servico.incluir(
					new Disponibilidade(
							Optional.empty(),
							"Cálculo I",
							new UsuarioIdentificador("1"),
							new CampusIdentificador("1"),
							1,
							LocalTime.of(10, 0),
							LocalTime.of(12, 0)));
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Então("a disponibilidade é incluída")
	public void verificarInclusao() {
		assertNull(excecaoCapturada);
		verify(repositorio).incluir(any(Disponibilidade.class));
	}

	@Quando("tento incluir disponibilidade em campus inativo")
	public void incluirEmCampusInativo() {
		when(campus.estaAtivo()).thenReturn(false);
		incluirPropriaDisponibilidade();
	}

	@Então("a inclusão da disponibilidade é recusada")
	public void verificarCampusInativo() {
		assertNotNull(excecaoCapturada);
		verify(repositorio, never()).incluir(any(Disponibilidade.class));
	}

	@Quando("tento incluir disponibilidade antes das 7 horas")
	public void incluirAntesDoFuncionamento() {
		incluir(novaDisponibilidade(1, LocalTime.of(6, 0), LocalTime.of(8, 0)));
	}

	@Quando("tento incluir horário que se sobrepõe a outro")
	public void incluirSobreposicao() {
		when(repositorio.listarDoMonitor(any(UsuarioIdentificador.class)))
				.thenReturn(List.of(disponibilidadeExistente(1, LocalTime.of(10, 0), LocalTime.of(12, 0), "2")));
		assertEquals(1, repositorio.listarDoMonitor(new UsuarioIdentificador("1")).size());
		incluir(novaDisponibilidade(1, LocalTime.of(11, 0), LocalTime.of(13, 0)));
	}

	@Quando("tento ultrapassar vinte horas semanais")
	public void ultrapassarCargaSemanal() {
		when(repositorio.listarDoMonitor(any(UsuarioIdentificador.class)))
				.thenReturn(
						List.of(
								disponibilidadeExistente(1, LocalTime.of(7, 0), LocalTime.of(11, 0), "2"),
								disponibilidadeExistente(2, LocalTime.of(7, 0), LocalTime.of(11, 0), "3"),
								disponibilidadeExistente(3, LocalTime.of(7, 0), LocalTime.of(11, 0), "4"),
								disponibilidadeExistente(4, LocalTime.of(7, 0), LocalTime.of(11, 0), "5"),
								disponibilidadeExistente(5, LocalTime.of(7, 0), LocalTime.of(11, 0), "6")));
		incluir(novaDisponibilidade(7, LocalTime.of(10, 0), LocalTime.of(12, 0)));
	}

	private Disponibilidade novaDisponibilidade(int dia, LocalTime inicio, LocalTime fim) {
		return new Disponibilidade(
				Optional.empty(),
				"Cálculo I",
				new UsuarioIdentificador("1"),
				new CampusIdentificador("1"),
				dia,
				inicio,
				fim);
	}

	private Disponibilidade disponibilidadeExistente(
			int dia, LocalTime inicio, LocalTime fim, String identificador) {
		return new Disponibilidade(
				Optional.of(new DisponibilidadeIdentificador(identificador)),
				"Cálculo I",
				new UsuarioIdentificador("1"),
				new CampusIdentificador("1"),
				dia,
				inicio,
				fim);
	}

	private void incluir(Disponibilidade candidata) {
		try {
			servico.incluir(candidata);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}
}
