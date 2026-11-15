package br.com.cem.dominio.comunicacao.pergunta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.*;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.faq.FaqServico;
import br.com.cem.dominio.comunicacao.pergunta.spi.IPerguntaRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.time.LocalDateTime;
import java.util.Optional;

public final class PerguntaStepDefinitions {
	private final ISegurancaServico seguranca = mock(ISegurancaServico.class);
	private final IUsuarioRepositorio usuarios = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogio = mock(IRelogioServico.class);
	private final IPerguntaRepositorio repositorio = mock(IPerguntaRepositorio.class);
	private final FaqServico faq = mock(FaqServico.class);
	private final IEnviadorEmailServico email = mock(IEnviadorEmailServico.class);
	private final PerguntaServico servico =
			new PerguntaServico(seguranca, repositorio, usuarios, relogio, faq, email);
	private final LocalDateTime agora = LocalDateTime.of(2026, 11, 13, 12, 0);
	private final UsuarioIdentificador autor = new UsuarioIdentificador("1");
	private final PerguntaIdentificador identificador = new PerguntaIdentificador("1");
	private RuntimeException excecaoCapturada;
	private Pergunta pergunta;

	@Dado("o ambiente de pergunta preparado")
	public void preparar() {
		when(relogio.agora()).thenReturn(agora);
		when(seguranca.getUsuario()).thenReturn("ana@aluno.cem.edu.br");
		var usuario =
				new Usuario(
						Optional.of(autor),
						"Ana",
						new UsuarioEmail("ana@aluno.cem.edu.br"),
						new UsuarioSenha("Cem@2026!"),
						Autorizacao.ALUNO,
						null,
						null,
						UsuarioSituacao.ATIVO);
		when(usuarios.buscarPorEmail(any())).thenReturn(usuario);
		when(usuarios.buscarPorIdentificador(autor)).thenReturn(usuario);
		when(seguranca.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		pergunta =
				new Pergunta(
						Optional.of(identificador),
						autor,
						"Como participar da monitoria?",
						"PENDENTE",
						Optional.empty(),
						agora);
		when(repositorio.existePorIdentificador(identificador)).thenReturn(true);
		when(repositorio.buscarPorIdentificador(identificador)).thenAnswer(chamada -> pergunta);
		doAnswer(
						chamada -> {
							pergunta = chamada.getArgument(0);
							return null;
						})
				.when(repositorio)
				.alterar(any());
	}

	@Quando("edito minha pergunta enquanto está pendente")
	public void editarPerguntaPendente() {
		servico.editar(identificador, "Como confirmar a presença?");
	}

	@Então("a nova pergunta é preservada")
	public void verificarEdicao() {
		assertEquals("Como confirmar a presença?", pergunta.getTexto());
		assertEquals(autor, pergunta.getAutor());
		assertEquals(agora, pergunta.getCriadaEm());
	}

	@Quando("envio uma dúvida com apenas dois caracteres")
	public void enviarPerguntaCurta() {
		capturar(() -> servico.enviar("Oi"));
	}

	@Então("a dúvida curta é recusada")
	public void verificarPerguntaCurta() {
		assertInstanceOf(IllegalArgumentException.class, excecaoCapturada);
		verify(repositorio, never()).incluir(any());
	}

	@Quando("respondo uma pergunta e tento tratá-la novamente")
	public void responderPergunta() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		servico.responder(identificador, pergunta.getTexto(), "Entre em minhas sessões.", true);
		capturar(
				() ->
						servico.responder(
								identificador, pergunta.getTexto(), "Outra resposta", true));
	}

	@Então("a primeira resposta permanece e as novas alterações são recusadas")
	public void verificarRespostaDefinitiva() {
		assertEquals(Optional.of("Entre em minhas sessões."), pergunta.getResposta());
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		assertThrows(IllegalStateException.class, () -> pergunta.editar("Outra pergunta?"));
		verify(faq, times(1)).incluir(any());
		verify(email)
				.enviar(
						"ana@aluno.cem.edu.br",
						"Sua dúvida foi respondida",
						"Entre em minhas sessões.");
	}

	@Quando("descarto uma dúvida e tento respondê-la depois")
	public void descartarPergunta() {
		when(seguranca.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
		servico.descartar(identificador);
		capturar(() -> servico.responder(identificador, pergunta.getTexto(), "Resposta", true));
	}

	@Então("a dúvida permanece descartada")
	public void verificarDescarte() {
		assertEquals("DESCARTADA", pergunta.getSituacao());
		assertInstanceOf(NaoAutorizadoExcecao.class, excecaoCapturada);
		verify(faq, never()).incluir(any());
	}

	private void capturar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}
}
