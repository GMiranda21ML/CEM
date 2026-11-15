package br.com.cem.dominio.comunicacao.faq;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.comunicacao.faq.spi.IFaqRepositorio;

import dev.sauloaraujo.d5.framework.dominio.AgregadoExistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.Optional;

public final class FaqStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IFaqRepositorio repositorio = mock(IFaqRepositorio.class);
	private final FaqServico servico = new FaqServico(seguranca, repositorio);
	private RuntimeException excecaoCapturada;
	private boolean consultaPermitida;

	@Dado("o ambiente de faq preparado")
	public void preparar() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	@Quando("cadastro novamente a mesma pergunta com espaços e maiúsculas")
	public void cadastrarPerguntaRepetida() {
		when(repositorio.existePorPergunta("Dúvida frequente?", Optional.empty())).thenReturn(true);
		capturar(
				() ->
						servico.incluir(
								new Faq(
										Optional.empty(),
										"  Dúvida frequente?  ",
										"Resposta",
										true)));
	}

	@Então("o FAQ recusa a duplicidade")
	public void verificarDuplicidade() {
		assertInstanceOf(AgregadoExistenteExcecao.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("cadastro uma resposta de 1001 caracteres")
	public void cadastrarRespostaLonga() {
		capturar(
				() ->
						servico.incluir(
								new Faq(Optional.empty(), "Pergunta?", "x".repeat(1001), true)));
	}

	@Então("o FAQ recusa a resposta longa")
	public void verificarLimiteResposta() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("um aluno tenta cadastrar uma entrada do FAQ")
	public void cadastrarComoAluno() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		capturar(() -> servico.incluir(new Faq(Optional.empty(), "Pergunta?", "Resposta", true)));
	}

	@Então("o FAQ recusa o cadastro sem autorização")
	public void verificarAutorizacaoCadastro() {
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("consulto as autorizações de leitura do FAQ")
	public void consultarAutorizacoes() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
		consultaPermitida = servico.criarAutorizacoes(Optional.empty()).getBuscar();
	}

	@Então("a consulta ao FAQ é permitida")
	public void verificarConsulta() {
		assertTrue(consultaPermitida);
	}

	private void capturar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}
}
