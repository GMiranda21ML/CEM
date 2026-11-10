package br.com.cem.infraestrutura.acesso.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.cem.aplicacao.acesso.usuario.UsuarioConsulta;
import br.com.cem.aplicacao.acesso.usuario.UsuarioOrdenacao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

@DataJpaTest
class UsuarioAplicacaoRepositorioTest {
	private static final String SENHA_FORTE = "Cem@2026!";

	@Autowired
	private IUsuarioRepositorioJpa repositorioJpa;

	private UsuarioRepositorio repositorio;
	private UsuarioAplicacaoRepositorio repositorioAplicacao;

	@BeforeEach
	void configurar() {
		repositorio = new UsuarioRepositorio(repositorioJpa);
		repositorioAplicacao = new UsuarioAplicacaoRepositorio(repositorioJpa);
	}

	private static Usuario criar(String email, String nome, Autorizacao papel) {
		return new Usuario(Optional.empty(), nome, new UsuarioEmail(email), new UsuarioSenha(SENHA_FORTE), papel,
				"Ciência da Computação", null, UsuarioSituacao.ATIVO);
	}

	private static UsuarioConsulta consultar(Optional<String> nome, Optional<Autorizacao> papel) {
		return new UsuarioConsulta(Optional.of(UsuarioOrdenacao.NOME), Optional.empty(), Optional.empty(),
				Optional.empty(), nome, papel, Optional.empty());
	}

	@Test
	void pesquisaRetornaOsUsuarios() {
		repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira", Autorizacao.ALUNO));
		repositorio.incluir(criar("rodrigo.alves@cem.edu.br", "Rodrigo Alves", Autorizacao.PROFESSOR));

		var resultado = repositorioAplicacao.pesquisar(consultar(Optional.empty(), Optional.empty()));

		assertEquals(2, resultado.getTotal());
		assertEquals("Lucas Ferreira", resultado.getProjecoes().get(0).getNome());
		assertEquals("Rodrigo Alves", resultado.getProjecoes().get(1).getNome());
	}

	@Test
	void pesquisaFiltrandoPorTrechoDoNome() {
		repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira", Autorizacao.ALUNO));
		repositorio.incluir(criar("rodrigo.alves@cem.edu.br", "Rodrigo Alves", Autorizacao.PROFESSOR));

		var resultado = repositorioAplicacao.pesquisar(consultar(Optional.of("lucas"), Optional.empty()));

		assertEquals(1, resultado.getTotal());
		assertEquals("Lucas Ferreira", resultado.getProjecoes().get(0).getNome());
	}

	@Test
	void pesquisaFiltrandoPorPapel() {
		repositorio.incluir(criar("lucas.ferreira@aluno.cem.edu.br", "Lucas Ferreira", Autorizacao.ALUNO));
		repositorio.incluir(criar("rodrigo.alves@cem.edu.br", "Rodrigo Alves", Autorizacao.PROFESSOR));

		var resultado = repositorioAplicacao.pesquisar(consultar(Optional.empty(), Optional.of(Autorizacao.PROFESSOR)));

		assertEquals(1, resultado.getTotal());
		assertEquals("Rodrigo Alves", resultado.getProjecoes().get(0).getNome());
	}
}
