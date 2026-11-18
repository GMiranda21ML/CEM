package br.com.cem.infraestrutura.monitoria.candidatura;

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
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.ISelecaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.SelecaoRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class CandidaturaRepositorioTest {
	@Autowired private ICandidaturaRepositorioJpa jpa;
	@Autowired private ISelecaoRepositorioJpa selecoesJpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;

	private CandidaturaRepositorio repositorio;
	private SelecaoIdentificador selecaoId;
	private DisciplinaIdentificador disciplinaId;
	private DisciplinaIdentificador outraDisciplinaId;
	private UsuarioIdentificador alunoId;

	@BeforeEach
	void configurar() {
		repositorio =
				new CandidaturaRepositorio(jpa, selecoesJpa, disciplinasJpa, usuariosJpa);
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
		var professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
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
										"Banco de Dados",
										new DisciplinaCodigo("BDD1001"),
										"Computação"))
						.getIdentificador()
						.orElseThrow();
		outraDisciplinaId =
				disciplinas
						.incluir(
								new Disciplina(
										Optional.empty(),
										new InstituicaoIdentificador("cem.edu.br"),
										new CursoIdentificador(curso.getIdentificador().toString()),
										professorId,
										"Redes",
										new DisciplinaCodigo("RED1002"),
										"Computação"))
						.getIdentificador()
						.orElseThrow();
		var selecoes = new SelecaoRepositorio(selecoesJpa, disciplinasJpa, usuariosJpa);
		LocalDate hoje = LocalDate.of(2026, 3, 1);
		selecaoId =
				selecoes
						.incluir(
								new Selecao(
										Optional.empty(),
										disciplinaId,
										professorId,
										"2026.1",
										hoje.plusDays(10),
										2,
										7.0,
										40,
										60,
										7.0,
										SelecaoSituacao.ABERTA,
										Optional.empty(),
										hoje,
										Optional.empty()))
						.getIdentificador()
						.orElseThrow();
	}

	@Test
	void incluiAprovaEVerificaConsultasDeVinculo() {
		LocalDateTime agora = LocalDateTime.of(2026, 3, 2, 14, 0);
		var cand =
				repositorio.incluir(
						new Candidatura(
								Optional.empty(),
								selecaoId,
								disciplinaId,
								alunoId,
								8.5,
								CandidaturaSituacao.PENDENTE,
								Optional.empty(),
								Optional.empty(),
								Optional.empty(),
								Optional.empty(),
								agora,
								Optional.empty()));
		CandidaturaIdentificador id = cand.getIdentificador().orElseThrow();
		assertTrue(repositorio.existePorSelecao(selecaoId));
		assertTrue(repositorio.existeAtivaPorAlunoEDisciplina(alunoId, disciplinaId));
		assertFalse(repositorio.ehMonitorAtivo(alunoId, disciplinaId));
		assertEquals(1, repositorio.listarPorSelecao(selecaoId).size());

		var entrevistada = cand.registrarEntrevista(9.0, Optional.of("Ótima"), 8.8);
		var aprovada =
				entrevistada.aprovar(8.8, agora.toLocalDate().plusDays(12), "Aprovada");
		repositorio.alterar(aprovada);

		assertTrue(repositorio.ehMonitorAtivo(alunoId, disciplinaId));
		assertTrue(repositorio.ehMonitorEmAlgumaDisciplina(alunoId));
		assertEquals(
				1,
				repositorio.contarMonitoriasAtivasNoSemestre(
						alunoId, "2026.1", outraDisciplinaId));
		assertEquals(
				0,
				repositorio.contarMonitoriasAtivasNoSemestre(alunoId, "2026.1", disciplinaId));
	}

	@Test
	void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new CandidaturaIdentificador("x")));
	}
}
