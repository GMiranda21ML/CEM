package br.com.cem.dominio.monitoria.falta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Falta implements IAgregado<FaltaIdentificador> {
	private final Optional<FaltaIdentificador> identificador;
	private final SessaoIdentificador sessao;
	private final UsuarioIdentificador pessoa;
	public static final int PAPEL_TAMANHO = 10;
	private final String papel;
	private final UsuarioIdentificador autor;
	public static final int OBSERVACAO_TAMANHO = 1000;
	private final Optional<String> observacao;
	private final LocalDateTime registradaEm;
	private final Optional<SessaoIdentificador> reposicao;

	public Falta(
			Optional<FaltaIdentificador> identificador,
			SessaoIdentificador sessao,
			UsuarioIdentificador pessoa,
			String papel,
			UsuarioIdentificador autor,
			Optional<String> observacao,
			LocalDateTime registradaEm,
			Optional<SessaoIdentificador> reposicao) {
		this.identificador = Objects.requireNonNull(identificador);
		this.sessao = Objects.requireNonNull(sessao);
		this.pessoa = Objects.requireNonNull(pessoa);
		Validate.isTrue(Set.of("ALUNO", "MONITOR").contains(papel), "Papel da falta inválido");
		this.papel = papel;
		this.autor = Objects.requireNonNull(autor);
		this.observacao =
				Objects.requireNonNull(observacao)
						.map(String::strip)
						.filter(texto -> !texto.isEmpty());
		this.observacao.ifPresent(
				texto ->
						Validate.isTrue(
								texto.length() <= OBSERVACAO_TAMANHO,
								"Observação deve ter até %d caracteres",
								OBSERVACAO_TAMANHO));
		this.registradaEm = Objects.requireNonNull(registradaEm);
		this.reposicao = Objects.requireNonNull(reposicao);
		Validate.isTrue(
				reposicao.isEmpty() || deAluno(), "Somente falta de aluno admite reposição");
	}

	@Override
	public Optional<FaltaIdentificador> getIdentificador() {
		return identificador;
	}

	public SessaoIdentificador getSessao() {
		return sessao;
	}

	public UsuarioIdentificador getPessoa() {
		return pessoa;
	}

	public String getPapel() {
		return papel;
	}

	public UsuarioIdentificador getAutor() {
		return autor;
	}

	public Optional<String> getObservacao() {
		return observacao;
	}

	public LocalDateTime getRegistradaEm() {
		return registradaEm;
	}

	public Falta editar(Optional<String> observacao) {
		return new Falta(
				identificador, sessao, pessoa, papel, autor, observacao, registradaEm, reposicao);
	}

	public boolean deAluno() {
		return papel.equals("ALUNO");
	}

	public Optional<SessaoIdentificador> getReposicao() {
		return reposicao;
	}

	public Falta agendarReposicao(SessaoIdentificador reposicao) {
		Objects.requireNonNull(reposicao);
		return new Falta(
				identificador,
				sessao,
				pessoa,
				papel,
				autor,
				observacao,
				registradaEm,
				Optional.of(reposicao));
	}
}
