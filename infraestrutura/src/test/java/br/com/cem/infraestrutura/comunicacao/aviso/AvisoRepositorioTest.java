package br.com.cem.infraestrutura.comunicacao.aviso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.SessaoRepositorio;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class AvisoRepositorioTest {
	@Test
	void monitorDesignadoPublicaMasAlunoSemMonitoriaNaoPublica() {
		var seguranca = mock(dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico.class);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(seguranca.getUsuario()).thenReturn("monitor@aluno.cem.edu.br");
		var relogio = mock(br.com.cem.dominio.spi.IRelogioServico.class);
		when(relogio.agora()).thenReturn(agora);
		var monitores =
				new MonitorAvisoServico(new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa));
		var servico =
				new br.com.cem.dominio.comunicacao.aviso.AvisoServico(
						seguranca,
						new AvisoRepositorio(jpa),
						relogio,
						new UsuarioRepositorio(usuariosJpa),
						monitores);
		var aviso =
				servico.publicar("Aviso do monitor", "Encontro confirmado", agora.toLocalDate());
		assertTrue(aviso.getIdentificador().isPresent());
		when(seguranca.getUsuario()).thenReturn("aluno@aluno.cem.edu.br");
		assertThrows(
				dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao.class,
				() -> servico.publicar("Aviso do aluno", "Não autorizado", agora.toLocalDate()));
	}

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
	void persisteEBuscaComReferencias() {
		var repo = new AvisoRepositorio(jpa);
		var a =
				repo.incluir(
						new Aviso(
								Optional.empty(),
								"Encontro de monitoria",
								"Confirme sua presença",
								agora.toLocalDate(),
								true,
								agora));
		var id = a.getIdentificador().orElseThrow();
		assertTrue(repo.existePorIdentificador(id));
		assertEquals(a.getTitulo(), repo.buscarPorIdentificador(id).getTitulo());
		assertFalse(repo.existePorIdentificador(new AvisoIdentificador("invalido")));
	}
}
