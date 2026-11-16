package br.com.cem.infraestrutura.acesso.curso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;

@DataJpaTest
class CursoRepositorioTest {
	@Autowired private ICursoRepositorioJpa jpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	private CursoRepositorio repositorio;
	@BeforeEach void configurar() { repositorio = new CursoRepositorio(jpa); }

	@Test void incluiPesquisaEVerificaUnicidadeSemDiferenciarMaiusculas() {
		var instituicao = new InstituicaoIdentificador("cem.edu.br");
		var curso = repositorio.incluir(new Curso(Optional.empty(), instituicao, "Sistemas de Informação", "SI"));
		assertTrue(repositorio.existePorNome("sistemas DE informação", instituicao, Optional.empty()));
		assertTrue(repositorio.existePorSigla("si", instituicao, Optional.empty()));
		assertFalse(repositorio.existePorNome("Sistemas de Informação", new InstituicaoIdentificador("outra.edu.br"), Optional.empty()));
		assertFalse(repositorio.existePorNome("Sistemas de Informação", instituicao, curso.getIdentificador()));
	}

	@Test void pesquisaContabilizaUsuariosDaInstituicaoNoCurso() {
		jpa.saveAndFlush(new CursoJpa(null, "cem.edu.br", "Computação", "CC"));
		usuariosJpa.saveAndFlush(new UsuarioJpa(null, "Ana", "ana@cem.edu.br", "senha", Autorizacao.ALUNO,
				"Computação", null, UsuarioSituacao.ATIVO));
		usuariosJpa.saveAndFlush(new UsuarioJpa(null, "Bruno", "bruno@outra.edu.br", "senha", Autorizacao.ALUNO,
				"Computação", null, UsuarioSituacao.ATIVO));
		var page = jpa.pesquisar("cem.edu.br", null, PageRequest.of(0, 10));
		assertEquals(1, page.getTotalElements());
		assertEquals(1, page.getContent().getFirst().getQuantidadeUsuarios());
		assertEquals(0, page.getContent().getFirst().getQuantidadeDisciplinas());
	}

	@Test void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new CursoIdentificador("abc")));
	}
}
