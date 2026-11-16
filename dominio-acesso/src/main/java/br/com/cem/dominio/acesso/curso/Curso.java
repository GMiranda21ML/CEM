package br.com.cem.dominio.acesso.curso;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Curso implements IAgregado<CursoIdentificador> {
	public static final int NOME_TAMANHO = 150;
	public static final int SIGLA_TAMANHO = 20;

	private final Optional<CursoIdentificador> identificador;
	private final InstituicaoIdentificador instituicao;
	private final String nome;
	private final String sigla;

	public Curso(Optional<CursoIdentificador> identificador, InstituicaoIdentificador instituicao, String nome,
			String sigla) {
		this.identificador = Objects.requireNonNull(identificador, "Identificador não pode ser nulo");
		this.instituicao = Objects.requireNonNull(instituicao, "Instituição não pode ser nula");
		this.nome = Validate.notBlank(nome, "Nome do curso em branco");
		Validate.isTrue(nome.length() <= NOME_TAMANHO, "Nome do curso maior que %d caracteres", NOME_TAMANHO);
		this.sigla = Validate.notBlank(sigla, "Sigla do curso em branco");
		Validate.isTrue(sigla.length() <= SIGLA_TAMANHO, "Sigla do curso maior que %d caracteres", SIGLA_TAMANHO);
	}

	@Override
	public Optional<CursoIdentificador> getIdentificador() {
		return identificador;
	}

	public InstituicaoIdentificador getInstituicao() {
		return instituicao;
	}

	public String getNome() {
		return nome;
	}

	public String getSigla() {
		return sigla;
	}

	public Curso alterarDados(String novoNome, String novaSigla) {
		return new Curso(identificador, instituicao, novoNome, novaSigla);
	}
}
