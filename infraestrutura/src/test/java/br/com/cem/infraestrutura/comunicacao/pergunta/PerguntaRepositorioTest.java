package br.com.cem.infraestrutura.comunicacao.pergunta;

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
import br.com.cem.dominio.comunicacao.pergunta.Pergunta;
import br.com.cem.dominio.comunicacao.pergunta.PerguntaIdentificador;
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
class PerguntaRepositorioTest {
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	@Autowired private IPerguntaRepositorioJpa jpa;
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
		var repo = new PerguntaRepositorio(jpa, usuariosJpa);
		var a =
				repo.incluir(
						new Pergunta(
								Optional.empty(),
								aluno,
								"Como confirmar a presença?",
								"PENDENTE",
								Optional.empty(),
								agora));
		var id = a.getIdentificador().orElseThrow();
		assertTrue(repo.existePorIdentificador(id));
		assertEquals(a.getTexto(), repo.buscarPorIdentificador(id).getTexto());
		assertFalse(repo.existePorIdentificador(new PerguntaIdentificador("invalido")));
	}
}
