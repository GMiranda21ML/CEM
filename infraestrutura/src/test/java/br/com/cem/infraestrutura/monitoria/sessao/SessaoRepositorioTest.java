package br.com.cem.infraestrutura.monitoria.sessao;

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
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.sessao.RespostaSessao;
import br.com.cem.dominio.monitoria.sessao.SituacaoRespostaSessao;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class SessaoRepositorioTest {
	@Test
	void carregaParticipantesComAConsultaDaSessao() {
		gerenciadorEntidades.flush();
		gerenciadorEntidades.clear();
		var entidade =
				sessoesJpa
						.findById(Long.valueOf(sessao.getIdentificador().orElseThrow().toString()))
						.orElseThrow();
		assertTrue(org.hibernate.Hibernate.isInitialized(entidade.getAlunos()));
		var repo = new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa);
		var encontrada = repo.buscarPorIdentificador(sessao.getIdentificador().orElseThrow());
		assertEquals(List.of(aluno), encontrada.getAlunos());
	}

	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	@Autowired private ISessaoRepositorioJpa jpa;
	@Autowired private jakarta.persistence.EntityManager gerenciadorEntidades;
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
						Sessao.restaurar(
								Optional.empty(),
								"Cálculo I",
								professor,
								monitor,
								campus,
								"Sala 12",
								List.of(aluno),
								List.of(new RespostaSessao(aluno, SituacaoRespostaSessao.NAO_VAI_PARTICIPAR, Optional.of("Imprevisto"), Optional.of(agora.plusDays(2)))),
								agora.minusDays(1),
								agora.minusDays(1).plusHours(1),
								Optional.of(agora.minusDays(2)),
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
		assertTrue(
				new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa)
						.existePorMonitor(monitor));
		assertFalse(
				new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa).existePorMonitor(aluno));
		var repo = new SessaoRepositorio(jpa, usuariosJpa, campusJpa);
		var a = sessao;
		var id = a.getIdentificador().orElseThrow();
		assertTrue(repo.existePorIdentificador(id));
		assertEquals(a.getDisciplina(), repo.buscarPorIdentificador(id).getDisciplina());
		var persistida = repo.buscarPorIdentificador(id);
		assertEquals("Sala 12", persistida.getSala());
		assertEquals(Optional.of(agora.minusDays(2)), persistida.getInicioAnterior());
		assertEquals(SituacaoRespostaSessao.NAO_VAI_PARTICIPAR, persistida.getRespostas().iterator().next().getSituacao());
		assertEquals(Optional.of("Imprevisto"), persistida.getRespostas().iterator().next().getMotivo());
		assertEquals(Optional.of(agora.plusDays(2)), persistida.getRespostas().iterator().next().getInicioSugerido());
		assertFalse(repo.existePorIdentificador(new SessaoIdentificador("invalido")));
		assertTrue(repo.conflita(monitor, aluno, a.getInicio(), a.getFim()));
		assertFalse(repo.conflita(monitor, aluno, a.getFim(), a.getFim().plusHours(1)));
	}
}
