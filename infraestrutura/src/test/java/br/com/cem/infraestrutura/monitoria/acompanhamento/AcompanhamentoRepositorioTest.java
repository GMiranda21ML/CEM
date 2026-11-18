package br.com.cem.infraestrutura.monitoria.acompanhamento;

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
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao;
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
class AcompanhamentoRepositorioTest {
	@Autowired private IAcompanhamentoRepositorioJpa jpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;

	private AcompanhamentoRepositorio repositorio;
	private UsuarioIdentificador alunoId;
	private UsuarioIdentificador monitorId;
	private UsuarioIdentificador professorId;
	private DisciplinaIdentificador disciplinaId;

	@BeforeEach
	void configurar() {
		repositorio = new AcompanhamentoRepositorio(jpa, disciplinasJpa, usuariosJpa);
		var curso =
				cursosJpa.saveAndFlush(new CursoJpa(null, "cem.edu.br", "Computação", "CC"));
		var professor =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Prof",
								"prof@cem.edu.br",
								"Cem@2026!",
								Autorizacao.PROFESSOR,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		var monitor =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Monitor",
								"monitor@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		var aluno =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Aluno",
								"aluno@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
		monitorId = new UsuarioIdentificador(monitor.getIdentificador().toString());
		alunoId = new UsuarioIdentificador(aluno.getIdentificador().toString());
		var disciplinas =
				new DisciplinaRepositorio(disciplinasJpa, cursosJpa, usuariosJpa);
		disciplinaId =
				disciplinas
						.incluir(
								new Disciplina(
										Optional.empty(),
										new InstituicaoIdentificador("cem.edu.br"),
										new CursoIdentificador(curso.getIdentificador().toString()),
										professorId,
										"Cálculo I",
										new DisciplinaCodigo("MAT1001"),
										"Matemática"))
						.getIdentificador()
						.orElseThrow();
	}

	@Test
	void incluiEncerraEVerificaBloqueioPorEncerramento() {
		LocalDate hoje = LocalDate.of(2026, 4, 1);
		var acomp =
				repositorio.incluir(
						new Acompanhamento(
								Optional.empty(),
								alunoId,
								disciplinaId,
								monitorId,
								"2026.1",
								7.5,
								75,
								AcompanhamentoSituacao.ATIVO,
								hoje,
								Optional.empty(),
								Optional.empty()));
		AcompanhamentoIdentificador id = acomp.getIdentificador().orElseThrow();
		assertEquals(7.5, repositorio.buscarPorIdentificador(id).getMetaNota());
		assertTrue(repositorio.existeAtivoPorAlunoEDisciplina(alunoId, disciplinaId));
		assertFalse(repositorio.existeEncerradoPorAlunoEDisciplina(alunoId, disciplinaId));
		assertFalse(repositorio.existeBloqueioPorEncerramento(alunoId, "Cálculo I", professorId));

		var encerrado =
				acomp.encerrar(
						"Aluno concluiu todas as metas propostas no plano.", hoje.plusDays(30));
		repositorio.alterar(encerrado);
		assertFalse(repositorio.existeAtivoPorAlunoEDisciplina(alunoId, disciplinaId));
		assertTrue(repositorio.existeEncerradoPorAlunoEDisciplina(alunoId, disciplinaId));
		assertTrue(repositorio.existeBloqueioPorEncerramento(alunoId, "Cálculo I", professorId));
		assertTrue(repositorio.existeBloqueioPorEncerramento(alunoId, "MAT1001", professorId));
	}

	@Test
	void identificadorNaoNumericoNaoExiste() {
		assertFalse(
				repositorio.existePorIdentificador(new AcompanhamentoIdentificador("nao-num")));
	}
}
