package br.com.cem.dominio.acesso.curso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class CursoStepDefinitions {
	private final Map<String, Curso> cursos = new LinkedHashMap<>();
	private final Set<String> cursosComDisciplinas = new HashSet<>();
	private final Set<String> cursosComUsuarios = new HashSet<>();

	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final ICursoRepositorio repositorio = mock(ICursoRepositorio.class);
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico = mock(IInstituicaoUsuarioServico.class);
	private final CursoServico servico = new CursoServico(segurancaServico, repositorio, instituicaoUsuarioServico);

	private int proximoIdentificador = 1;
	private InstituicaoIdentificador instituicaoAtual;
	private Curso resultado;
	private RuntimeException excecaoCapturada;

	public CursoStepDefinitions() {
		when(segurancaServico.getUsuario()).thenReturn("coordenador@cem.edu.br");
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		when(instituicaoUsuarioServico.buscarPorUsuario(any())).thenAnswer(chamada -> instituicaoAtual);

		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> cursos.containsKey(chamada.getArgument(0).toString()));

		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			CursoIdentificador identificador = chamada.getArgument(0);
			var curso = cursos.get(identificador.toString());
			if (curso == null) {
				throw new AgregadoInexistenteExcecao(CursoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador));
			}
			return curso;
		});

		when(repositorio.existePorNome(any(), any(), any())).thenAnswer(chamada -> {
			String nome = chamada.getArgument(0);
			InstituicaoIdentificador instituicao = chamada.getArgument(1);
			Optional<CursoIdentificador> ignorado = chamada.getArgument(2);
			return cursos.values().stream().anyMatch(c -> c.getNome().equalsIgnoreCase(nome)
					&& c.getInstituicao().equals(instituicao) && !c.getIdentificador().equals(ignorado));
		});

		when(repositorio.existePorSigla(any(), any(), any())).thenAnswer(chamada -> {
			String sigla = chamada.getArgument(0);
			InstituicaoIdentificador instituicao = chamada.getArgument(1);
			Optional<CursoIdentificador> ignorado = chamada.getArgument(2);
			return cursos.values().stream().anyMatch(c -> c.getSigla().equalsIgnoreCase(sigla)
					&& c.getInstituicao().equals(instituicao) && !c.getIdentificador().equals(ignorado));
		});

		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Curso curso = chamada.getArgument(0);
			var identificador = new CursoIdentificador(String.valueOf(proximoIdentificador++));
			var incluido = new Curso(Optional.of(identificador), curso.getInstituicao(), curso.getNome(),
					curso.getSigla());
			cursos.put(identificador.toString(), incluido);
			return incluido;
		});

		when(repositorio.possuiDisciplinasVinculadas(any())).thenAnswer(chamada -> {
			CursoIdentificador id = chamada.getArgument(0);
			var curso = cursos.get(id.toString());
			return curso != null && cursosComDisciplinas.contains(curso.getNome());
		});

		when(repositorio.possuiUsuariosVinculados(any())).thenAnswer(chamada -> {
			CursoIdentificador id = chamada.getArgument(0);
			var curso = cursos.get(id.toString());
			return curso != null && cursosComUsuarios.contains(curso.getNome());
		});
	}

	@Dado("que a coordenadora do curso pertence à instituição {string}")
	public void coordenadorPertenceAInstituicao(String instituicao) {
		instituicaoAtual = new InstituicaoIdentificador(instituicao);
	}

	@Dado("que não existe curso {string} com sigla {string} na instituição {string}")
	public void naoExisteCursoComSigla(String nome, String sigla, String instituicao) {
		cursos.values().removeIf(c -> (c.getNome().equalsIgnoreCase(nome) || c.getSigla().equalsIgnoreCase(sigla))
				&& c.getInstituicao().equals(new InstituicaoIdentificador(instituicao)));
	}

	@Dado("que existe o curso {string} com sigla {string} na instituição {string}")
	public void existeCursoNaInstituicao(String nome, String sigla, String instituicao) {
		adicionarCurso(nome, sigla, instituicao);
	}

	@Dado("que existe o curso {string} com sigla {string} sem disciplinas e sem usuários vinculados")
	public void existeCursoSemVinculos(String nome, String sigla) {
		adicionarCurso(nome, sigla, instituicaoAtual.toString());
	}

	@Dado("que existe o curso {string} com disciplinas vinculadas")
	public void existeCursoComDisciplinas(String nome) {
		adicionarCurso(nome, "CC", instituicaoAtual.toString());
		cursosComDisciplinas.add(nome);
	}

	@Dado("que existe o curso {string} com usuários vinculados")
	public void existeCursoComUsuarios(String nome) {
		adicionarCurso(nome, "EC", instituicaoAtual.toString());
		cursosComUsuarios.add(nome);
	}

	private Curso adicionarCurso(String nome, String sigla, String instituicao) {
		var id = new CursoIdentificador(String.valueOf(proximoIdentificador++));
		var curso = new Curso(Optional.of(id), new InstituicaoIdentificador(instituicao), nome, sigla);
		cursos.put(id.toString(), curso);
		return curso;
	}

	@Quando("a coordenadora cadastra o curso {string} com a sigla {string}")
	public void cadastraCurso(String nome, String sigla) {
		executar(() -> resultado = servico.cadastrar(nome, sigla));
	}

	@Quando("a coordenadora tenta cadastrar um curso com nome vazio e sigla {string}")
	public void tentaCadastrarSemNome(String sigla) {
		executar(() -> resultado = servico.cadastrar(" ", sigla));
	}

	@Quando("a coordenadora tenta cadastrar o curso {string} com sigla vazia")
	public void tentaCadastrarSemSigla(String nome) {
		executar(() -> resultado = servico.cadastrar(nome, " "));
	}

	@Quando("a coordenadora tenta cadastrar outro curso {string} com a sigla {string}")
	public void tentaCadastrarOutroCurso(String nome, String sigla) {
		executar(() -> resultado = servico.cadastrar(nome, sigla));
	}

	@Quando("a coordenadora remove o curso {string}")
	public void removeCurso(String nome) {
		var curso = buscarPorNome(nome);
		executar(() -> {
			servico.excluir(curso.getIdentificador().orElseThrow());
			cursos.remove(curso.getIdentificador().orElseThrow().toString());
		});
	}

	@Quando("a coordenadora tenta remover o curso {string}")
	public void tentaRemoverCurso(String nome) {
		removeCurso(nome);
	}

	private void executar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException e) {
			excecaoCapturada = e;
		}
	}

	private Curso buscarPorNome(String nome) {
		return cursos.values().stream().filter(c -> c.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow();
	}

	@Entao("o curso {string} com sigla {string} é registrado na instituição {string}")
	public void cursoRegistrado(String nome, String sigla, String instituicao) {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(nome, resultado.getNome());
		assertEquals(sigla, resultado.getSigla());
		assertEquals(new InstituicaoIdentificador(instituicao), resultado.getInstituicao());
	}

	@Entao("o cadastro do curso é recusado")
	public void cadastroRecusado() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("o curso {string} deixa de existir")
	public void cursoDeixaDeExistir(String nome) {
		assertNull(excecaoCapturada);
		assertTrue(cursos.values().stream().noneMatch(c -> c.getNome().equalsIgnoreCase(nome)));
	}

	@Entao("a remoção do curso é recusada")
	public void remocaoRecusada() {
		assertNotNull(excecaoCapturada);
	}
}
