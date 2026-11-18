package br.com.cem.infraestrutura.monitoria.acompanhamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoConsulta;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoOrdenacao;
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
import br.com.cem.dominio.monitoria.acompanhamento.SituacaoDesempenho;
import br.com.cem.dominio.monitoria.acompanhamento.TendenciaEvolucao;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.feedback.Feedback;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.dominio.monitoria.sessao.Sessao;
import br.com.cem.dominio.spi.IRelogioServico;
import br.com.cem.infraestrutura.acesso.campus.CampusRepositorio;
import br.com.cem.infraestrutura.acesso.campus.ICampusRepositorioJpa;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;
import br.com.cem.infraestrutura.monitoria.candidatura.CandidaturaRepositorio;
import br.com.cem.infraestrutura.monitoria.candidatura.ICandidaturaRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.falta.FaltaRepositorio;
import br.com.cem.infraestrutura.monitoria.falta.IFaltaRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.feedback.FeedbackRepositorio;
import br.com.cem.infraestrutura.monitoria.feedback.IFeedbackRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.ISelecaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.SelecaoRepositorio;
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
class AcompanhamentoAplicacaoRepositorioTest {
	@Autowired private IAcompanhamentoRepositorioJpa jpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;
	@Autowired private ICampusRepositorioJpa campusJpa;
	@Autowired private ISessaoRepositorioJpa sessoesJpa;
	@Autowired private IFaltaRepositorioJpa faltasJpa;
	@Autowired private IFeedbackRepositorioJpa feedbacksJpa;
	@Autowired private ISelecaoRepositorioJpa selecoesJpa;
	@Autowired private ICandidaturaRepositorioJpa candidaturasJpa;

	private final LocalDateTime agora = LocalDateTime.of(2026, 5, 20, 12, 0);
	private UsuarioIdentificador monitorId;
	private UsuarioIdentificador alunoId;
	private UsuarioIdentificador segundoAlunoId;
	private DisciplinaIdentificador disciplinaId;

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
								"Carlos Monitor",
								"carlos@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		var aluno =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Ana Aluna",
								"ana@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"Computação",
								null,
								UsuarioSituacao.ATIVO));
		var segundoAluno =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Bruno Aluno",
								"bruno@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"CC",
								null,
								UsuarioSituacao.ATIVO));
		var professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
		monitorId = new UsuarioIdentificador(monitor.getIdentificador().toString());
		alunoId = new UsuarioIdentificador(aluno.getIdentificador().toString());
		segundoAlunoId =
				new UsuarioIdentificador(segundoAluno.getIdentificador().toString());

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

		var selecaoRepo = new SelecaoRepositorio(selecoesJpa, disciplinasJpa, usuariosJpa);
		LocalDate inicioSemestre = LocalDate.of(2026, 3, 1);
		var selecaoId =
				selecaoRepo
						.incluir(
								new Selecao(
										Optional.empty(),
										disciplinaId,
										professorId,
										"2026.1",
										inicioSemestre.plusDays(10),
										2,
										7.0,
										50,
										50,
										7.0,
										SelecaoSituacao.ABERTA,
										Optional.empty(),
										inicioSemestre,
										Optional.empty()))
						.getIdentificador()
						.orElseThrow();
		var candRepo =
				new CandidaturaRepositorio(
						candidaturasJpa, selecoesJpa, disciplinasJpa, usuariosJpa);
		candRepo.incluir(
				new Candidatura(
						Optional.empty(),
						selecaoId,
						disciplinaId,
						monitorId,
						9.0,
						CandidaturaSituacao.APROVADA,
						Optional.of(9.0),
						Optional.empty(),
						Optional.of(9.0),
						Optional.of("Selecionado"),
						inicioSemestre.atStartOfDay(),
						Optional.of(inicioSemestre.plusDays(12))));

		var acompRepo = new AcompanhamentoRepositorio(jpa, disciplinasJpa, usuariosJpa);
		acompRepo.incluir(
				new Acompanhamento(
						Optional.empty(),
						alunoId,
						disciplinaId,
						monitorId,
						"2026.1",
						7.5,
						75,
						AcompanhamentoSituacao.ATIVO,
						inicioSemestre.plusDays(15),
						Optional.empty(),
						Optional.empty()));

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
		var sessaoRepo = new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa);
		var feedbackRepo = new FeedbackRepositorio(feedbacksJpa, sessoesJpa, usuariosJpa);
		double[] notas = {5.0, 5.5, 8.0, 8.5};
		for (int i = 0; i < notas.length; i++) {
			LocalDateTime inicio = agora.minusDays(20L - i * 3L);
			var sessaoId =
					sessaoRepo
							.incluir(
									new Sessao(
											Optional.empty(),
											"Cálculo I",
											professorId,
											monitorId,
											campusId,
											List.of(alunoId),
											inicio,
											inicio.plusHours(1),
											false))
							.getIdentificador()
							.orElseThrow();
			feedbackRepo.incluir(
					new Feedback(
							Optional.empty(),
							sessaoId,
							alunoId,
							monitorId,
							notas[i],
							Optional.of("Sessão " + (i + 1)),
							inicio.toLocalDate(),
							Optional.empty()));
		}
	}

	@Test
	void pesquisaComResumoCalculadoEListaOpcoesAbertura() {
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("carlos@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao("ALUNO")).thenReturn(true);
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(agora);

		var aplicacao =
				new AcompanhamentoAplicacaoRepositorio(
						jpa,
						new AcompanhamentoRepositorio(jpa, disciplinasJpa, usuariosJpa),
						new DisciplinaRepositorio(disciplinasJpa, cursosJpa, usuariosJpa),
						new SessaoRepositorio(sessoesJpa, usuariosJpa, campusJpa),
						new FaltaRepositorio(faltasJpa, sessoesJpa, usuariosJpa),
						new FeedbackRepositorio(feedbacksJpa, sessoesJpa, usuariosJpa),
						seguranca,
						new UsuarioRepositorio(usuariosJpa),
						relogio);

		var opcoes = aplicacao.listarOpcoesAbertura();
		assertEquals(1, opcoes.size());
		assertEquals(segundoAlunoId, opcoes.getFirst().aluno());

		var pagina =
				aplicacao.pesquisar(
						new AcompanhamentoConsulta(
								Optional.of(AcompanhamentoOrdenacao.ALUNO),
								Optional.of(false),
								Optional.of(0),
								Optional.of(20),
								Optional.of("Ana")));
		assertEquals(1, pagina.getTotal());
		var resumo = pagina.getProjecoes().getFirst();
		assertTrue(resumo.possuiFeedback());
		assertEquals(100.0, resumo.frequenciaPercentual().orElseThrow(), 0.01);
		assertEquals(5.25, resumo.notaInicial().orElseThrow(), 0.01);
		assertEquals(8.25, resumo.notaAtual().orElseThrow(), 0.01);
		assertEquals(TendenciaEvolucao.EM_EVOLUCAO, resumo.tendencia());
		assertEquals(SituacaoDesempenho.META_ATINGIDA, resumo.situacaoDesempenho());
	}
}
