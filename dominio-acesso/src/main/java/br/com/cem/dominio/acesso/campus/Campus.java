package br.com.cem.dominio.acesso.campus;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

public final class Campus implements IAgregado<CampusIdentificador> {
	public static final int NOME_TAMANHO = 150;
	public static final int ENDERECO_TAMANHO = 250;

	private final Optional<CampusIdentificador> identificador;
	private final InstituicaoIdentificador instituicao;
	private final String nome;
	private final @Nullable String endereco;
	private final CampusSituacao situacao;

	public Campus(Optional<CampusIdentificador> identificador, InstituicaoIdentificador instituicao, String nome,
			@Nullable String endereco, CampusSituacao situacao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.instituicao = Objects.requireNonNull(instituicao);
		this.nome = Validate.notBlank(nome, "Nome do campus em branco");
		Validate.isTrue(nome.length() <= NOME_TAMANHO, "Nome do campus maior que %d caracteres", NOME_TAMANHO);
		if (endereco != null) {
			Validate.isTrue(endereco.length() <= ENDERECO_TAMANHO, "Endereço do campus maior que %d caracteres",
					ENDERECO_TAMANHO);
		}
		this.endereco = endereco;
		this.situacao = Objects.requireNonNull(situacao);
	}

	@Override
	public Optional<CampusIdentificador> getIdentificador() {
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

	public boolean estaAtivo() {
		return situacao == CampusSituacao.ATIVO;
	}

	public Campus alterarDados(String novoNome, @Nullable String novoEndereco) {
		return new Campus(identificador, instituicao, novoNome, novoEndereco, situacao);
	}

	public Campus inativar() {
		Validate.validState(estaAtivo(), "O campus já está inativo");
		return new Campus(identificador, instituicao, nome, endereco, CampusSituacao.INATIVO);
	}

	public Campus reativar() {
		Validate.validState(!estaAtivo(), "O campus já está ativo");
		return new Campus(identificador, instituicao, nome, endereco, CampusSituacao.ATIVO);
	}
}
