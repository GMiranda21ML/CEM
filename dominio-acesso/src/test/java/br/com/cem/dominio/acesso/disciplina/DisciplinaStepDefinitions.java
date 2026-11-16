package br.com.cem.dominio.acesso.disciplina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.disciplina.spi.IProfessorValidadorServico;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class DisciplinaStepDefinitions {
	private final Map<String, Disciplina> disciplinas = new LinkedHashMap<>();
	private final Map<String, Curso> cursos = new LinkedHashMap<>();
	private final Set<String> professoresValidos = new HashSet<>();
	private final Set<String> disciplinasComVinculos = new HashSet<>();

	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final IDisciplinaRepositorio repositorio = mock(IDisciplinaRepositorio.class);
	private final ICursoRepositorio cursoRepositorio = mock(ICursoRepositorio.class);
	private final IProfessorValidadorServico professorValidadorServico = mock(IProfessorValidadorServico.class);
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico = mock(IInstituicaoUsuarioServico.class);

	private final DisciplinaServico servico = new DisciplinaServico(segurancaServico, repositorio, cursoRepositorio,
			professorValidadorServico, instituicaoUsuarioServico);

	private int proximoIdentificador = 1;
	private InstituicaoIdentificador instituicaoAtual;
	private Disciplina resultado;
	private RuntimeException excecaoCapturada;

	public DisciplinaStepDefinitions() {
		when(segurancaServico.getUsuario()).thenReturn("coordenador@cem.edu.br");
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		when(instituicaoUsuarioServico.buscarPorUsuario(any())).thenAnswer(chamada -> instituicaoAtual);

		when(cursoRepositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> cursos.containsKey(chamada.getArgument(0).toString()));
		when(cursoRepositorio.buscarPorIdentificador(any()))
				.thenAnswer(chamada -> cursos.get(chamada.getArgument(0).toString()));

		when(professorValidadorServico.isProfessorValido(any(), any(), any())).thenAnswer(chamada -> {
			UsuarioIdentificador prof = chamada.getArgument(0);
			CursoIdentificador curso = chamada.getArgument(1);
			return professoresValidos.contains(prof.toString() + "@" + curso.toString());
		});

		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> disciplinas.containsKey(chamada.getArgument(0).toString()));

		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			DisciplinaIdentificador id = chamada.getArgument(0);
			var d = disciplinas.get(id.toString());
			if (d == null) {
				throw new AgregadoInexistenteExcecao(
						DisciplinaServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(id));
			}
			return d;
		});

		when(repositorio.existePorCodigo(any(), any(), any())).thenAnswer(chamada -> {
			DisciplinaCodigo codigo = chamada.getArgument(0);
			InstituicaoIdentificador inst = chamada.getArgument(1);
			Optional<DisciplinaIdentificador> ignorado = chamada.getArgument(2);
			return disciplinas.values().stream().anyMatch(d -> d.getCodigo().equals(codigo)
					&& d.getInstituicao().equals(inst) && !d.getIdentificador().equals(ignorado));
		});

		when(repositorio.existePorNomeNoCurso(any(), any(), any())).thenAnswer(chamada -> {
			String nome = chamada.getArgument(0);
			CursoIdentificador curso = chamada.getArgument(1);
			Optional<DisciplinaIdentificador> ignorado = chamada.getArgument(2);
			return disciplinas.values().stream().anyMatch(d -> d.getNome().equalsIgnoreCase(nome)
					&& d.getCurso().equals(curso) && !d.getIdentificador().equals(ignorado));
		});

		when(repositorio.possuiVinculosMonitoria(any())).thenAnswer(chamada -> {
			DisciplinaIdentificador id = chamada.getArgument(0);
			var d = disciplinas.get(id.toString());
			return d != null && disciplinasComVinculos.contains(d.getCodigo().toString());
		});

		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Disciplina d = chamada.getArgument(0);
			var id = new DisciplinaIdentificador(String.valueOf(proximoIdentificador++));
			var inc = new Disciplina(Optional.of(id), d.getInstituicao(), d.getCurso(), d.getProfessor(), d.getNome(),
					d.getCodigo(), d.getArea());
			disciplinas.put(id.toString(), inc);
			return inc;
		});

		doAnswer(chamada -> {
			Disciplina d = chamada.getArgument(0);
			disciplinas.put(d.getIdentificador().orElseThrow().toString(), d);
			return null;
		}).when(repositorio).alterar(any());
	}

	@Dado("que a coordenadora de disciplinas pertence à instituição {string}")
	public void coordenadorPertenceAInstituicao(String instituicao) {
		instituicaoAtual = new InstituicaoIdentificador(instituicao);
	}

	@Dado("que existe o curso de disciplina {string} nessa instituição")
	public void existeCursoNessaInstituicao(String nomeCurso) {
		var cursoId = new CursoIdentificador(nomeCurso);
		cursos.put(cursoId.toString(),
				new Curso(Optional.of(cursoId), instituicaoAtual, nomeCurso, nomeCurso.substring(0, 2).toUpperCase()));
	}

	@Dado("que existe o professor de disciplina {string} ativo e vinculado ao curso {string}")
	@Dado("que existe o professor de disciplina {string} ativo no curso {string}")
	public void existeProfessorAtivoNoCurso(String professor, String curso) {
		professoresValidos.add(professor + "@" + curso);
	}

	@Dado("que não existe disciplina com código {string} nessa instituição")
	public void naoExisteDisciplinaComCodigo(String codigo) {
		disciplinas.values().removeIf(d -> d.getCodigo().toString().equalsIgnoreCase(codigo)
				&& d.getInstituicao().equals(instituicaoAtual));
	}

	@Dado("que já existe uma disciplina com código {string} nessa instituição")
	public void jaExisteDisciplinaComCodigo(String codigo) {
		cadastrarDisciplinaAux("Existente", codigo, "Geral", "Ciência da Computação", "Prof. Padrão");
	}

	@Dado("que já existe a disciplina {string} no curso {string}")
	public void jaExisteDisciplinaNoCurso(String nome, String curso) {
		cadastrarDisciplinaAux(nome, "MAT1001", "Matemática", curso, "Prof. Padrão");
	}

	@Dado("que o professor {string} está inativo no curso {string}")
	public void professorInativoNoCurso(String professor, String curso) {
		// Não adiciona em professoresValidos
	}

	@Dado("que o professor {string} pertence apenas ao curso {string}")
	public void professorPertenceApenasAoCurso(String professor, String curso) {
		professoresValidos.add(professor + "@" + curso);
	}

	@Dado("que existe a disciplina {string} com código {string} e área {string}")
	public void existeDisciplinaComArea(String nome, String codigo, String area) {
		cadastrarDisciplinaAux(nome, codigo, area, "Ciência da Computação", "Prof. Padrão");
	}

	@Dado("que existe a disciplina {string} com código {string} sem seleções, sem monitores e sem sessões")
	public void existeDisciplinaSemVinculos(String nome, String codigo) {
		cadastrarDisciplinaAux(nome, codigo, "Geral", "Ciência da Computação", "Prof. Padrão");
	}

	@Dado("que existe a disciplina {string} com código {string} com vínculos de monitoria")
	public void existeDisciplinaComVinculos(String nome, String codigo) {
		cadastrarDisciplinaAux(nome, codigo, "Matemática", "Ciência da Computação", "Prof. Padrão");
		disciplinasComVinculos.add(codigo);
	}

	private Disciplina cadastrarDisciplinaAux(String nome, String codigo, String area, String curso, String professor) {
		if (instituicaoAtual == null) {
			instituicaoAtual = new InstituicaoIdentificador("Instituto CEM");
		}
		var cursoId = new CursoIdentificador(curso);
		if (!cursos.containsKey(cursoId.toString())) {
			cursos.put(cursoId.toString(),
					new Curso(Optional.of(cursoId), instituicaoAtual, curso, "XX"));
		}
		professoresValidos.add(professor + "@" + curso);
		var id = new DisciplinaIdentificador(String.valueOf(proximoIdentificador++));
		var d = new Disciplina(Optional.of(id), instituicaoAtual, cursoId, new UsuarioIdentificador(professor), nome,
				new DisciplinaCodigo(codigo), area);
		disciplinas.put(id.toString(), d);
		return d;
	}

	@Quando("a coordenadora cadastra a disciplina {string} com código {string}, área {string}, no curso {string} e professor {string}")
	public void cadastraDisciplina(String nome, String codigo, String area, String curso, String professor) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo(codigo), area,
				new CursoIdentificador(curso), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora tenta cadastrar uma disciplina com nome vazio, código {string}, área {string}, curso {string} e professor {string}")
	public void tentaCadastrarSemNome(String codigo, String area, String curso, String professor) {
		executar(() -> resultado = servico.cadastrar(" ", new DisciplinaCodigo(codigo), area,
				new CursoIdentificador(curso), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora tenta cadastrar a disciplina {string} com código {string}, área {string}, curso {string} e professor {string}")
	public void tentaCadastrarComCodigo(String nome, String codigo, String area, String curso, String professor) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo(codigo), area,
				new CursoIdentificador(curso), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora tenta cadastrar a disciplina {string} com código {string}, área vazia, curso {string} e professor {string}")
	public void tentaCadastrarSemArea(String nome, String codigo, String curso, String professor) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo(codigo), " ",
				new CursoIdentificador(curso), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora tenta cadastrar outra disciplina {string} com o mesmo código {string}")
	public void tentaCadastrarComMesmoCodigo(String nome, String codigo) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo(codigo), "Matemática",
				new CursoIdentificador("Ciência da Computação"), new UsuarioIdentificador("Prof. Padrão")));
	}

	@Quando("a coordenadora tenta cadastrar outra disciplina {string} com código {string} no mesmo curso {string}")
	public void tentaCadastrarComMesmoNome(String nome, String codigo, String curso) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo(codigo), "Matemática",
				new CursoIdentificador(curso), new UsuarioIdentificador("Prof. Padrão")));
	}

	@Quando("a coordenadora tenta cadastrar a disciplina {string} para o professor {string}")
	public void tentaCadastrarProfessor(String nome, String professor) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo("MAT1003"), "Matemática",
				new CursoIdentificador("Ciência da Computação"), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora tenta cadastrar a disciplina {string} no curso {string} com o professor {string}")
	public void tentaCadastrarProfessorNoCurso(String nome, String curso, String professor) {
		executar(() -> resultado = servico.cadastrar(nome, new DisciplinaCodigo("MAT1004"), "Matemática",
				new CursoIdentificador(curso), new UsuarioIdentificador(professor)));
	}

	@Quando("a coordenadora altera a área da disciplina para {string}")
	public void alteraAreaDisciplina(String novaArea) {
		var d = disciplinas.values().iterator().next();
		executar(() -> servico.alterar(d.alterarDados(d.getNome(), novaArea, d.getProfessor())));
	}

	@Quando("a coordenadora remove a disciplina {string}")
	public void removeDisciplina(String codigo) {
		var d = disciplinas.values().stream().filter(disc -> disc.getCodigo().toString().equalsIgnoreCase(codigo))
				.findFirst().orElseThrow();
		executar(() -> {
			servico.excluir(d.getIdentificador().orElseThrow());
			disciplinas.remove(d.getIdentificador().orElseThrow().toString());
		});
	}

	@Quando("a coordenadora tenta remover a disciplina {string}")
	public void tentaRemoverDisciplina(String codigo) {
		removeDisciplina(codigo);
	}

	private void executar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	@Entao("a disciplina {string} é registrada com sucesso")
	public void disciplinaRegistrada(String codigo) {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(codigo, resultado.getCodigo().toString());
	}

	@Entao("o cadastro da disciplina é recusado")
	public void cadastroRecusado() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("a disciplina passa a ter a área {string}")
	public void disciplinaPassaATerArea(String area) {
		assertNull(excecaoCapturada);
		assertTrue(disciplinas.values().stream().anyMatch(d -> d.getArea().equals(area)));
	}

	@Entao("a disciplina {string} deixa de existir")
	public void disciplinaDeixaDeExistir(String codigo) {
		assertNull(excecaoCapturada);
		assertTrue(disciplinas.values().stream().noneMatch(d -> d.getCodigo().toString().equalsIgnoreCase(codigo)));
	}

	@Entao("a remoção da disciplina é recusada")
	public void remocaoRecusada() {
		assertNotNull(excecaoCapturada);
	}
}
