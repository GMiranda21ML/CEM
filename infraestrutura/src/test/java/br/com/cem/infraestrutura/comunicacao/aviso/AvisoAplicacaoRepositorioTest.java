package br.com.cem.infraestrutura.comunicacao.aviso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.aplicacao.comunicacao.aviso.AvisoConsulta;
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
import br.com.cem.dominio.comunicacao.aviso.Aviso;
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
class AvisoAplicacaoRepositorioTest {
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	@Autowired private IAvisoRepositorioJpa jpa;
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
		var repo = new AvisoRepositorio(jpa);
		var totalAnterior = jpa.count();
		repo.incluir(
				new Aviso(
						Optional.empty(),
						"Encontro de monitoria",
						"Confirme sua presença",
						agora.toLocalDate(),
						true,
						agora));
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("aluno@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao("COORDENADOR")).thenReturn(true);
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(agora);
		var aplicacao =
				new AvisoAplicacaoRepositorio(
						jpa, seguranca, new UsuarioRepositorio(usuariosJpa), relogio);
		assertEquals(
				totalAnterior + 1,
				aplicacao
						.pesquisar(
								new AvisoConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.of(100),
										Optional.empty()))
						.getTotal());
		repo.incluir(
				new Aviso(
						Optional.empty(),
						"Expirado",
						"Texto",
						agora.toLocalDate().minusDays(1),
						true,
						agora));
		when(seguranca.possuiAutorizacao("COORDENADOR")).thenReturn(false);
		var quantidadeVisivel = jpa.findAll().stream()
				.filter(a -> a.getAtivo() && !a.getValidade().isBefore(agora.toLocalDate())).count();
		assertEquals(
				quantidadeVisivel,
				aplicacao
						.pesquisar(
								new AvisoConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.of(100),
										Optional.empty()))
						.getTotal());
	}
}
