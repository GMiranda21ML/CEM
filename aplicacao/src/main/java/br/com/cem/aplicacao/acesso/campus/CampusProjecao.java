package br.com.cem.aplicacao.acesso.campus;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.CampusSituacao;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IProjecao;

public final class CampusProjecao implements IProjecao<Campus> {
	private final CampusIdentificador identificador;
	private final InstituicaoIdentificador instituicao;
	private final String nome;
	private final @Nullable String endereco;
	private final CampusSituacao situacao;

	public CampusProjecao(CampusIdentificador identificador, InstituicaoIdentificador instituicao, String nome,
			@Nullable String endereco, CampusSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.instituicao = Objects.requireNonNull(instituicao);
		this.nome = Objects.requireNonNull(nome);
		this.endereco = endereco;
		this.situacao = Objects.requireNonNull(situacao);
	}

	public CampusProjecao(Long identificador, String instituicao, String nome, @Nullable String endereco,
			CampusSituacao situacao) {
		this(new CampusIdentificador(identificador.toString()), new InstituicaoIdentificador(instituicao), nome,
				endereco, situacao);
	}

	public CampusIdentificador getIdentificador() {
		return identificador;
	}

	public InstituicaoIdentificador getInstituicao() {
		return instituicao;
	}

	public String getNome() {
		return nome;
	}

	public @Nullable String getEndereco() {
		return endereco;
	}

	public CampusSituacao getSituacao() {
		return situacao;
	}
}
