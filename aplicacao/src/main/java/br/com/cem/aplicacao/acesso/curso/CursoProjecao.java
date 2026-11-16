package br.com.cem.aplicacao.acesso.curso;

import java.util.Objects;

import br.com.cem.dominio.acesso.curso.Curso;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class CursoProjecao implements IProjecao<Curso> {
	private final CursoIdentificador identificador;
	private final InstituicaoIdentificador instituicao;
	private final String nome;
	private final String sigla;
	private final long quantidadeDisciplinas;
	private final long quantidadeUsuarios;

	public CursoProjecao(CursoIdentificador identificador, InstituicaoIdentificador instituicao, String nome,
			String sigla, long quantidadeDisciplinas, long quantidadeUsuarios) {
		this.identificador = Objects.requireNonNull(identificador);
		this.instituicao = Objects.requireNonNull(instituicao);
		this.nome = Objects.requireNonNull(nome);
		this.sigla = Objects.requireNonNull(sigla);
		this.quantidadeDisciplinas = quantidadeDisciplinas;
		this.quantidadeUsuarios = quantidadeUsuarios;
	}

	public CursoProjecao(Long identificador, String instituicao, String nome, String sigla,
			Long quantidadeDisciplinas, Long quantidadeUsuarios) {
		this(new CursoIdentificador(identificador.toString()), new InstituicaoIdentificador(instituicao), nome, sigla,
				quantidadeDisciplinas, quantidadeUsuarios);
	}

	public CursoIdentificador getIdentificador() { return identificador; }
	public InstituicaoIdentificador getInstituicao() { return instituicao; }
	public String getNome() { return nome; }
	public String getSigla() { return sigla; }
	public long getQuantidadeDisciplinas() { return quantidadeDisciplinas; }
	public long getQuantidadeUsuarios() { return quantidadeUsuarios; }
}
