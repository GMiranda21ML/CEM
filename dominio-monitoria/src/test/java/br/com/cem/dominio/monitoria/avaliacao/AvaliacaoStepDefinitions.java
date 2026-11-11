package br.com.cem.dominio.monitoria.avaliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.spi.IAvaliacaoRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.spi.ISessaoAvaliacaoServico;
import br.com.cem.dominio.spi.IRelogioServico;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class AvaliacaoStepDefinitions {
	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 11, 12, 0);
	private static final UsuarioIdentificador USUARIO_ID = new UsuarioIdentificador("1");

	private final Map<String, Avaliacao> avaliacoes = new LinkedHashMap<>();
	private final Map<String, SessaoAvaliavel> sessoes = new LinkedHashMap<>();
	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final IAvaliacaoRepositorio repositorio = mock(IAvaliacaoRepositorio.class);
	private final ISessaoAvaliacaoServico sessaoServico = mock(ISessaoAvaliacaoServico.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IRelogioServico relogioServico = mock(IRelogioServico.class);
	private final AvaliacaoServico servico = new AvaliacaoServico(segurancaServico, repositorio, sessaoServico,
			usuarioRepositorio, relogioServico);

	private int proximoIdentificador = 1;
	private Avaliacao resultado;
	private RuntimeException excecaoCapturada;
	private double mediaConsultada;

	public AvaliacaoStepDefinitions() {
		when(relogioServico.agora()).thenReturn(AGORA);
		when(segurancaServico.getUsuario()).thenReturn("participante@aluno.cem.edu.br");
		when(segurancaServico.possuiAutorizacao(Autorizacao.ALUNO.name())).thenReturn(true);
		when(usuarioRepositorio.buscarPorEmail(any())).thenReturn(usuarioAtual());
		when(sessaoServico.buscarParaAvaliacao(any(), any()))
				.thenAnswer(chamada -> sessoes.get(chamada.getArgument(0).toString()));
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> avaliacoes.containsKey(chamada.getArgument(0).toString()));
		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			AvaliacaoIdentificador identificador = chamada.getArgument(0);
			var avaliacao = avaliacoes.get(identificador.toString());
			if (avaliacao == null) {
				throw new AgregadoInexistenteExcecao(AvaliacaoServico.IDENTIFICADOR_INEXISTENTE_MODELO.formatted(identificador));
			}
			return avaliacao;
		});
		when(repositorio.existePorSessaoEAutor(any(), any())).thenAnswer(chamada -> {
			SessaoIdentificador sessao = chamada.getArgument(0);
			UsuarioIdentificador autor = chamada.getArgument(1);
			return avaliacoes.values().stream()
					.anyMatch(avaliacao -> avaliacao.getSessao().equals(sessao) && avaliacao.getAutor().equals(autor));
		});
		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Avaliacao avaliacao = chamada.getArgument(0);
			var identificador = new AvaliacaoIdentificador(String.valueOf(proximoIdentificador++));
			var incluida = copiar(avaliacao, Optional.of(identificador));
			avaliacoes.put(identificador.toString(), incluida);
			return incluida;
		});
		doAnswer(chamada -> {
			Avaliacao avaliacao = chamada.getArgument(0);
			avaliacoes.put(avaliacao.getIdentificador().orElseThrow().toString(), avaliacao);
			return null;
		}).when(repositorio).alterar(any());
	}

	private static Usuario usuarioAtual() {
		return new Usuario(Optional.of(USUARIO_ID), "Participante", new UsuarioEmail("participante@aluno.cem.edu.br"),
				new UsuarioSenha("Cem@2026!"), Autorizacao.ALUNO, "Computação", null, UsuarioSituacao.ATIVO);
	}

	private static Avaliacao copiar(Avaliacao avaliacao, Optional<AvaliacaoIdentificador> identificador) {
		return new Avaliacao(identificador, avaliacao.getSessao(), avaliacao.getAutor(), avaliacao.getPerfil(),
				avaliacao.getNota(), avaliacao.getComentario(), avaliacao.getRegistradaEm(), avaliacao.getAtualizadaEm());
	}

	@Dado("que o aluno participou da sessão {string} realizada há {int} dias")
	public void alunoParticipou(String sessao, int dias) {
		definirSessao(sessao, true, true, AvaliacaoPerfil.ALUNO, AGORA.minusDays(dias));
	}

	@Dado("que o monitor ministrou a sessão {string} realizada há {int} dia")
	@Dado("que o monitor ministrou a sessão {string} realizada há {int} dias")
	public void monitorMinistrou(String sessao, int dias) {
		definirSessao(sessao, true, true, AvaliacaoPerfil.MONITOR, AGORA.minusDays(dias));
	}

	@Dado("que o aluno ainda não avaliou a sessão {string}")
	@Dado("que o monitor ainda não avaliou a sessão {string}")
	public void aindaNaoAvaliou(String sessao) {
		avaliacoes.values().removeIf(avaliacao -> avaliacao.getSessao().equals(new SessaoIdentificador(sessao))
				&& avaliacao.getAutor().equals(USUARIO_ID));
	}

	@Dado("que o aluno já avaliou a sessão {string}")
	public void alunoJaAvaliou(String sessao) {
		adicionarAvaliacao(sessao, USUARIO_ID, AvaliacaoPerfil.ALUNO, 5, "Registrada");
	}

	@Dado("que o aluno está inscrito na sessão {string} que ainda não foi realizada")
	public void sessaoNaoRealizada(String sessao) {
		definirSessao(sessao, false, true, AvaliacaoPerfil.ALUNO, AGORA.plusDays(1));
	}

	@Dado("que o aluno faltou à sessão {string} realizada há {int} dias")
	public void alunoFaltou(String sessao, int dias) {
		definirSessao(sessao, true, false, AvaliacaoPerfil.ALUNO, AGORA.minusDays(dias));
	}

	@Dado("que o usuário não participou da sessão {string} realizada há {int} dias")
	public void usuarioNaoParticipou(String sessao, int dias) {
		definirSessao(sessao, true, false, AvaliacaoPerfil.ALUNO, AGORA.minusDays(dias));
	}

	@Dado("que o aluno avaliou a sessão {string} realizada há {int} dias")
	public void alunoAvaliou(String sessao, int dias) {
		definirSessao(sessao, true, true, AvaliacaoPerfil.ALUNO, AGORA.minusDays(dias));
		resultado = adicionarAvaliacao(sessao, USUARIO_ID, AvaliacaoPerfil.ALUNO, 5, "Original");
	}

	@Dado("que a sessão {string} possui avaliações com notas {int} e {int}")
	public void sessaoPossuiAvaliacoes(String sessao, int primeira, int segunda) {
		definirSessao(sessao, true, true, AvaliacaoPerfil.ALUNO, AGORA.minusDays(2));
		adicionarAvaliacao(sessao, new UsuarioIdentificador("2"), AvaliacaoPerfil.ALUNO, primeira, null);
		adicionarAvaliacao(sessao, new UsuarioIdentificador("3"), AvaliacaoPerfil.MONITOR, segunda, null);
	}

	private void definirSessao(String identificador, boolean realizada, boolean participou, AvaliacaoPerfil perfil,
			LocalDateTime realizadaEm) {
		var sessao = new SessaoIdentificador(identificador);
		sessoes.put(identificador, new SessaoAvaliavel(sessao, realizada, realizadaEm, participou, perfil));
	}

	private Avaliacao adicionarAvaliacao(String sessao, UsuarioIdentificador autor, AvaliacaoPerfil perfil, int nota,
			String comentario) {
		var identificador = new AvaliacaoIdentificador(String.valueOf(proximoIdentificador++));
		var realizadaEm = sessoes.get(sessao).realizadaEm();
		var avaliacao = new Avaliacao(Optional.of(identificador), new SessaoIdentificador(sessao), autor, perfil, nota,
				comentario, realizadaEm.plusHours(1), realizadaEm.plusHours(1));
		avaliacoes.put(identificador.toString(), avaliacao);
		return avaliacao;
	}

	@Quando("o aluno avalia a sessão {string} com nota {int} e comentário {string}")
	@Quando("o monitor avalia a sessão {string} com nota {int} e comentário {string}")
	public void avaliarComComentario(String sessao, int nota, String comentario) {
		executar(() -> resultado = servico.registrar(new SessaoIdentificador(sessao), nota, comentario));
	}

	@Quando("o aluno tenta avaliar a sessão {string} com nota {int}")
	public void avaliarComNota(String sessao, int nota) {
		executar(() -> resultado = servico.registrar(new SessaoIdentificador(sessao), nota, null));
	}

	@Quando("o aluno tenta registrar outra avaliação para a sessão {string}")
	@Quando("o aluno tenta avaliar a sessão {string}")
	@Quando("o usuário tenta avaliar a sessão {string}")
	public void tentarAvaliar(String sessao) {
		executar(() -> resultado = servico.registrar(new SessaoIdentificador(sessao), 5, null));
	}

	@Quando("o aluno altera sua avaliação para nota {int} e comentário {string}")
	public void alterarAvaliacao(int nota, String comentario) {
		executar(() -> servico.editar(resultado.getIdentificador().orElseThrow(), nota, comentario));
	}

	@Quando("o aluno tenta alterar sua avaliação")
	public void tentarAlterarAvaliacao() {
		alterarAvaliacao(4, "Alterada");
	}

	@Quando("um participante consulta as avaliações da sessão {string}")
	public void consultarAvaliacoes(String sessao) {
		executar(() -> servico.validarConsultaDaSessao(new SessaoIdentificador(sessao)));
		mediaConsultada = avaliacoes.values().stream()
				.filter(avaliacao -> avaliacao.getSessao().equals(new SessaoIdentificador(sessao)))
				.mapToInt(Avaliacao::getNota).average().orElseThrow();
	}

	private void executar(Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Entao("a avaliação da sessão {string} é registrada para o aluno")
	@Entao("a avaliação da sessão {string} é registrada para o monitor")
	public void avaliacaoRegistrada(String sessao) {
		assertNull(excecaoCapturada);
		assertNotNull(resultado);
		assertEquals(new SessaoIdentificador(sessao), resultado.getSessao());
	}

	@Entao("a avaliação é recusada")
	@Entao("a alteração da avaliação é recusada")
	public void avaliacaoRecusada() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("a avaliação da sessão {string} passa a ter nota {int} e comentário {string}")
	public void avaliacaoAlterada(String sessao, int nota, String comentario) {
		assertNull(excecaoCapturada);
		var alterada = avaliacoes.values().stream()
				.filter(avaliacao -> avaliacao.getSessao().equals(new SessaoIdentificador(sessao))
						&& avaliacao.getAutor().equals(USUARIO_ID))
				.findFirst().orElseThrow();
		assertEquals(nota, alterada.getNota());
		assertEquals(comentario, alterada.getComentario());
	}

	@Entao("a média apresentada é {double}")
	public void mediaApresentada(double media) {
		assertEquals(media, mediaConsultada);
	}
}
