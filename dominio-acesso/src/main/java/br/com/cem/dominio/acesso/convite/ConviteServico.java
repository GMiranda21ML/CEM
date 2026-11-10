package br.com.cem.dominio.acesso.convite;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.convite.spi.IConviteRepositorio;
import br.com.cem.dominio.acesso.usuario.Usuario;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioSenha;
import br.com.cem.dominio.acesso.usuario.UsuarioSituacao;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class ConviteServico extends AgregadoServicoAbstrato<ConviteIdentificador, Convite, ConviteAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe convite com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe um convite com o identificador %s";
	public static final String IDENTIFICADOR_INFORMADO = "O identificador do convite é gerado na inclusão e não deve ser informado";
	public static final String PAPEL_INVALIDO = "O convite só pode ser de coordenador ou de professor";
	public static final String SOMENTE_ADMINISTRADOR_CONVIDA_COORDENADOR = "Somente o administrador CEM convida coordenadores";
	public static final String SOMENTE_COORDENADOR_CONVIDA_PROFESSOR = "Somente um coordenador convida professores";
	public static final String EMAIL_PENDENTE_MODELO = "Já existe um convite aguardando aceite para o e-mail %s";
	public static final String EMAIL_CADASTRADO_MODELO = "Já existe uma conta com o e-mail %s";
	public static final String CONVITE_INEXISTENTE_PARA_EMAIL_MODELO = "Não existe convite aguardando aceite para o e-mail %s";
	public static final String CODIGO_INVALIDO = "Código de verificação inválido";
	public static final String CONVITE_EXPIRADO = "O convite expirou";

	private static final String ASSUNTO_CONVITE_MODELO = "Convite para criar sua conta de %s no CEM";

	private final IConviteRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;
	private final IEnviadorEmailServico enviadorEmailServico;

	public ConviteServico(ISegurancaServico segurancaServico, IConviteRepositorio repositorio,
			IUsuarioRepositorio usuarioRepositorio, IEnviadorEmailServico enviadorEmailServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);

		this.repositorio = Objects.requireNonNull(repositorio);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
		this.enviadorEmailServico = Objects.requireNonNull(enviadorEmailServico);
	}

	@Override
	public ConviteAutorizacoes criarAutorizacoes(Optional<ConviteIdentificador> identificador) {
		Objects.requireNonNull(identificador);

		var administrador = getSegurancaServico().possuiAutorizacao(Autorizacao.ADMINISTRADOR.name());
		var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
		var podeConvidar = administrador || coordenador;

		return new ConviteAutorizacoes(podeConvidar, podeConvidar, podeConvidar, podeConvidar, podeConvidar,
				podeConvidar);
	}

	@Override
	protected void validarInclusao(Convite agregado) {
		Objects.requireNonNull(agregado);

		Validate.isTrue(agregado.getIdentificador().isEmpty(), IDENTIFICADOR_INFORMADO);

		if (agregado.getPapel() == Autorizacao.COORDENADOR) {
			Validate.isTrue(getSegurancaServico().possuiAutorizacao(Autorizacao.ADMINISTRADOR.name()),
					SOMENTE_ADMINISTRADOR_CONVIDA_COORDENADOR);
		} else if (agregado.getPapel() == Autorizacao.PROFESSOR) {
			Validate.isTrue(getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name()),
					SOMENTE_COORDENADOR_CONVIDA_PROFESSOR);
		} else {
			throw new IllegalArgumentException(PAPEL_INVALIDO);
		}
	}

	@Override
	protected void validarInclusaoAlteracao(Convite agregado) {
		Objects.requireNonNull(agregado);

		validarInexistencia(repositorio::existePendentePorEmail, agregado.getEmailInstitucional(),
				EMAIL_PENDENTE_MODELO);

		var usuarioEmail = new UsuarioEmail(agregado.getEmailInstitucional().toString());
		validarInexistencia(email -> usuarioRepositorio.existePorEmail(email, Optional.empty()), usuarioEmail,
				EMAIL_CADASTRADO_MODELO);
	}

	/** Monta o convite com um código recém-gerado, inclui e envia o e-mail com esse código. */
	public Convite convidar(ConviteEmail emailInstitucional, Autorizacao papel, String curso) {
		Objects.requireNonNull(emailInstitucional);
		Objects.requireNonNull(papel);
		Objects.requireNonNull(curso);

		var agora = LocalDateTime.now();
		var novoConvite = new Convite(Optional.empty(), emailInstitucional, papel, curso, gerarCodigo(), agora,
				agora.plusDays(Convite.VALIDADE_DIAS), ConviteSituacao.AGUARDANDO_ACEITE);

		var convite = incluir(novoConvite);
		enviarCodigo(convite);
		return convite;
	}

	public void reenviar(ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		validarAutorizacao(Optional.of(identificador), ConviteAutorizacoes::getReenviar);
		validarExistencia(identificador);

		var convite = repositorio.buscarPorIdentificador(identificador);
		var novoConvite = convite.reenviar(gerarCodigo(), LocalDateTime.now());

		repositorio.alterar(novoConvite);
		enviarCodigo(novoConvite);
	}

	public void cancelar(ConviteIdentificador identificador) {
		Objects.requireNonNull(identificador);

		validarAutorizacao(Optional.of(identificador), ConviteAutorizacoes::getCancelar);
		validarExistencia(identificador);

		var convite = repositorio.buscarPorIdentificador(identificador);
		repositorio.alterar(convite.cancelar());
	}

	/** Confere o código sem criar a conta. Ação pública, sem autenticação. */
	public Convite verificar(ConviteEmail emailInstitucional, ConviteCodigo codigo) {
		return validarCodigo(emailInstitucional, codigo);
	}

	/** Valida o código e cria a conta do convidado. Ação pública, sem autenticação. */
	public Usuario aceitar(ConviteEmail emailInstitucional, ConviteCodigo codigo, String nome, UsuarioSenha senha) {
		Objects.requireNonNull(nome);
		Objects.requireNonNull(senha);

		var convite = validarCodigo(emailInstitucional, codigo);

		var usuario = new Usuario(Optional.empty(), nome, new UsuarioEmail(emailInstitucional.toString()), senha,
				convite.getPapel(), convite.getCurso(), null, UsuarioSituacao.ATIVO);
		var usuarioCriado = usuarioRepositorio.incluir(usuario);

		repositorio.alterar(convite.aceitar());

		return usuarioCriado;
	}

	private Convite validarCodigo(ConviteEmail emailInstitucional, ConviteCodigo codigo) {
		Objects.requireNonNull(emailInstitucional);
		Objects.requireNonNull(codigo);

		var conviteEncontrado = repositorio.buscarPendentePorEmail(emailInstitucional);
		Validate.isTrue(conviteEncontrado.isPresent(), CONVITE_INEXISTENTE_PARA_EMAIL_MODELO, emailInstitucional);
		var convite = conviteEncontrado.get();

		Validate.isTrue(convite.getCodigo().equals(codigo), CODIGO_INVALIDO);
		Validate.isTrue(!convite.estaExpirado(LocalDateTime.now()), CONVITE_EXPIRADO);

		return convite;
	}

	private void enviarCodigo(Convite convite) {
		var papel = convite.getPapel() == Autorizacao.COORDENADOR ? "coordenador" : "professor";
		var assunto = String.format(ASSUNTO_CONVITE_MODELO, papel);
		var corpo = "Você foi convidado para acessar o CEM como " + papel + " do curso " + convite.getCurso()
				+ ". Informe o código " + convite.getCodigo() + " em \"Tenho um convite\". O código vale até "
				+ convite.getValidoAte() + ".";

		enviadorEmailServico.enviar(convite.getEmailInstitucional().toString(), assunto, corpo);
	}

	private static ConviteCodigo gerarCodigo() {
		var numero = ThreadLocalRandom.current().nextInt(0, 1_000_000);
		return new ConviteCodigo(String.format("%06d", numero));
	}
}
