package br.com.cem.dominio.acesso.campus;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.spi.ICampusRepositorio;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class CampusServico extends AgregadoServicoAbstrato<CampusIdentificador, Campus, CampusAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe campus com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe um campus com o identificador %s";
	public static final String NOME_EXISTENTE_MODELO = "Já existe um campus com o nome %s na instituição";
	public static final String OUTRA_INSTITUICAO = "O campus não pertence à instituição do coordenador autenticado";
	public static final String SITUACAO_INICIAL_INVALIDA = "O campus precisa ser cadastrado ativo";
	public static final String SITUACAO_ALTERADA_INDEVIDAMENTE = "A situação do campus só pode ser alterada pelas ações de inativação e reativação";
	public static final String CAMPUS_INATIVO = "O campus está inativo e não pode receber novas disponibilidades nem sessões";

	private final ICampusRepositorio repositorio;
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico;

	public CampusServico(ISegurancaServico segurancaServico, ICampusRepositorio repositorio,
			IInstituicaoUsuarioServico instituicaoUsuarioServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.instituicaoUsuarioServico = Objects.requireNonNull(instituicaoUsuarioServico);
	}

	@Override
	public CampusAutorizacoes criarAutorizacoes(Optional<CampusIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
		var pertence = identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())
				|| pertenceAoUsuario(repositorio.buscarPorIdentificador(identificador.get()));
		var gerenciar = coordenador && pertence;
		return new CampusAutorizacoes(false, gerenciar, gerenciar, false, coordenador, gerenciar, gerenciar);
	}

	public Campus cadastrar(String nome, @Nullable String endereco) {
		validarAutorizacao(Optional.empty(), CampusAutorizacoes::getCadastrar);
		var campus = new Campus(Optional.empty(), instituicaoDoUsuario(), nome, endereco, CampusSituacao.ATIVO);
		validarInclusao(campus);
		validarInclusaoAlteracao(campus);
		return repositorio.incluir(campus);
	}

	@Override
	protected void validarInclusao(Campus agregado) {
		Objects.requireNonNull(agregado);
		Validate.isTrue(agregado.getIdentificador().isEmpty(),
				"O identificador do campus é gerado no cadastro e não deve ser informado");
		Validate.isTrue(agregado.estaAtivo(), SITUACAO_INICIAL_INVALIDA);
		Validate.isTrue(agregado.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
	}

	@Override
	protected void validarInclusaoAlteracao(Campus agregado) {
		Objects.requireNonNull(agregado);
		validarInexistencia(
				nome -> repositorio.existePorNome(nome, agregado.getInstituicao(), agregado.getIdentificador()),
				agregado.getNome(), NOME_EXISTENTE_MODELO);
	}

	@Override
	protected void validarAlteracao(Campus agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().orElseThrow();
		var existente = repositorio.buscarPorIdentificador(identificador);
		Validate.isTrue(existente.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
		Validate.isTrue(agregado.getInstituicao().equals(existente.getInstituicao()), OUTRA_INSTITUICAO);
		Validate.isTrue(agregado.getSituacao() == existente.getSituacao(), SITUACAO_ALTERADA_INDEVIDAMENTE);
	}

	public void inativar(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarAutorizacao(Optional.of(identificador), CampusAutorizacoes::getInativar);
		validarExistencia(identificador);
		repositorio.alterar(repositorio.buscarPorIdentificador(identificador).inativar());
	}

	public void reativar(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarAutorizacao(Optional.of(identificador), CampusAutorizacoes::getReativar);
		validarExistencia(identificador);
		repositorio.alterar(repositorio.buscarPorIdentificador(identificador).reativar());
	}

	public boolean estaAtivo(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		validarExistencia(identificador);
		return repositorio.buscarPorIdentificador(identificador).estaAtivo();
	}

	public void validarUsoEmNovaAtividade(CampusIdentificador identificador) {
		Objects.requireNonNull(identificador);
		Validate.validState(estaAtivo(identificador), CAMPUS_INATIVO);
	}

	private boolean pertenceAoUsuario(Campus campus) {
		return campus.getInstituicao().equals(instituicaoDoUsuario());
	}

	private InstituicaoIdentificador instituicaoDoUsuario() {
		return instituicaoUsuarioServico.buscarPorUsuario(getSegurancaServico().getUsuario());
	}
}
