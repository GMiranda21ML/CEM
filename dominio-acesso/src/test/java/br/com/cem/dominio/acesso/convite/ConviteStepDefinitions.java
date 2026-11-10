package br.com.cem.dominio.acesso.convite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.spi.IConviteRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;

import dev.sauloaraujo.d5.framework.dominio.AgregadoInexistenteExcecao;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public final class ConviteStepDefinitions {
	private static final String SENHA_FORTE = "Cem@2026!";
	private static final String CURSO_PADRAO = "Ciência da Computação";

	private final Map<String, Convite> convites = new HashMap<>();
	private final Map<String, Usuario> usuarios = new HashMap<>();

	private final ISegurancaServico segurancaServico = mock(ISegurancaServico.class);
	private final IConviteRepositorio repositorio = mock(IConviteRepositorio.class);
	private final IUsuarioRepositorio usuarioRepositorio = mock(IUsuarioRepositorio.class);
	private final IEnviadorEmailServico enviadorEmailServico = mock(IEnviadorEmailServico.class);
	private final ConviteServico servico = new ConviteServico(segurancaServico, repositorio, usuarioRepositorio,
			enviadorEmailServico);

	private int proximoIdentificadorConvite = 1;
	private int proximoIdentificadorUsuario = 1;
	private Convite resultadoConvite;
	private Usuario resultadoAceite;
	private RuntimeException excecaoCapturada;

	public ConviteStepDefinitions() {
		when(repositorio.existePorIdentificador(any()))
				.thenAnswer(chamada -> convites.containsKey(chaveConvite(chamada.getArgument(0))));

		when(repositorio.buscarPorIdentificador(any())).thenAnswer(chamada -> {
			ConviteIdentificador identificador = chamada.getArgument(0);
			var convite = convites.get(chaveConvite(identificador));
			if (convite == null) {
				throw new AgregadoInexistenteExcecao(
						String.format(ConviteServico.IDENTIFICADOR_INEXISTENTE_MODELO, identificador));
			}
			return convite;
		});

		when(repositorio.existePendentePorEmail(any()))
				.thenAnswer(chamada -> buscarPendente(chamada.getArgument(0)) != null);

		when(repositorio.buscarPendentePorEmail(any()))
				.thenAnswer(chamada -> Optional.ofNullable(buscarPendente(chamada.getArgument(0))));

		when(repositorio.incluir(any())).thenAnswer(chamada -> {
			Convite convite = chamada.getArgument(0);
			var identificador = new ConviteIdentificador(String.valueOf(proximoIdentificadorConvite++));
			var incluido = new Convite(Optional.of(identificador), convite.getEmailInstitucional(),
					convite.getPapel(), convite.getCurso(), convite.getCodigo(), convite.getEnviadoEm(),
					convite.getValidoAte(), convite.getSituacao());
			convites.put(chaveConvite(identificador), incluido);
			return incluido;
		});

		doAnswer(chamada -> {
			Convite convite = chamada.getArgument(0);
			convites.put(chaveConvite(convite.getIdentificador().orElseThrow()), convite);
			return null;
		}).when(repositorio).alterar(any());

		when(usuarioRepositorio.existePorEmail(any(), any()))
				.thenAnswer(chamada -> usuarios.containsKey(chaveUsuario(chamada.getArgument(0))));

		when(usuarioRepositorio.incluir(any())).thenAnswer(chamada -> {
			Usuario usuario = chamada.getArgument(0);
			var identificador = new UsuarioIdentificador(String.valueOf(proximoIdentificadorUsuario++));
			var incluido = new Usuario(Optional.of(identificador), usuario.getNome(), usuario.getEmailInstitucional(),
					usuario.getSenha(), usuario.getPapel(), usuario.getCurso(), usuario.getCampus(),
					usuario.getSituacao());
			usuarios.put(chaveUsuario(incluido.getEmailInstitucional()), incluido);
			return incluido;
		});
	}

	private static String chaveConvite(ConviteIdentificador identificador) {
		return identificador.toString();
	}

	private static String chaveUsuario(UsuarioEmail email) {
		return email.toString();
	}

	private Convite buscarPendente(ConviteEmail email) {
		for (var convite : convites.values()) {
			if (convite.getEmailInstitucional().equals(email) && convite.estaAguardandoAceite()) {
				return convite;
			}
		}
		return null;
	}

	private Convite buscarConvitePorEmail(String email) {
		for (var convite : convites.values()) {
			if (convite.getEmailInstitucional().equals(new ConviteEmail(email))) {
				return convite;
			}
		}
		throw new IllegalStateException("Convite não encontrado para o e-mail " + email);
	}

	private void comoAdministrador() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.ADMINISTRADOR.name())).thenReturn(true);
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(false);
	}

	private void comoCoordenador() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.ADMINISTRADOR.name())).thenReturn(false);
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	private void comoAutorizado() {
		when(segurancaServico.possuiAutorizacao(Autorizacao.ADMINISTRADOR.name())).thenReturn(true);
		when(segurancaServico.possuiAutorizacao(Autorizacao.COORDENADOR.name())).thenReturn(true);
	}

	private void incluirConviteExistente(String email, String codigo, ConviteSituacao situacao, int diasAtras) {
		var enviadoEm = LocalDateTime.now().minusDays(diasAtras);
		var identificador = new ConviteIdentificador(String.valueOf(proximoIdentificadorConvite++));
		var convite = new Convite(Optional.of(identificador), new ConviteEmail(email), Autorizacao.PROFESSOR,
				CURSO_PADRAO, new ConviteCodigo(codigo), enviadoEm, enviadoEm.plusDays(Convite.VALIDADE_DIAS),
				situacao);
		convites.put(chaveConvite(identificador), convite);
	}

	@Dado("que já existe uma conta com o e-mail {string}")
	public void que_ja_existe_uma_conta_com_o_email(String email) {
		var identificador = new UsuarioIdentificador(String.valueOf(proximoIdentificadorUsuario++));
		var usuario = new Usuario(Optional.of(identificador), "Marta Siqueira", new UsuarioEmail(email),
				new UsuarioSenha(SENHA_FORTE), Autorizacao.COORDENADOR, CURSO_PADRAO, null, UsuarioSituacao.ATIVO);
		usuarios.put(chaveUsuario(usuario.getEmailInstitucional()), usuario);
	}

	@Dado("que existe um convite para {string} aguardando aceite com código {string}")
	public void que_existe_um_convite_aguardando_aceite_com_codigo(String email, String codigo) {
		incluirConviteExistente(email, codigo, ConviteSituacao.AGUARDANDO_ACEITE, 0);
	}

	@Dado("que existe um convite para {string} aguardando aceite com código {string} enviado há {int} dias")
	public void que_existe_um_convite_aguardando_aceite_enviado_ha_dias(String email, String codigo, int dias) {
		incluirConviteExistente(email, codigo, ConviteSituacao.AGUARDANDO_ACEITE, dias);
	}

	@Dado("que existe um convite para {string} aguardando aceite")
	public void que_existe_um_convite_aguardando_aceite(String email) {
		incluirConviteExistente(email, "111111", ConviteSituacao.AGUARDANDO_ACEITE, 0);
	}

	@Dado("que existe um convite para {string} aceito")
	public void que_existe_um_convite_aceito(String email) {
		incluirConviteExistente(email, "111111", ConviteSituacao.ACEITO, 0);
	}

	@Dado("que existe um convite para {string} cancelado")
	public void que_existe_um_convite_cancelado(String email) {
		incluirConviteExistente(email, "111111", ConviteSituacao.CANCELADO, 0);
	}

	@Quando("o administrador convida {string} para coordenador do curso {string}")
	public void o_administrador_convida_para_coordenador(String email, String curso) {
		comoAdministrador();
		convidar(email, Autorizacao.COORDENADOR, curso);
	}

	@Quando("o administrador tenta convidar {string} para professor do curso {string}")
	public void o_administrador_tenta_convidar_para_professor(String email, String curso) {
		comoAdministrador();
		convidar(email, Autorizacao.PROFESSOR, curso);
	}

	@Quando("o administrador tenta convidar {string} para coordenador do curso {string}")
	public void o_administrador_tenta_convidar_para_coordenador(String email, String curso) {
		comoAdministrador();
		convidar(email, Autorizacao.COORDENADOR, curso);
	}

	@Quando("o coordenador convida {string} para professor do curso {string}")
	public void o_coordenador_convida_para_professor(String email, String curso) {
		comoCoordenador();
		convidar(email, Autorizacao.PROFESSOR, curso);
	}

	@Quando("o coordenador tenta convidar {string} para coordenador do curso {string}")
	public void o_coordenador_tenta_convidar_para_coordenador(String email, String curso) {
		comoCoordenador();
		convidar(email, Autorizacao.COORDENADOR, curso);
	}

	private void convidar(String email, Autorizacao papel, String curso) {
		try {
			resultadoConvite = servico.convidar(new ConviteEmail(email), papel, curso);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o convite de {string} é reenviado")
	public void o_convite_e_reenviado(String email) {
		comoAutorizado();
		try {
			servico.reenviar(buscarConvitePorEmail(email).getIdentificador().orElseThrow());
			resultadoConvite = buscarConvitePorEmail(email);
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("o convite de {string} é cancelado")
	public void o_convite_e_cancelado(String email) {
		comoAutorizado();
		try {
			servico.cancelar(buscarConvitePorEmail(email).getIdentificador().orElseThrow());
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("tenta-se cancelar o convite de {string}")
	public void tenta_se_cancelar_o_convite(String email) {
		comoAutorizado();
		try {
			servico.cancelar(buscarConvitePorEmail(email).getIdentificador().orElseThrow());
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Quando("{string} aceita o convite com o código {string}, o nome {string} e uma senha forte")
	public void aceita_o_convite(String email, String codigo, String nome) {
		aceitar(email, codigo, nome, SENHA_FORTE);
	}

	@Quando("{string} tenta aceitar o convite com o código {string}")
	public void tenta_aceitar_o_convite_com_codigo(String email, String codigo) {
		aceitar(email, codigo, "Convidado", SENHA_FORTE);
	}

	@Quando("{string} tenta aceitar o convite")
	public void tenta_aceitar_o_convite(String email) {
		aceitar(email, "000000", "Convidado", SENHA_FORTE);
	}

	private void aceitar(String email, String codigo, String nome, String senha) {
		try {
			resultadoAceite = servico.aceitar(new ConviteEmail(email), new ConviteCodigo(codigo), nome,
					new UsuarioSenha(senha));
		} catch (RuntimeException excecao) {
			excecaoCapturada = excecao;
		}
	}

	@Entao("existe um convite para {string} aguardando aceite")
	public void existe_um_convite_aguardando_aceite(String email) {
		assertEquals(ConviteSituacao.AGUARDANDO_ACEITE, buscarConvitePorEmail(email).getSituacao());
	}

	@Entao("o convite tem um código de verificação de 6 dígitos válido por 7 dias")
	public void o_convite_tem_codigo_valido_por_7_dias() {
		assertNotNull(resultadoConvite);
		assertTrue(resultadoConvite.getCodigo().toString().matches("\\d{6}"));
		assertEquals(resultadoConvite.getEnviadoEm().plusDays(Convite.VALIDADE_DIAS), resultadoConvite.getValidoAte());
	}

	@Entao("o convite é recusado")
	public void o_convite_e_recusado() {
		assertNotNull(excecaoCapturada);
	}

	@Entao("o convite de {string} tem um código de verificação diferente de {string}, válido por mais 7 dias")
	public void o_convite_tem_codigo_diferente(String email, String codigoAntigo) {
		assertNull(excecaoCapturada);
		var convite = buscarConvitePorEmail(email);
		assertNotEquals(new ConviteCodigo(codigoAntigo), convite.getCodigo());
		assertEquals(convite.getEnviadoEm().plusDays(Convite.VALIDADE_DIAS), convite.getValidoAte());
	}

	@Entao("o convite de {string} está cancelado")
	public void o_convite_esta_cancelado(String email) {
		assertEquals(ConviteSituacao.CANCELADO, buscarConvitePorEmail(email).getSituacao());
	}

	@Entao("uma conta é criada para {string}")
	public void uma_conta_e_criada_para(String email) {
		assertNull(excecaoCapturada);
		assertNotNull(resultadoAceite);
		assertEquals(new UsuarioEmail(email), resultadoAceite.getEmailInstitucional());
	}

	@Entao("o convite de {string} está aceito")
	public void o_convite_esta_aceito(String email) {
		assertEquals(ConviteSituacao.ACEITO, buscarConvitePorEmail(email).getSituacao());
	}
}
