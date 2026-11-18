package br.com.cem.infraestrutura.monitoria.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.cem.aplicacao.monitoria.feedback.FeedbackConsulta;
import br.com.cem.aplicacao.monitoria.feedback.FeedbackOrdenacao;
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
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoSituacao;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;
import br.com.cem.infraestrutura.monitoria.acompanhamento.AcompanhamentoRepositorio;
import br.com.cem.infraestrutura.monitoria.acompanhamento.IAcompanhamentoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.ISessaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.sessao.SessaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class FeedbackAplicacaoRepositorioTest {
	@Autowired private IFeedbackRepositorioJpa jpa;
	@Autowired private ISessaoRepositorioJpa sessoesJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	@Autowired private ICampusRepositorioJpa campusJpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IAcompanhamentoRepositorioJpa acompanhamentosJpa;

	private UsuarioIdentificador alunoId;
	private UsuarioIdentificador monitorId;
	private DisciplinaIdentificador disciplinaId;
	private Acompanhamento acomp;

	@BeforeEach
	void configurar() {
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
								"Lucas",
								"lucas@aluno.cem.edu.br",
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
										"Compiladores",
										new DisciplinaCodigo("CMP1001"),
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
		LocalDateTime inicio = LocalDateTime.of(2026, 4, 15, 10, 0);
		var sessaoId =
				new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa)
						.incluir(
								new Sessao(
										Optional.empty(),
										"Compiladores",
										professorId,
										monitorId,
										campusId,
										List.of(alunoId),
										inicio,
										inicio.plusHours(1),
										false))
						.getIdentificador()
						.orElseThrow();
		new FeedbackRepositorio(jpa, sessoesJpa, usuariosJpa)
				.incluir(
						new Feedback(
								Optional.empty(),
								sessaoId,
								alunoId,
								monitorId,
								7.0,
								Optional.of("Participou bem"),
								inicio.toLocalDate(),
								Optional.empty()));
		acomp =
				new AcompanhamentoRepositorio(acompanhamentosJpa, disciplinasJpa, usuariosJpa)
						.incluir(
								new Acompanhamento(
										Optional.empty(),
										alunoId,
										disciplinaId,
										monitorId,
										"2026.1",
										7.0,
										75,
										AcompanhamentoSituacao.ATIVO,
										LocalDate.of(2026, 4, 1),
										Optional.empty(),
										Optional.empty()));
	}

	@Test
	void pesquisaSinalizaBloqueioAposEncerramentoDoAcompanhamento() {
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("monitor@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao("ALUNO")).thenReturn(true);

		var aplicacao =
				new FeedbackAplicacaoRepositorio(
						jpa,
						new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa),
						new AcompanhamentoRepositorio(
								acompanhamentosJpa, disciplinasJpa, usuariosJpa),
						seguranca,
						new UsuarioRepositorio(usuariosJpa));

		var consulta =
				new FeedbackConsulta(
						Optional.of(FeedbackOrdenacao.CRIACAO),
						Optional.of(true),
						Optional.of(0),
						Optional.of(20),
						Optional.of("Lucas"));

		var paginaAtiva = aplicacao.pesquisar(consulta);
		assertEquals(1, paginaAtiva.getTotal());
		assertFalse(paginaAtiva.getProjecoes().getFirst().bloqueado());

		new AcompanhamentoRepositorio(acompanhamentosJpa, disciplinasJpa, usuariosJpa)
				.alterar(
						acomp.encerrar(
								"Acompanhamento finalizado com evolução satisfatória.",
								LocalDate.of(2026, 5, 20)));

		var paginaEncerrada = aplicacao.pesquisar(consulta);
		assertTrue(paginaEncerrada.getProjecoes().getFirst().bloqueado());
	}
}
