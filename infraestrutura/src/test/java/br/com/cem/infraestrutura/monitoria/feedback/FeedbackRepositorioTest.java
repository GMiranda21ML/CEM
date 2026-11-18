package br.com.cem.infraestrutura.monitoria.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.feedback.FeedbackIdentificador;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.SessaoRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class FeedbackRepositorioTest {
	@Autowired private IFeedbackRepositorioJpa jpa;
	@Autowired private ISessaoRepositorioJpa sessoesJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	@Autowired private ICampusRepositorioJpa campusJpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;

	private FeedbackRepositorio repositorio;
	private SessaoIdentificador sessaoId;
	private UsuarioIdentificador alunoId;
	private UsuarioIdentificador monitorId;
	private DisciplinaIdentificador disciplinaId;

	@BeforeEach
	void configurar() {
		repositorio = new FeedbackRepositorio(jpa, sessoesJpa, usuariosJpa);
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
		var professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
		monitorId = new UsuarioIdentificador(monitor.getIdentificador().toString());
		alunoId = new UsuarioIdentificador(aluno.getIdentificador().toString());
		disciplinaId =
				new DisciplinaRepositorio(disciplinasJpa, cursosJpa, usuariosJpa)
						.incluir(
								new Disciplina(
										Optional.empty(),
										new InstituicaoIdentificador("cem.edu.br"),
										new CursoIdentificador(curso.getIdentificador().toString()),
										professorId,
										"Estrutura de Dados",
										new DisciplinaCodigo("EDS1001"),
										"Computação"))
						.getIdentificador()
						.orElseThrow();
		var campusId =
				new CampusRepositorio(campusJpa)
						.incluir(
								new Campus(
										Optional.empty(),
										new InstituicaoIdentificador("cem.edu.br"),
										"Central",
										null,
										CampusSituacao.ATIVO))
						.getIdentificador()
						.orElseThrow();
		LocalDateTime inicio = LocalDateTime.of(2026, 4, 10, 14, 0);
		sessaoId =
				new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa)
						.incluir(
								new Sessao(
										Optional.empty(),
										"Estrutura de Dados",
										professorId,
										monitorId,
										campusId,
										List.of(alunoId),
										inicio,
										inicio.plusHours(1),
										false))
						.getIdentificador()
						.orElseThrow();
	}

	@Test
	void incluiListaEDetectaDuplicidadePorSessaoEDisciplina() {
		var feedback =
				repositorio.incluir(
						new Feedback(
								Optional.empty(),
								sessaoId,
								alunoId,
								monitorId,
								8.5,
								Optional.of("Bom progresso"),
								LocalDateTime.of(2026, 4, 10, 16, 0).toLocalDate(),
								Optional.empty()));
		FeedbackIdentificador id = feedback.getIdentificador().orElseThrow();
		assertEquals(8.5, repositorio.buscarPorIdentificador(id).getNota());
		assertTrue(repositorio.existePorSessaoEAluno(sessaoId, alunoId));
		assertTrue(repositorio.existePorAlunoEDisciplina(alunoId, disciplinaId));
		assertEquals(1, repositorio.listarPorAlunoEDisciplina(alunoId, disciplinaId).size());
	}

	@Test
	void identificadorNaoNumericoNaoExiste() {
		assertFalse(repositorio.existePorIdentificador(new FeedbackIdentificador("xyz")));
	}
}
