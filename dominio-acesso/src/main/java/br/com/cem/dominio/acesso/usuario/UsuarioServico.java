package br.com.cem.dominio.acesso.usuario;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;
import br.com.cem.dominio.acesso.dominioemail.spi.IDominioEmailRepositorio;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.spi.IEnviadorEmailServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutenticadoExcecao;

public final class UsuarioServico extends AgregadoServicoAbstrato<UsuarioIdentificador, Usuario, UsuarioAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe usuário com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe um usuário com o identificador %s";
	public static final String IDENTIFICADOR_INFORMADO = "O identificador do usuário é gerado na inclusão e não deve ser informado";
	public static final String EMAIL_EXISTENTE_MODELO = "Já existe um usuário com o e-mail %s";
	public static final String DOMINIO_NAO_LIBERADO_MODELO = "O domínio de e-mail %s não está liberado para autocadastro";
	public static final String SOMENTE_ALUNO_MODELO = "O autocadastro só cria contas de aluno";
	public static final String SITUACAO_INICIAL_INVALIDA = "A conta precisa ser criada com a situação ativa";
	public static final String EMAIL_IMUTAVEL = "O e-mail de um usuário não pode ser alterado";
	public static final String PAPEL_IMUTAVEL = "O perfil de um usuário não pode ser alterado";

	private static final String ASSUNTO_BOAS_VINDAS = "Bem-vindo ao CEM";

	private final IUsuarioRepositorio repositorio;
	private final IDominioEmailRepositorio dominioEmailRepositorio;
	private final IEnviadorEmailServico enviadorEmailServico;

	public UsuarioServico(ISegurancaServico segurancaServico, IUsuarioRepositorio repositorio,
			IDominioEmailRepositorio dominioEmailRepositorio, IEnviadorEmailServico enviadorEmailServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);

		this.repositorio = Objects.requireNonNull(repositorio);
		this.dominioEmailRepositorio = Objects.requireNonNull(dominioEmailRepositorio);
		this.enviadorEmailServico = Objects.requireNonNull(enviadorEmailServico);
	}

	@Override
	public UsuarioAutorizacoes criarAutorizacoes(Optional<UsuarioIdentificador> identificador) {
		Objects.requireNonNull(identificador);

		// O autocadastro de aluno (ação criarConta) é a única ação pública, sem login: por isso
		// a autorização de incluir é sempre verdadeira, e a consulta ao usuário autenticado é
		// protegida, pois quem chama essa ação pode estar anônimo.
		var coordenador = possuiPapel(Autorizacao.COORDENADOR);
		return new UsuarioAutorizacoes(true, coordenador, coordenador, coordenador, coordenador, coordenador);
	}

	private boolean possuiPapel(Autorizacao papel) {
		try {
			return getSegurancaServico().possuiAutorizacao(papel.name());
		} catch (NaoAutenticadoExcecao excecao) {
			return false;
		}
	}

	@Override
	protected void validarInclusao(Usuario agregado) {
		Objects.requireNonNull(agregado);

		Validate.isTrue(agregado.getIdentificador().isEmpty(), IDENTIFICADOR_INFORMADO);
		Validate.isTrue(agregado.getPapel() == Autorizacao.ALUNO, SOMENTE_ALUNO_MODELO);
		Validate.isTrue(agregado.estaAtivo(), SITUACAO_INICIAL_INVALIDA);
	}

	@Override
	protected void validarInclusaoAlteracao(Usuario agregado) {
		Objects.requireNonNull(agregado);

		validarInexistencia(email -> repositorio.existePorEmail(email, agregado.getIdentificador()),
				agregado.getEmailInstitucional(), EMAIL_EXISTENTE_MODELO);

		// Só a inclusão exige domínio liberado: removê-lo depois não afeta contas já criadas (RN009),
		// e o e-mail não muda numa alteração (validarAlteracao garante isso).
		if (agregado.getIdentificador().isEmpty() && agregado.getPapel() == Autorizacao.ALUNO) {
			var dominio = new DominioEmailDominio(agregado.getEmailInstitucional().getDominio());
			validarExistencia(dominioEmailRepositorio::existePorIdentificador, dominio, DOMINIO_NAO_LIBERADO_MODELO);
		}
	}

	@Override
	protected void validarAlteracao(Usuario agregado) {
		Objects.requireNonNull(agregado);

		var identificador = agregado.getIdentificador().orElseThrow();
		var existente = repositorio.buscarPorIdentificador(identificador);

		Validate.isTrue(existente.getEmailInstitucional().equals(agregado.getEmailInstitucional()), EMAIL_IMUTAVEL);
		Validate.isTrue(existente.getPapel() == agregado.getPapel(), PAPEL_IMUTAVEL);
	}

	/** Cria a conta (autocadastro de aluno) e envia o e-mail de boas-vindas. */
	public Usuario criarConta(Usuario agregado) {
		Objects.requireNonNull(agregado);

		var usuario = incluir(agregado);

		var corpo = "Olá, " + usuario.getNome() + "! Sua conta de aluno no CEM foi criada com sucesso.";
		enviadorEmailServico.enviar(usuario.getEmailInstitucional().toString(), ASSUNTO_BOAS_VINDAS, corpo);

		return usuario;
	}

	public void inativar(UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		validarAutorizacao(Optional.of(identificador), UsuarioAutorizacoes::getInativar);
		validarExistencia(identificador);

		var usuario = repositorio.buscarPorIdentificador(identificador);
		repositorio.alterar(usuario.inativar());
	}

	public void reativar(UsuarioIdentificador identificador) {
		Objects.requireNonNull(identificador);

		validarAutorizacao(Optional.of(identificador), UsuarioAutorizacoes::getReativar);
		validarExistencia(identificador);

		var usuario = repositorio.buscarPorIdentificador(identificador);
		repositorio.alterar(usuario.reativar());
	}
}
