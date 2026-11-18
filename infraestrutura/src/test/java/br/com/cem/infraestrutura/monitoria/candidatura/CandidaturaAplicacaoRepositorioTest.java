package br.com.cem.infraestrutura.monitoria.candidatura;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaConsulta;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaOrdenacao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaSituacao;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.dominio.spi.IRelogioServico;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;
import br.com.cem.infraestrutura.monitoria.selecao.ISelecaoRepositorioJpa;
import br.com.cem.infraestrutura.monitoria.selecao.SelecaoRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class CandidaturaAplicacaoRepositorioTest {
	@Autowired private ICandidaturaRepositorioJpa jpa;
	@Autowired private ISelecaoRepositorioJpa selecoesJpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;

	private SelecaoIdentificador selecaoId;
	private DisciplinaIdentificador disciplinaId;
	private UsuarioIdentificador alunoId;

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
		var aluno =
				usuariosJpa.saveAndFlush(
						new UsuarioJpa(
								null,
								"Mariana Silva",
								"mariana@aluno.cem.edu.br",
								"Cem@2026!",
								Autorizacao.ALUNO,
								"CC",
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
										"Sistemas Operacionais",
										new DisciplinaCodigo("SOP1001"),
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
										hoje.plusDays(15),
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
	void listaSelecoesAbertasEPesquisaCandidaturas() {
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("mariana@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao("ALUNO")).thenReturn(true);
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(LocalDateTime.of(2026, 3, 3, 9, 0));

		var aplicacao =
				new CandidaturaAplicacaoRepositorio(
						jpa, selecoesJpa, seguranca, new UsuarioRepositorio(usuariosJpa), relogio);

		var abertasAntes = aplicacao.listarSelecoesAbertasAluno();
		assertEquals(1, abertasAntes.size());
		assertTrue(abertasAntes.getFirst().elegivel());

		var candRepo =
				new CandidaturaRepositorio(jpa, selecoesJpa, disciplinasJpa, usuariosJpa);
		candRepo.incluir(
				new Candidatura(
						Optional.empty(),
						selecaoId,
						disciplinaId,
						alunoId,
						8.2,
						CandidaturaSituacao.PENDENTE,
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						Optional.empty(),
						LocalDateTime.of(2026, 3, 3, 9, 0),
						Optional.empty()));

		var abertasDepois = aplicacao.listarSelecoesAbertasAluno();
		assertFalse(abertasDepois.getFirst().elegivel());
		assertFalse(abertasDepois.getFirst().semCandidaturaAtiva());
		assertEquals("PENDENTE", abertasDepois.getFirst().situacaoCandidaturaAtiva().orElseThrow());

		var pagina =
				aplicacao.pesquisar(
						new CandidaturaConsulta(
								Optional.of(CandidaturaOrdenacao.CRIACAO),
								Optional.of(true),
								Optional.of(0),
								Optional.of(20),
								Optional.of("Mariana"),
								Optional.of(selecaoId)));
		assertEquals(1, pagina.getTotal());
	}
}
