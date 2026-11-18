package br.com.cem.infraestrutura.monitoria.selecao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.cem.aplicacao.monitoria.selecao.SelecaoConsulta;
import br.com.cem.aplicacao.monitoria.selecao.SelecaoOrdenacao;
import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaCodigo;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoSituacao;
import br.com.cem.dominio.spi.IRelogioServico;
import br.com.cem.infraestrutura.acesso.curso.CursoJpa;
import br.com.cem.infraestrutura.acesso.curso.ICursoRepositorioJpa;
import br.com.cem.infraestrutura.acesso.disciplina.DisciplinaRepositorio;
import br.com.cem.infraestrutura.acesso.disciplina.IDisciplinaRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.IUsuarioRepositorioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioJpa;
import br.com.cem.infraestrutura.acesso.usuario.UsuarioRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
class SelecaoAplicacaoRepositorioTest {
	@Autowired private ISelecaoRepositorioJpa jpa;
	@Autowired private IDisciplinaRepositorioJpa disciplinasJpa;
	@Autowired private ICursoRepositorioJpa cursosJpa;
	@Autowired private IUsuarioRepositorioJpa usuariosJpa;

	private DisciplinaIdentificador disciplinaId;
	private UsuarioIdentificador professorId;

	@BeforeEach
	void configurar() {
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
		usuariosJpa.saveAndFlush(
				new UsuarioJpa(
						null,
						"Lucas Aluno",
						"lucas@aluno.cem.edu.br",
						"Cem@2026!",
						Autorizacao.ALUNO,
						"Computação",
						null,
						UsuarioSituacao.ATIVO));
		professorId =
				new UsuarioIdentificador(professor.getIdentificador().toString());
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
										"Algoritmos",
										new DisciplinaCodigo("ALG1001"),
										"Computação"))
						.getIdentificador()
						.orElseThrow();
		var repo = new SelecaoRepositorio(jpa, disciplinasJpa, usuariosJpa);
		LocalDate hoje = LocalDate.of(2026, 3, 1);
		repo.incluir(
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
						Optional.empty()));
	}

	@Test
	void pesquisaPorPapelEListaDisciplinasDoProfessor() {
		var seguranca = mock(ISegurancaServico.class);
		when(seguranca.getUsuario()).thenReturn("helena@cem.edu.br");
		when(seguranca.possuiAutorizacao("PROFESSOR")).thenReturn(true);
		var relogio = mock(IRelogioServico.class);
		when(relogio.agora()).thenReturn(LocalDateTime.of(2026, 3, 5, 10, 0));

		var aplicacao =
				new SelecaoAplicacaoRepositorio(
						jpa, seguranca, new UsuarioRepositorio(usuariosJpa), relogio);

		var pagina =
				aplicacao.pesquisar(
						new SelecaoConsulta(
								Optional.of(SelecaoOrdenacao.PRAZO),
								Optional.of(false),
								Optional.of(0),
								Optional.of(20),
								Optional.of("Algoritmos")));
		assertEquals(1, pagina.getTotal());
		assertEquals("Algoritmos", pagina.getProjecoes().getFirst().disciplinaNome());
		assertEquals(1, aplicacao.listarDisciplinasDoProfessor().size());

		when(seguranca.getUsuario()).thenReturn("lucas@aluno.cem.edu.br");
		when(seguranca.possuiAutorizacao("PROFESSOR")).thenReturn(false);
		when(seguranca.possuiAutorizacao("ALUNO")).thenReturn(true);
		assertEquals(
				1,
				aplicacao
						.pesquisar(
								new SelecaoConsulta(
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.empty(),
										Optional.empty()))
						.getTotal());
	}
}
