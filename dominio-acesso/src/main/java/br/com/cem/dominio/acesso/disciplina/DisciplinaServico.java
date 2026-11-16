package br.com.cem.dominio.acesso.disciplina;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.Autorizacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.curso.spi.ICursoRepositorio;
import br.com.cem.dominio.acesso.disciplina.spi.IDisciplinaRepositorio;
import br.com.cem.dominio.acesso.disciplina.spi.IProfessorValidadorServico;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class DisciplinaServico
		extends AgregadoServicoAbstrato<DisciplinaIdentificador, Disciplina, DisciplinaAutorizacoes> {
	public static final String IDENTIFICADOR_INEXISTENTE_MODELO = "Não existe disciplina com o identificador %s";
	public static final String IDENTIFICADOR_EXISTENTE_MODELO = "Já existe uma disciplina com o identificador %s";
	public static final String CODIGO_EXISTENTE_MODELO = "Já existe uma disciplina com o código %s na instituição";
	public static final String NOME_EXISTENTE_NO_CURSO_MODELO = "Já existe uma disciplina com o nome %s no curso";
	public static final String CURSO_INEXISTENTE = "O curso informado não existe";
	public static final String CURSO_OUTRA_INSTITUICAO = "O curso informado não pertence à instituição do coordenador autenticado";
	public static final String OUTRA_INSTITUICAO = "A disciplina não pertence à instituição do coordenador autenticado";
	public static final String PROFESSOR_INVALIDO = "O professor responsável deve estar ativo e vinculado ao curso e à instituição da disciplina";
	public static final String POSSUI_VINCULOS_MONITORIA = "A disciplina possui vínculos de monitoria e não pode ser excluída";

	private final IDisciplinaRepositorio repositorio;
	private final ICursoRepositorio cursoRepositorio;
	private final IProfessorValidadorServico professorValidadorServico;
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico;

	public DisciplinaServico(ISegurancaServico segurancaServico, IDisciplinaRepositorio repositorio,
			ICursoRepositorio cursoRepositorio, IProfessorValidadorServico professorValidadorServico,
			IInstituicaoUsuarioServico instituicaoUsuarioServico) {
		super(segurancaServico, repositorio, IDENTIFICADOR_EXISTENTE_MODELO, IDENTIFICADOR_INEXISTENTE_MODELO);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.cursoRepositorio = Objects.requireNonNull(cursoRepositorio);
		this.professorValidadorServico = Objects.requireNonNull(professorValidadorServico);
		this.instituicaoUsuarioServico = Objects.requireNonNull(instituicaoUsuarioServico);
	}

	@Override
	public DisciplinaAutorizacoes criarAutorizacoes(Optional<DisciplinaIdentificador> identificador) {
		Objects.requireNonNull(identificador);
		var coordenador = getSegurancaServico().possuiAutorizacao(Autorizacao.COORDENADOR.name());
		var pertence = identificador.isEmpty() || !repositorio.existePorIdentificador(identificador.get())
				|| pertenceAoUsuario(repositorio.buscarPorIdentificador(identificador.get()));
		var gerenciar = coordenador && pertence;
		return new DisciplinaAutorizacoes(false, gerenciar, gerenciar, gerenciar, coordenador);
	}

	public Disciplina cadastrar(String nome, DisciplinaCodigo codigo, String area, CursoIdentificador curso,
			UsuarioIdentificador professor) {
		validarAutorizacao(Optional.empty(), DisciplinaAutorizacoes::getCadastrar);
		var disciplina = new Disciplina(Optional.empty(), instituicaoDoUsuario(), curso, professor, nome, codigo, area);
		validarInclusao(disciplina);
		validarInclusaoAlteracao(disciplina);
		return repositorio.incluir(disciplina);
	}

	@Override
	protected void validarInclusao(Disciplina agregado) {
		Objects.requireNonNull(agregado);
		Validate.isTrue(agregado.getIdentificador().isEmpty(),
				"O identificador da disciplina é gerado no cadastro e não deve ser informado");
		Validate.isTrue(agregado.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
	}

	@Override
	protected void validarInclusaoAlteracao(Disciplina agregado) {
		Objects.requireNonNull(agregado);
		Validate.isTrue(cursoRepositorio.existePorIdentificador(agregado.getCurso()), CURSO_INEXISTENTE);
		var cursoObj = cursoRepositorio.buscarPorIdentificador(agregado.getCurso());
		Validate.isTrue(cursoObj.getInstituicao().equals(agregado.getInstituicao()), CURSO_OUTRA_INSTITUICAO);

		Validate.isTrue(
				professorValidadorServico.isProfessorValido(agregado.getProfessor(), agregado.getCurso(),
						agregado.getInstituicao()),
				PROFESSOR_INVALIDO);

		validarInexistencia(
				codigo -> repositorio.existePorCodigo(codigo, agregado.getInstituicao(), agregado.getIdentificador()),
				agregado.getCodigo(), CODIGO_EXISTENTE_MODELO);

		validarInexistencia(
				nome -> repositorio.existePorNomeNoCurso(nome, agregado.getCurso(), agregado.getIdentificador()),
				agregado.getNome(), NOME_EXISTENTE_NO_CURSO_MODELO);
	}

	@Override
	protected void validarAlteracao(Disciplina agregado) {
		Objects.requireNonNull(agregado);
		var identificador = agregado.getIdentificador().orElseThrow();
		var existente = repositorio.buscarPorIdentificador(identificador);
		Validate.isTrue(existente.getInstituicao().equals(instituicaoDoUsuario()), OUTRA_INSTITUICAO);
		Validate.isTrue(agregado.getInstituicao().equals(existente.getInstituicao()), OUTRA_INSTITUICAO);
		Validate.isTrue(agregado.getCodigo().equals(existente.getCodigo()),
				"O código da disciplina não pode ser alterado");
		Validate.isTrue(agregado.getCurso().equals(existente.getCurso()),
				"O curso da disciplina não pode ser alterado");
	}

	@Override
	protected void validarExclusao(DisciplinaIdentificador identificador) {
		Objects.requireNonNull(identificador);
		Validate.isTrue(!repositorio.possuiVinculosMonitoria(identificador), POSSUI_VINCULOS_MONITORIA);
	}

	private boolean pertenceAoUsuario(Disciplina disciplina) {
		return disciplina.getInstituicao().equals(instituicaoDoUsuario());
	}

	private InstituicaoIdentificador instituicaoDoUsuario() {
		return instituicaoUsuarioServico.buscarPorUsuario(getSegurancaServico().getUsuario());
	}
}
