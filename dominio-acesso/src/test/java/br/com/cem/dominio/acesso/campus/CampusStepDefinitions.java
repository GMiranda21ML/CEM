package br.com.cem.dominio.acesso.campus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class CampusStepDefinitions {
	private final Map<String, Campus> campi = new LinkedHashMap<>();
	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final ICampusRepositorio repositorio = mock(ICampusRepositorio.class);
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico = mock(IInstituicaoUsuarioServico.class);
	private final CampusServico servico = new CampusServico(segurancaServico, repositorio, instituicaoUsuarioServico);

	private int proximoIdentificador = 1;
	private InstituicaoIdentificador instituicaoAtual;
	private Campus resultado;
	private RuntimeException excecaoCapturada;

	public CampusStepDefinitions() {
		when(segurancaServico.getUsuario()).thenReturn("coordenador@cem.edu.br");
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		when(instituicaoUsuarioServico.buscarPorUsuario(any())).thenAnswer(chamada -> instituicaoAtual);
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> campi.containsKey(chamada.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			CampusIdentificador identificador = chamada.getArgument(0);
			var campus = campi.get(identificador.toString());
			if (campus == null) {
				throw new AgregadoInexistenteExcecao(CampusServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador));
			}
			return campus;
		});
		when(repositorio.existePorNome(any(), any(), any())).thenAnswer(chamada -> {
			String nome = chamada.getArgument(0);
			InstituicaoIdentificador instituicao = chamada.getArgument(1);
			Optional<CampusIdentificador> ignorado = chamada.getArgument(2);
			return campi.values().stream().anyMatch(campus -> campus.getNome().equalsIgnoreCase(nome)
					&& campus.getInstituicao().equals(instituicao) && !campus.getIdentificador().equals(ignorado));
		});
		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Campus campus = chamada.getArgument(0);
			var identificador = new CampusIdentificador(String.valueOf(proximoIdentificador++));
			var incluido = new Campus(Optional.of(identificador), campus.getInstituicao(), campus.getNome(),
					campus.getEndereco(), campus.getSituacao());
			campi.put(identificador.toString(), incluido);
			return incluido;
		});
		doAnswer(chamada -> {
			Campus campus = chamada.getArgument(0);
			campi.put(campus.getIdentificador().orElseThrow().toString(), campus);
			return null;
		}).when(repositorio).alterar(any());
	}

	@Dado("que o coordenador pertence à instituição {string}")
	public void coordenadorPertenceAInstituicao(String instituicao) {
		instituicaoAtual = new InstituicaoIdentificador(instituicao);
	}

	@Dado("que não existe campus chamado {string} na instituição {string}")
	public void naoExisteCampus(String nome, String instituicao) {
		campi.values().removeIf(campus -> campus.getNome().equalsIgnoreCase(nome)
				&& campus.getInstituicao().equals(new InstituicaoIdentificador(instituicao)));
	}

	@Dado("que existe campus chamado {string} na instituição {string}")
	public void existeCampusChamado(String nome, String instituicao) {
		adicionarCampus(nome, instituicao, CampusSituacao.ATIVO);
	}

	@Dado("que existe o campus ativo {string} na instituição {string}")
	public void existeCampusAtivo(String nome, String instituicao) {
		adicionarCampus(nome, instituicao, CampusSituacao.ATIVO);
	}

	@Dado("que existe o campus inativo {string} na instituição {string}")
	public void existeCampusInativoNaInstituicao(String nome, String instituicao) {
		adicionarCampus(nome, instituicao, CampusSituacao.INATIVO);
	}

	@Dado("que o campus {string} está inativo")
	public void campusEstaInativo(String nome) {
		instituicaoAtual = new InstituicaoIdentificador("Instituto CEM");
		adicionarCampus(nome, instituicaoAtual.toString(), CampusSituacao.INATIVO);
	}

	private Campus adicionarCampus(String nome, String instituicao, CampusSituacao situacao) {
		var identificador = new CampusIdentificador(String.valueOf(proximoIdentificador++));
		var campus = new Campus(Optional.of(identificador), new InstituicaoIdentificador(instituicao), nome,
				"Rua existente", situacao);
		campi.put(identificador.toString(), campus);
		return campus;
	}

	@Quando("o coordenador cadastra o campus {string} com endereço {string}")
	public void cadastrarCampus(String nome, String endereco) {
		executar(() -> resultado = servico.cadastrar(nome, endereco));
	}

	@Quando("o coordenador tenta cadastrar um campus com nome vazio")
	public void cadastrarCampusSemNome() {
		executar(() -> resultado = servico.cadastrar(" ", null));
	}

	@Quando("o coordenador tenta cadastrar outro campus chamado {string}")
	public void cadastrarCampusDuplicado(String nome) {
		executar(() -> resultado = servico.cadastrar(nome, null));
	}

	@Quando("o coordenador tenta cadastrar o campus {string} na instituição {string}")
	public void cadastrarCampusEmOutraInstituicao(String nome, String instituicao) {
		var campus = new Campus(Optional.empty(), new InstituicaoIdentificador(instituicao), nome, null,
				CampusSituacao.ATIVO);
		executar(() -> servico.incluir(campus));
	}

	@Quando("o coordenador altera o nome do campus para {string}")
	public void alterarNome(String novoNome) {
		var campus = campi.values().iterator().next();
		executar(() -> servico.alterar(campus.alterarDados(novoNome, campus.getEndereco())));
	}

	@Quando("o coordenador tenta alterar o nome do campus para {string}")
	public void tentarAlterarNome(String novoNome) {
		alterarNome(novoNome);
	}

	@Quando("o coordenador inativa o campus {string}")
	public void inativarCampus(String nome) {
		executar(() -> servico.inativar(buscar(nome).getIdentificador().orElseThrow()));
	}

	@Quando("o coordenador reativa o campus {string}")
	public void reativarCampus(String nome) {
		executar(() -> servico.reativar(buscar(nome).getIdentificador().orElseThrow()));
	}

	@Quando("tenta-se criar uma disponibilidade no campus {string}")
	public void criarDisponibilidade(String nome) {
		executar(() -> servico.validarUsoEmNovaAtividade(buscar(nome).getIdentificador().orElseThrow()));
	}

	@Quando("tenta-se criar uma sessão no campus {string}")
	public void criarSessao(String nome) {
		criarDisponibilidade(nome);
	}

	private void executar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	private Campus buscar(String nome) {
		return campi.values().stream().filter(campus -> campus.getNome().equals(nome)).findFirst().orElseThrow();
	}

	@Entao("o campus {string} é registrado ativo na instituição {string}")
	public void campusRegistradoAtivo(String nome, String instituicao) {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(nome, resultado.getNome());
		assertEquals(new InstituicaoIdentificador(instituicao), resultado.getInstituicao());
		assertTrue(resultado.estaAtivo());
	}

	@Entao("o cadastro do campus é recusado")
	public void cadastroRecusado() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("a alteração do campus é recusada")
	public void alteracaoRecusada() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("o campus passa a se chamar {string}")
	public void campusPassaASeChamar(String nome) {
		assertNull(excecaoCapturada);
		assertTrue(campi.values().stream().anyMatch(campus -> campus.getNome().equals(nome)));
	}

	@Entao("o campus {string} está inativo")
	public void campusFicaInativo(String nome) {
		assertFalse(buscar(nome).estaAtivo());
	}

	@Entao("o campus {string} está ativo")
	public void campusFicaAtivo(String nome) {
		assertTrue(buscar(nome).estaAtivo());
	}

	@Entao("a disponibilidade é recusada")
	public void disponibilidadeRecusada() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("a sessão é recusada")
	public void sessaoRecusada() {
		assertNotNull(excecaoCapturada);
	}
}
