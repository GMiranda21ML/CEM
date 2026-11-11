package br.com.cem.infraestrutura.monitoria.avaliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.avaliacao.Avaliacao;
import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

@DataJpaTest
class AvaliacaoRepositorioTest {
	@Autowired
	private IAvaliacaoRepositorioJpa repositorioJpa;

	@Autowired
	private IUsuarioRepositorioJpa usuarioRepositorioJpa;

	private AvaliacaoRepositorio repositorio;
	private UsuarioRepositorio usuarioRepositorio;

	@BeforeEach
	void configurar() {
		repositorio = new AvaliacaoRepositorio(repositorioJpa, usuarioRepositorioJpa);
		usuarioRepositorio = new UsuarioRepositorio(usuarioRepositorioJpa);
	}

	@Test
	void incluiBuscaAlteraEVerificaDuplicidade() {
		var autor = incluirUsuario();
		var sessao = new SessaoIdentificador("S1");
		var agora = LocalDateTime.of(2026, 11, 11, 12, 0);
		var incluida = repositorio.incluir(new Avaliacao(Optional.empty(), sessao,
				autor.getIdentificador().orElseThrow(), AvaliacaoPerfil.ALUNO, 5, "Excelente", agora, agora));

		assertTrue(repositorio.existePorSessaoEAutor(sessao, autor.getIdentificador().orElseThrow()));
		var encontrada = repositorio.buscarPorIdentificador(incluida.getIdentificador().orElseThrow());
		assertEquals(5, encontrada.getNota());

		repositorio.alterar(encontrada.editar(4, "Muito bom", agora.plusHours(1)));
		assertEquals(4, repositorio.buscarPorIdentificador(incluida.getIdentificador().orElseThrow()).getNota());
	}

	@Test
	void participanteSemAvaliacaoNaoPossuiDuplicidade() {
		var autor = incluirUsuario();
		assertFalse(repositorio.existePorSessaoEAutor(new SessaoIdentificador("S2"),
				autor.getIdentificador().orElseThrow()));
	}

	private Usuario incluirUsuario() {
		return usuarioRepositorio.incluir(new Usuario(Optional.empty(), "Ana",
				new UsuarioEmail("ana@aluno.cem.edu.br"), new UsuarioSenha("Cem@2026!"), Autorizacao.ALUNO,
				"Computação", null, UsuarioSituacao.ATIVO));
	}
}
