package br.com.cem.dominio.acesso.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;
import br.com.cem.dominio.acesso.dominioemail.spi.IDominioEmailRepositorio;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class UsuarioStepDefinitions {
	private static final String SENHA_FORTE = "Cem@2026!";
	private static final String NOVO_NOME = "Rafael Teixeira Souza";
	private static final String NOVO_CURSO = "Engenharia Civil";
	private static final String NOVO_CAMPUS = "Campus Leste";

	private final Map<String, Usuario> usuarios = new HashMap<>();
	private final Set<String> dominiosLiberados = new HashSet<>();

	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final IUsuarioRepositorio repositorio = mock(IUsuarioRepositorio.class);
	private final IDominioEmailRepositorio dominioEmailRepositorio = mock(IDominioEmailRepositorio.class);
	private final IEnviadorEmailServico enviadorEmailServico = mock(IEnviadorEmailServico.class);
	private final UsuarioServico servico = new UsuarioServico(segurancaServico, repositorio, dominioEmailRepositorio,
			enviadorEmailServico);

	private int proximoIdentificador = 1;
	private Usuario resultadoInclusao;
	private RuntimeException excecaoCapturada;

	public UsuarioStepDefinitions() {
		when(dominioEmailRepositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> dominiosLiberados.contains(chaveDominio(chamada.getArgument(0))));

		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> usuarios.containsKey(chaveUsuario(chamada.getArgument(0))));

		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			UsuarioIdentificador identificador = chamada.getArgument(0);
			var usuario = usuarios.get(chaveUsuario(identificador));
			if (usuario == null) {
				throw new AgregadoInexistenteExcecao(
						String.format(UsuarioServico.IDENTIFICADOR_INEXISTENTE_MODELO, identificador));
			}
			return usuario;
		});

		when(repositorio.existePorEmail(any(), any())).thenAnswer(chamada -> {
			UsuarioEmail email = chamada.getArgument(0);
			Optional<UsuarioIdentificador> identificadorIgnorado = chamada.getArgument(1);
			for (var usuario : usuarios.values()) {
				var mesmoEmail = usuario.getEmailInstitucional().equals(email);
				var ignorado = identificadorIgnorado.isPresent()
						&& usuario.getIdentificador().equals(identificadorIgnorado);
				if (mesmoEmail && !ignorado) {
					return true;
				}
			}
			return false;
		});

		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Usuario usuario = chamada.getArgument(0);
			var identificador = new UsuarioIdentificador(String.valueOf(proximoIdentificador++));
			var incluido = new Usuario(Optional.of(identificador), usuario.getNome(), usuario.getEmailInstitucional(),
					usuario.getSenha(), usuario.getPapel(), usuario.getCurso(), usuario.getCampus(),
					usuario.getSituacao());
			usuarios.put(chaveUsuario(identificador), incluido);
			return incluido;
		});

		doAnswer(chamada -> {
			Usuario usuario = chamada.getArgument(0);
			usuarios.put(chaveUsuario(usuario.getIdentificador().orElseThrow()), usuario);
			return null;
		}).when(repositorio).alterar(any());

		doAnswer(chamada -> {
			UsuarioIdentificador identificador = chamada.getArgument(0);
			usuarios.remove(chaveUsuario(identificador));
			return null;
		}).when(repositorio).excluir(any());
	}

	private static String chaveUsuario(UsuarioIdentificador identificador) {
		return identificador.toString();
	}

	private static String chaveDominio(DominioEmailDominio dominio) {
		return dominio.toString();
	}

	private Usuario buscarPorNome(String nome) {
		for (var usuario : usuarios.values()) {
			if (usuario.getNome().equals(nome)) {
				return usuario;
			}
		}
		throw new IllegalStateException("Usuário não encontrado com o nome " + nome);
	}

	private static String emailDoAluno(String nome) {
		return nome.toLowerCase().replace(' ', '.') + "@aluno.cem.edu.br";
	}

	private void comoCoordenador() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	@Dado("que o domínio de e-mail {string} está liberado para autocadastro")
	public void que_o_dominio_de_email_esta_liberado(String dominio) {
		dominiosLiberados.add(dominio);
	}

	@Dado("que o domínio de e-mail {string} não está liberado para autocadastro")
	public void que_o_dominio_de_email_nao_esta_liberado(String dominio) {
		dominiosLiberados.remove(dominio);
	}

	@Dado("que {string} já tem uma conta")
	public void que_ja_tem_uma_conta(String email) {
		var identificador = new UsuarioIdentificador(String.valueOf(proximoIdentificador++));
		var usuario = new Usuario(Optional.of(identificador), "Usuário existente", new UsuarioEmail(email),
				new UsuarioSenha(SENHA_FORTE), Autorizacao.ALUNO, "Ciência da Computação", null,
				UsuarioSituacao.ATIVO);
		usuarios.put(chaveUsuario(identificador), usuario);
	}

	@Dado("que existe um usuário aluno {string}")
	public void que_existe_um_usuario_aluno(String nome) {
		criarUsuario(nome, Autorizacao.ALUNO, UsuarioSituacao.ATIVO);
	}

	@Dado("que existe um usuário ativo {string}")
	public void que_existe_um_usuario_ativo(String nome) {
		criarUsuario(nome, Autorizacao.ALUNO, UsuarioSituacao.ATIVO);
	}

	@Dado("que existe um usuário inativo {string}")
	public void que_existe_um_usuario_inativo(String nome) {
		criarUsuario(nome, Autorizacao.ALUNO, UsuarioSituacao.INATIVO);
	}

	private void criarUsuario(String nome, Autorizacao papel, UsuarioSituacao situacao) {
		var identificador = new UsuarioIdentificador(String.valueOf(proximoIdentificador++));
		var usuario = new Usuario(Optional.of(identificador), nome, new UsuarioEmail(emailDoAluno(nome)),
				new UsuarioSenha(SENHA_FORTE), papel, "Ciência da Computação", null, situacao);
		usuarios.put(chaveUsuario(identificador), usuario);
	}

	@Quando("{string} cria uma conta de aluno com o nome {string}, o curso {string} e uma senha forte")
	public void cria_uma_conta_de_aluno(String email, String nome, String curso) {
		criarConta(email, nome, curso, SENHA_FORTE, Autorizacao.ALUNO);
	}

	@Quando("{string} tenta criar uma conta de aluno")
	public void tenta_criar_uma_conta_de_aluno(String email) {
		criarConta(email, "Lucas Ferreira", "Ciência da Computação", SENHA_FORTE, Autorizacao.ALUNO);
	}

	@Quando("{string} tenta criar outra conta de aluno")
	public void tenta_criar_outra_conta_de_aluno(String email) {
		criarConta(email, "Lucas Ferreira", "Ciência da Computação", SENHA_FORTE, Autorizacao.ALUNO);
	}

	@Quando("{string} tenta criar uma conta de aluno com a senha {string}")
	public void tenta_criar_uma_conta_de_aluno_com_senha(String email, String senha) {
		criarConta(email, "Lucas Ferreira", "Ciência da Computação", senha, Autorizacao.ALUNO);
	}

	@Quando("{string} tenta criar uma conta de coordenador")
	public void tenta_criar_uma_conta_de_coordenador(String email) {
		criarConta(email, "Lucas Ferreira", "Ciência da Computação", SENHA_FORTE, Autorizacao.COORDENADOR);
	}

	private void criarConta(String email, String nome, String curso, String senha, Autorizacao papel) {
		try {
			var usuario = new Usuario(Optional.empty(), nome, new UsuarioEmail(email), new UsuarioSenha(senha), papel,
					curso, null, UsuarioSituacao.ATIVO);
			resultadoInclusao = servico.criarConta(usuario);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o coordenador altera o nome, o curso e o campus de {string}")
	public void o_coordenador_altera_nome_curso_campus(String nome) {
		comoCoordenador();
		var existente = buscarPorNome(nome);
		alterar(existente, NOVO_NOME, existente.getEmailInstitucional(), existente.getPapel(), NOVO_CURSO,
				NOVO_CAMPUS);
	}

	@Quando("o coordenador tenta alterar o e-mail de {string}")
	public void o_coordenador_tenta_alterar_o_email(String nome) {
		comoCoordenador();
		var existente = buscarPorNome(nome);
		alterar(existente, existente.getNome(), new UsuarioEmail("outro.email@aluno.cem.edu.br"),
				existente.getPapel(), existente.getCurso(), existente.getCampus());
	}

	@Quando("o coordenador tenta alterar o perfil de {string} para professor")
	public void o_coordenador_tenta_alterar_o_perfil(String nome) {
		comoCoordenador();
		var existente = buscarPorNome(nome);
		alterar(existente, existente.getNome(), existente.getEmailInstitucional(), Autorizacao.PROFESSOR,
				existente.getCurso(), existente.getCampus());
	}

	private void alterar(Usuario existente, String nome, UsuarioEmail email, Autorizacao papel, String curso,
			String campus) {
		try {
			var alterado = new Usuario(existente.getIdentificador(), nome, email, existente.getSenha(), papel, curso,
					campus, existente.getSituacao());
			servico.alterar(alterado);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o coordenador inativa {string}")
	public void o_coordenador_inativa(String nome) {
		comoCoordenador();
		try {
			servico.inativar(buscarPorNome(nome).getIdentificador().orElseThrow());
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o coordenador reativa {string}")
	public void o_coordenador_reativa(String nome) {
		comoCoordenador();
		try {
			servico.reativar(buscarPorNome(nome).getIdentificador().orElseThrow());
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o coordenador exclui {string}")
	public void o_coordenador_exclui(String nome) {
		comoCoordenador();
		try {
			servico.excluir(buscarPorNome(nome).getIdentificador().orElseThrow());
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Entao("uma conta de aluno é criada para {string}")
	public void uma_conta_de_aluno_e_criada_para(String email) {
		assertNull(excecaoCapturada);
		assertNotNull(resultadoInclusao);
		assertEquals(new UsuarioEmail(email), resultadoInclusao.getEmailInstitucional());
		assertEquals(Autorizacao.ALUNO, resultadoInclusao.getPapel());
	}

	@Entao("a operação é recusada")
	public void a_operacao_e_recusada() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("os dados de {string} são atualizados")
	public void os_dados_sao_atualizados(String nomeAntigo) {
		assertNull(excecaoCapturada);
		var atualizado = usuarios.get(chaveUsuario(buscarPorNomeNovoOuAntigo(nomeAntigo)));
		assertEquals(NOVO_NOME, atualizado.getNome());
		assertEquals(NOVO_CURSO, atualizado.getCurso());
		assertEquals(NOVO_CAMPUS, atualizado.getCampus());
	}

	private UsuarioIdentificador buscarPorNomeNovoOuAntigo(String nomeAntigo) {
		for (var usuario : usuarios.values()) {
			if (usuario.getNome().equals(NOVO_NOME) || usuario.getNome().equals(nomeAntigo)) {
				return usuario.getIdentificador().orElseThrow();
			}
		}
		throw new IllegalStateException("Usuário não encontrado");
	}

	@Entao("{string} está inativo")
	public void esta_inativo(String nome) {
		assertFalse(buscarPorNome(nome).estaAtivo());
	}

	@Entao("{string} está ativo")
	public void esta_ativo(String nome) {
		assertTrue(buscarPorNome(nome).estaAtivo());
	}

	@Entao("{string} não existe mais")
	public void nao_existe_mais(String nome) {
		for (var usuario : usuarios.values()) {
			assertFalse(usuario.getNome().equals(nome));
		}
	}
}
