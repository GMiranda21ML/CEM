package br.com.cem.dominio.acesso.dominioemail;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.dominioemail.spi.IDominioEmailRepositorio;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class DominioEmailStepDefinitions {
	private final Map<String, DominioEmail> dominios = new HashMap<>();
	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final IDominioEmailRepositorio repositorio = mock(IDominioEmailRepositorio.class);
	private final DominioEmailServico servico = new DominioEmailServico(segurancaServico, repositorio);

	private RuntimeException excecaoCapturada;

	public DominioEmailStepDefinitions() {
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> dominios.containsKey(chave(chamada.getArgument(0))));

		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			DominioEmail dominioEmail = chamada.getArgument(0);
			dominios.put(chave(dominioEmail.getIdentificador().orElseThrow()), dominioEmail);
			return dominioEmail;
		});

		doAnswer(chamada -> {
			DominioEmailDominio identificador = chamada.getArgument(0);
			dominios.remove(chave(identificador));
			return null;
		}).when(repositorio).excluir(any());
	}

	private static String chave(DominioEmailDominio dominio) {
		return dominio.toString();
	}

	private void comoCoordenador() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	private void comoProfessor() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
	}

	@Dado("que o domínio {string} não está liberado")
	public void que_o_dominio_nao_esta_liberado(String dominio) {
		dominios.remove(dominio);
	}

	@Dado("que o domínio {string} está liberado")
	public void que_o_dominio_esta_liberado(String dominio) {
		dominios.put(dominio, new DominioEmail(new DominioEmailDominio(dominio)));
	}

	@Quando("o coordenador libera o domínio {string}")
	public void o_coordenador_libera_o_dominio(String dominio) {
		comoCoordenador();
		liberar(dominio);
	}

	@Quando("um professor tenta liberar o domínio {string}")
	public void um_professor_tenta_liberar_o_dominio(String dominio) {
		comoProfessor();
		liberar(dominio);
	}

	private void liberar(String dominio) {
		try {
			servico.incluir(new DominioEmail(new DominioEmailDominio(dominio)));
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o coordenador remove o domínio {string}")
	public void o_coordenador_remove_o_dominio(String dominio) {
		comoCoordenador();
		try {
			servico.excluir(new DominioEmailDominio(dominio));
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Entao("o domínio {string} está liberado")
	public void o_dominio_esta_liberado(String dominio) {
		assertTrue(dominios.containsKey(dominio));
	}

	@Entao("o domínio {string} não está mais liberado")
	public void o_dominio_nao_esta_mais_liberado(String dominio) {
		assertFalse(dominios.containsKey(dominio));
	}

	@Entao("a liberação é recusada")
	public void a_liberacao_e_recusada() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
	}
}
