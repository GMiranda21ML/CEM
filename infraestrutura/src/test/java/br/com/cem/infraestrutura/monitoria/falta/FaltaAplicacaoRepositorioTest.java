package br.com.cem.infraestrutura.monitoria.falta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.aplicacao.monitoria.falta.FaltaConsulta;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.falta.Falta;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.spi.IRelogioServico;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.SessaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class FaltaAplicacaoRepositorioTest {
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	@Autowired private IFaltaRepositorioJpa jpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	@Autowired private ICampusRepositorioJpa campusJpa;
	@Autowired private ISessaoRepositorioJpa sessoesJpa;
	private UsuarioIdentificador aluno, monitor, professor;
	private CampusIdentificador campus;
	private Sessao sessao;

	@BeforeEach
	void configurar() {
		var usuarios = new UsuarioRepositorio(usuariosJpa);
		aluno = usuario(usuarios, "Aluno", Autorizacao.ALUNO);
		monitor = usuario(usuarios, "Monitor", Autorizacao.ALUNO);
		professor = usuario(usuarios, "Professor", Autorizacao.PROFESSOR);
		var c =
				new CampusRepositorio(campusJpa)
						.incluir(
								new Campus(
										Optional.empty(),
										new InstituicaoIdentificador("cem.edu.br"),
										"Central",
										null,
										CampusSituacao.ATIVO));
		campus = c.getIdentificador().orElseThrow();
		var repositorioSessao = new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa);
		sessao =
				repositorioSessao.incluir(
						new Sessao(
								Optional.empty(),
								"Cálculo I",
								professor,
								monitor,
								campus,
								List.of(aluno),
								agora.minusDays(1),
								agora.minusDays(1).plusHours(1),
								false));
	}

	private UsuarioIdentificador usuario(UsuarioRepositorio repo, String nome, Autorizacao papel) {
		return repo.incluir(
						new Usuario(
								Optional.empty(),
								nome,
								new UsuarioEmail(nome.toLowerCase() + "@aluno.cem.edu.br"),
								new UsuarioSenha("Cem@2026!"),
								papel,
								null,
								null,
								UsuarioSituacao.ATIVO))
				.getIdentificador()
				.orElseThrow();
	}

	@Test
	void pesquisaComEscopoEPaginacao() {
		var repo = new FaltaRepositorio(jpa, sessoesJpa, usuariosJpa);
		var falta =
				repo.incluir(
						new Falta(
								Optional.empty(),
								sessao.getIdentificador().orElseThrow(),
								aluno,
								"ALUNO",
								monitor,
								Optional.empty(),
								agora,
								Optional.empty()));
		assertTrue(
				repo.buscarPorIdentificador(falta.getIdentificador().orElseThrow())
						.getReposicao()
						.isEmpty());
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("monitor@aluno.cem.edu.br");
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(agora);
		var aplicacao =
				new FaltaAplicacaoRepositorio(
						jpa, seguranca, new UsuarioRepositorio(usuariosJpa), relogio);
		assertEquals(
				1,
				aplicacao
						.pesquisar(
								new FaltaConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.of(100),
										Optional.empty()))
						.getTotal());
		for (String texto : List.of("", "   ")) {
			var resultado = aplicacao.pesquisar(new FaltaConsulta(
					Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(100),
					Optional.of(texto)));
			assertEquals(1, resultado.getTotal(), "Busca vazia deve incluir faltas sem observação");
			assertEquals(falta.getIdentificador().orElseThrow(), resultado.getProjecoes().getFirst().identificador());
			assertTrue(resultado.getProjecoes().getFirst().observacao().isEmpty());
		}
		assertEquals(0, aplicacao.pesquisar(new FaltaConsulta(
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(100),
				Optional.of("ausência"))).getTotal());
		when(seguranca.getUsuario()).thenReturn("aluno@aluno.cem.edu.br");
		assertEquals(0, aplicacao.pesquisar(new FaltaConsulta(
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(100),
				Optional.of(""))).getTotal(), "Busca vazia não amplia a autorização do aluno");
	}
}
