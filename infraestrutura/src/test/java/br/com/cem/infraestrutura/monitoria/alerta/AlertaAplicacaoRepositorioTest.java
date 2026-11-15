package br.com.cem.infraestrutura.monitoria.alerta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.aplicacao.monitoria.alerta.AlertaConsulta;
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
import br.com.cem.dominio.monitoria.alerta.Alerta;
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
class AlertaAplicacaoRepositorioTest {
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	@Autowired private IAlertaRepositorioJpa jpa;
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
								"Computação",
								null,
								UsuarioSituacao.ATIVO))
				.getIdentificador()
				.orElseThrow();
	}

	@Test
	void pesquisaComEscopoEPaginacao() {
		var repo = new AlertaRepositorio(jpa, usuariosJpa);
		repo.incluir(new Alerta(Optional.empty(), aluno, "ALUNO", "Cálculo I", false, agora));
		var seguranca = mock(ISegurancaServico.class);
		var coordenador = cadastrar("coord-alerta", " COMPUTAÇÃO ", "cem.edu.br", UsuarioSituacao.ATIVO);
		when(seguranca.getUsuario()).thenReturn(coordenador.getEmailInstitucional().toString());
		when(seguranca.possuiAutorizacao("COORDENADOR")).thenReturn(true);
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(agora);
		var aplicacao =
				new AlertaAplicacaoRepositorio(
						jpa, seguranca, new UsuarioRepositorio(usuariosJpa), relogio);
		assertEquals(
				1,
				aplicacao
						.pesquisar(
								new AlertaConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.of(100),
										Optional.empty()))
						.getTotal());
		when(seguranca.possuiAutorizacao("COORDENADOR")).thenReturn(false);
		assertEquals(
				0,
				aplicacao
						.pesquisar(
								new AlertaConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.of(100),
										Optional.empty()))
						.getTotal());
	}

	private Usuario cadastrar(String nome, String curso, String dominio, UsuarioSituacao situacao) {
		return new UsuarioRepositorio(usuariosJpa).incluir(new Usuario(Optional.empty(), nome,
				new UsuarioEmail(nome + "@" + dominio), new UsuarioSenha("Cem@2026!"),
				Autorizacao.COORDENADOR, curso, null, situacao));
	}

	private AlertaConsulta consulta() {
		return new AlertaConsulta(Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.of(100), Optional.empty());
	}

	@Test
	void selecionaSomenteCoordenadoresAtivosDoCursoEDaInstituicao() {
		var esperado = cadastrar("coord-correto", " computação ", "cem.edu.br", UsuarioSituacao.ATIVO);
		cadastrar("coord-outro-curso", "Medicina", "cem.edu.br", UsuarioSituacao.ATIVO);
		cadastrar("coord-outra-instituicao", "Computação", "outra.edu.br", UsuarioSituacao.ATIVO);
		cadastrar("coord-inativo", "Computação", "cem.edu.br", UsuarioSituacao.INATIVO);
		cadastrar("coord-dominio-parecido", "Computação", "em.edu.br", UsuarioSituacao.ATIVO);
		var usuarios = new UsuarioRepositorio(usuariosJpa);
		var servico = new CoordenadoresAlertaServico(usuarios, usuariosJpa);
		assertEquals(List.of(esperado.getIdentificador().orElseThrow()),
				servico.buscarPorPessoa(aluno).stream()
						.map(u -> u.getIdentificador().orElseThrow()).toList());
		var semCurso = usuarios.incluir(new Usuario(Optional.empty(), "Sem curso",
				new UsuarioEmail("sem-curso@aluno.cem.edu.br"), new UsuarioSenha("Cem@2026!"),
				Autorizacao.ALUNO, null, null, UsuarioSituacao.ATIVO));
		assertTrue(servico.buscarPorPessoa(semCurso.getIdentificador().orElseThrow()).isEmpty());
	}

	@Test
	void coordenadorNaoListaAlertasDeOutroCursoOuInstituicao() {
		var usuarios = new UsuarioRepositorio(usuariosJpa);
		var repo = new AlertaRepositorio(jpa, usuariosJpa);
		repo.incluir(new Alerta(Optional.empty(), aluno, "ALUNO", "Cálculo I", false, agora));
		var outroCurso = usuarios.incluir(new Usuario(Optional.empty(), "Outro curso",
				new UsuarioEmail("outro-curso@aluno.cem.edu.br"), new UsuarioSenha("Cem@2026!"),
				Autorizacao.ALUNO, "Medicina", null, UsuarioSituacao.ATIVO));
		var outraInstituicao = usuarios.incluir(new Usuario(Optional.empty(), "Outra instituição",
				new UsuarioEmail("outro@aluno.evilcem.edu.br"), new UsuarioSenha("Cem@2026!"),
				Autorizacao.ALUNO, "Computação", null, UsuarioSituacao.ATIVO));
		for (var pessoa : List.of(outroCurso, outraInstituicao))
			repo.incluir(new Alerta(Optional.empty(), pessoa.getIdentificador().orElseThrow(),
					"ALUNO", "Cálculo I", false, agora));
		var coordenador = cadastrar("coord-escopo", "Computação", "cem.edu.br", UsuarioSituacao.ATIVO);
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.possuiAutorizacao("COORDENADOR")).thenReturn(true);
		when(seguranca.getUsuario()).thenReturn(coordenador.getEmailInstitucional().toString());
		var aplicacao = new AlertaAplicacaoRepositorio(jpa, seguranca, usuarios, mock(IRelogioServico.class));
		assertEquals(1, aplicacao.pesquisar(consulta()).getTotal());
		var inativo = cadastrar("coord-sem-acesso", "Computação", "cem.edu.br", UsuarioSituacao.INATIVO);
		when(seguranca.getUsuario()).thenReturn(inativo.getEmailInstitucional().toString());
		assertEquals(0, aplicacao.pesquisar(consulta()).getTotal());
	}
}
