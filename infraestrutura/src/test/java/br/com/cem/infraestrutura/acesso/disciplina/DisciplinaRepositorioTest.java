package br.com.cem.infraestrutura.acesso.disciplina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

@DataJpaTest
class DisciplinaRepositorioTest {
	@Autowired private IDisciplinaRepositorioJpa jpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	private DisciplinaRepositorio repositorio;
	private CursoJpa curso;
	private UsuarioJpa professor;

	@BeforeEach void configurar() {
		repositorio = new DisciplinaRepositorio(jpa, cursosJpa, usuariosJpa);
		curso = cursosJpa.saveAndFlush(new CursoJpa(null, "cem.edu.br", "Computação", "CC"));
		professor = usuariosJpa.saveAndFlush(new UsuarioJpa(null, "Helena Duarte", "helena@cem.edu.br", "senha",
				Autorizacao.PROFESSOR, "Computação", null, UsuarioSituacao.ATIVO));
	}

	@Test void incluiBuscaEVerificaChavesUnicas() {
		var disciplina = repositorio.incluir(novaDisciplina("Estatística", "EST1001", "Matemática"));
		var id = disciplina.getIdentificador().orElseThrow();
		assertEquals("Estatística", repositorio.buscarPorIdentificador(id).getNome());
		assertTrue(repositorio.existePorCodigo(new DisciplinaCodigo("EST1001"),
				new InstituicaoIdentificador("cem.edu.br"), Optional.empty()));
		assertTrue(repositorio.existePorNomeNoCurso("estatística", new CursoIdentificador(curso.getIdentificador().toString()), Optional.empty()));
		assertFalse(repositorio.existePorCodigo(new DisciplinaCodigo("EST1001"),
				new InstituicaoIdentificador("cem.edu.br"), Optional.of(id)));
	}

	@Test void listaComFiltroEOpcoesDeProfessorDoCurso() {
		repositorio.incluir(novaDisciplina("Estatística", "EST1001", "Matemática"));
		var page = jpa.pesquisar("cem.edu.br", "%matemática%", curso.getIdentificador(), PageRequest.of(0, 10));
		assertEquals(1, page.getTotalElements());
		assertEquals("Computação", page.getContent().getFirst().getCursoNome());
		assertEquals(1, jpa.buscarProfessores(curso.getIdentificador(), "cem.edu.br").size());
		assertEquals("Helena Duarte", jpa.buscarProfessores(curso.getIdentificador(), "cem.edu.br").getFirst().nome());
	}

	@Test void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new DisciplinaIdentificador("abc")));
	}

	private Disciplina novaDisciplina(String nome, String codigo, String area) {
		return new Disciplina(Optional.empty(), new InstituicaoIdentificador("cem.edu.br"),
				new CursoIdentificador(curso.getIdentificador().toString()),
				new UsuarioIdentificador(professor.getIdentificador().toString()), nome, new DisciplinaCodigo(codigo), area);
	}
}
