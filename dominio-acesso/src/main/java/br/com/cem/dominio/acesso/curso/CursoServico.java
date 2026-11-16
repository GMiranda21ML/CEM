package br.com.cem.dominio.acesso.curso;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class CursoServico extends AgregadoServicoAbstrato<CursoIdentificador, Curso, CursoAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe curso com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe um curso com o identificador %s";
	public static final String NOME_EXISTENTE_MODELO = "Já existe um curso com o nome %s na instituição";
	public static final String SIGLA_EXISTENTE_MODELO = "Já existe um curso com a sigla %s na instituição";
	public static final String OUTRA_INSTITUICAO = "O curso não pertence à instituição do coordenador autenticado";
	public static final String POSSUI_DISCIPLINAS = "O curso possui disciplinas vinculadas e não pode ser excluído";
	public static final String POSSUI_USUARIOS = "O curso possui usuários vinculados e não pode ser excluído";

	private final ICursoRepositorio repositorio;
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico;

	public CursoServico(ISegurancaServico segurancaServico, ICursoRepositorio repositorio,
			IInstituicaoUsuarioServico instituicaoUsuarioServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.instituicaoUsuarioServico = Objects.requireNonNull(instituicaoUsuarioServico);
	}

	@Override
	public CursoAutorizacoes criarAutorizacoes(Optional<CursoIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
		var pertence = identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())
				|| pertenceAoUsuario(repositorio.buscarPorIdentificador(identificador.get()));
		var gerenciar = coordenador && pertence;
		return new CursoAutorizacoes(false, gerenciar, gerenciar, gerenciar, coordenador);
	}

	public Curso cadastrar(String nome, String sigla) {
		validarAutorizacao(Optional.empty(), CursoAutorizacoes::getCadastrar);
		var curso = new Curso(Optional.empty(), instituicaoDoUsuario(), nome, sigla);
		validarInclusao(curso);
		validarInclusaoAlteracao(curso);
		return repositorio.incluir(curso);
	}

	@Override
	protected void validarInclusao(Curso agregado) {
		Objects.requireNonNull(agregado);
		Validate.isTrue(agregado.getIdentificador().isEmpty(),
				"O identificador do curso é gerado no cadastro e não deve ser informado");
		Validate.isTrue(agregado.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
	}

	@Override
	protected void validarInclusaoAlteracao(Curso agregado) {
		Objects.requireNonNull(agregado);
		validarInexistencia(
				nome -> repositorio.existePorNome(nome, agregado.getInstituicao(), agregado.getIdentificador()),
				agregado.getNome(), NOME_EXISTENTE_MODELO);
		validarInexistencia(
				sigla -> repositorio.existePorSigla(sigla, agregado.getInstituicao(), agregado.getIdentificador()),
				agregado.getSigla(), SIGLA_EXISTENTE_MODELO);
	}

	@Override
	protected void validarAlteracao(Curso agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().orElseThrow();
		var existente = repositorio.buscarPorIdentificador(identificador);
		Validate.isTrue(existente.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
		Validate.isTrue(agregado.getInstituicao().equals(existente.getInstituicao()), OUTRA_INSTITUICAO);
	}

	@Override
	protected void validarExclusao(CursoIdentificador identificador) {
		Objects.requireNonNull(identificador);
		Validate.isTrue(!repositorio.possuiDisciplinasVinculadas(identificador), POSSUI_DISCIPLINAS);
		Validate.isTrue(!repositorio.possuiUsuariosVinculados(identificador), POSSUI_USUARIOS);
	}

	private boolean pertenceAoUsuario(Curso curso) {
		return curso.getInstituicao().equals(instituicaoDoUsuario());
	}

	private InstituicaoIdentificador instituicaoDoUsuario() {
		return instituicaoUsuarioServico.buscarPorUsuario(getSegurancaServico().getUsuario());
	}
}
