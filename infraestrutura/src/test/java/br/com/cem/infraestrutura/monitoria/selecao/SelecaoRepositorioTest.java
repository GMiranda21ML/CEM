package br.com.cem.infraestrutura.monitoria.selecao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
class SelecaoRepositorioTest {
	@Autowired private ISelecaoRepositorioJpa jpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;

	private SelecaoRepositorio repositorio;
	private DisciplinaIdentificador disciplinaId;
	private UsuarioIdentificador professorId;

	@BeforeEach
	void configurar() {
		repositorio = new SelecaoRepositorio(jpa, disciplinasJpa, usuariosJpa);
		var curso =
				cursosJpa.saveAndFlush(new CursoJpa(null, "cem.edu.br", "Computação", "CC"));
		var professor =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Helena Duarte",
								"helena@cem.edu.br",
								"Cem@2026!",
								Autorizacao.PROFESSOR,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
		var disciplinas =
				new DisciplinaRepositorio(disciplinasJpa, cursosJpa, usuariosJpa);
		var disciplina =
				disciplinas.incluir(
						new Disciplina(
								Optional.empty(),
								new InstituicaoIdentificador("cem.edu.br"),
								new CursoIdentificador(curso.getIdentificador().toString()),
								professorId,
								"Estrutura de Dados",
								new DisciplinaCodigo("EDS2001"),
								"Computação"));
		disciplinaId = disciplina.getIdentificador().orElseThrow();
	}

	@Test
	void incluiBuscaAlteraEVerificaAbertaPorDisciplina() {
		LocalDate hoje = LocalDate.of(2026, 3, 1);
		var incluida =
				repositorio.incluir(
						new Selecao(
								Optional.empty(),
								disciplinaId,
								professorId,
								"2026.1",
								hoje.plusDays(15),
								2,
								7.0,
								40,
								60,
								7.0,
								SelecaoSituacao.ABERTA,
								Optional.empty(),
								hoje,
								Optional.empty()));
		SelecaoIdentificador id = incluida.getIdentificador().orElseThrow();
		assertEquals("2026.1", repositorio.buscarPorIdentificador(id).getSemestre());
		assertTrue(repositorio.existeAbertaPorDisciplina(disciplinaId, Optional.empty()));
		assertFalse(repositorio.existeAbertaPorDisciplina(disciplinaId, Optional.of(id)));

		var encerrada = incluida.encerrar("Vagas preenchidas", hoje.plusDays(20));
		repositorio.alterar(encerrada);
		assertEquals(
				SelecaoSituacao.ENCERRADA,
				repositorio.buscarPorIdentificador(id).getSituacao());
		assertFalse(repositorio.existeAbertaPorDisciplina(disciplinaId, Optional.empty()));
	}

	@Test
	void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new SelecaoIdentificador("abc")));
	}
}
