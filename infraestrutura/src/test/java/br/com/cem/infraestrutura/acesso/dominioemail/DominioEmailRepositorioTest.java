package br.com.cem.infraestrutura.acesso.dominioemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;

@DataJpaTest
class DominioEmailRepositorioTest {
	@Autowired
	private IDominioEmailRepositorioJpa repositorioJpa;

	private DominioEmailRepositorio repositorio;

	@BeforeEach
	void configurar() {
		repositorio = new DominioEmailRepositorio(repositorioJpa);
	}

	@Test
	void incluiEBuscaPorIdentificador() {
		repositorio.incluir(new DominioEmail(new DominioEmailDominio("aluno.cem.edu.br")));

		var dominioEmail = repositorio.buscarPorIdentificador(new DominioEmailDominio("aluno.cem.edu.br"));

		assertEquals(new DominioEmailDominio("aluno.cem.edu.br"), dominioEmail.getDominio());
	}

	@Test
	void buscaPorIdentificadorInexistenteLancaExcecao() {
		assertThrows(AgregadoInexistenteExcecao.class,
				() -> repositorio.buscarPorIdentificador(new DominioEmailDominio("aluno.cem.edu.br")));
	}

	@Test
	void existePorIdentificador() {
		repositorio.incluir(new DominioEmail(new DominioEmailDominio("aluno.cem.edu.br")));

		assertTrue(repositorio.existePorIdentificador(new DominioEmailDominio("aluno.cem.edu.br")));
		assertFalse(repositorio.existePorIdentificador(new DominioEmailDominio("pos.cem.edu.br")));
	}

	@Test
	void exclui() {
		repositorio.incluir(new DominioEmail(new DominioEmailDominio("aluno.cem.edu.br")));

		repositorio.excluir(new DominioEmailDominio("aluno.cem.edu.br"));

		assertFalse(repositorio.existePorIdentificador(new DominioEmailDominio("aluno.cem.edu.br")));
	}
}
