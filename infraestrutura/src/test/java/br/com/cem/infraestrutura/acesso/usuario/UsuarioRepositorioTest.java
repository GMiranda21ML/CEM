package br.com.cem.infraestrutura.acesso.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;

@DataJpaTest
class UsuarioRepositorioTest {
	private static final String SENHA_FORTE = "Cem@2026!";

	@Autowired
	private IUsuarioRepositorioJpa repositorioJpa;

	private UsuarioRepositorio repositorio;

	@BeforeEach
	void configurar() {
		repositorio = new UsuarioRepositorio(repositorioJpa);
	}

	private static Usuario criar(String email, String nome) {
		return new Usuario(Optional.empty(), nome, new UsuarioEmail(email), new UsuarioSenha(SENHA_FORTE),
				Autorizacao.ALUNO, "Ciência da Computação", null, UsuarioSituacao.ATIVO);
	}

	@Test
	void incluiEBuscaPorIdentificador() {
		var incluido = repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		var usuario = repositorio.buscarPorIdentificador(incluido.getIdentificador().orElseThrow());

		assertEquals("Lucas Ferreira", usuario.getNome());
		assertEquals(new UsuarioEmail("lucas.ferreira@aluno.cem.edu.br"), usuario.getEmailInstitucional());
		assertEquals(Autorizacao.ALUNO, usuario.getPapel());
		assertTrue(usuario.estaAtivo());
	}

	@Test
	void buscaPorIdentificadorInexistenteLancaExcecao() {
		assertThrows(AgregadoInexistenteExcecao.class,
				() -> repositorio.buscarPorIdentificador(new UsuarioIdentificador("999")));
	}

	@Test
	void existePorIdentificadorComIdentificadorNaoNumerico() {
		assertFalse(repositorio.existePorIdentificador(new UsuarioIdentificador("abc")));
	}

	@Test
	void altera() {
		var incluido = repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		var alterado = new Usuario(incluido.getIdentificador(), "Lucas Ferreira Souza", incluido.getEmailInstitucional(),
				incluido.getSenha(), incluido.getPapel(), "Engenharia Civil", "Campus Leste", incluido.getSituacao());
		repositorio.alterar(alterado);

		var usuario = repositorio.buscarPorIdentificador(incluido.getIdentificador().orElseThrow());
		assertEquals("Lucas Ferreira Souza", usuario.getNome());
		assertEquals("Engenharia Civil", usuario.getCurso());
		assertEquals("Campus Leste", usuario.getCampus());
	}

	@Test
	void exclui() {
		var incluido = repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		repositorio.excluir(incluido.getIdentificador().orElseThrow());

		assertFalse(repositorio.existePorIdentificador(incluido.getIdentificador().orElseThrow()));
	}

	@Test
	void existePorEmail() {
		repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		assertTrue(repositorio.existePorEmail(new UsuarioEmail("lucas.ferreira@aluno.cem.edu.br"), Optional.empty()));
		assertFalse(repositorio.existePorEmail(new UsuarioEmail("outro@aluno.cem.edu.br"), Optional.empty()));
	}

	@Test
	void existePorEmailIgnorandoOProprioIdentificador() {
		var incluido = repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		var ignorado = incluido.getIdentificador();

		assertFalse(repositorio.existePorEmail(new UsuarioEmail("lucas.ferreira@aluno.cem.edu.br"), ignorado));
	}

	@Test
	void buscaPorEmail() {
		repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira"));

		var usuario = repositorio.buscarPorEmail(new UsuarioEmail("lucas.ferreira@aluno.cem.edu.br"));

		assertEquals("Lucas Ferreira", usuario.getNome());
	}

	@Test
	void buscaPorEmailInexistenteLancaExcecao() {
		assertThrows(AgregadoInexistenteExcecao.class,
				() -> repositorio.buscarPorEmail(new UsuarioEmail("inexistente@aluno.cem.edu.br")));
	}
}
