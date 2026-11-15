package br.com.cem.dominio.monitoria.alerta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.dominio.IAgregado;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Alerta implements IAgregado<AlertaIdentificador> {
	private final Optional<AlertaIdentificador> identificador;
	private final UsuarioIdentificador pessoa;
	public static final int PAPEL_TAMANHO = 10;
	private final String papel;
	public static final int DISCIPLINA_TAMANHO = 150;
	private final String disciplina;
	private final boolean acompanhado;
	private final LocalDateTime criadoEm;

	public Alerta(
			Optional<AlertaIdentificador> identificador,
			UsuarioIdentificador pessoa,
			String papel,
			String disciplina,
			boolean acompanhado,
			LocalDateTime criadoEm) {
		this.identificador = Objects.requireNonNull(identificador);
		this.pessoa = Objects.requireNonNull(pessoa);
		Validate.isTrue(Set.of("ALUNO", "MONITOR").contains(papel), "Papel inválido");
		this.papel = papel;
		this.disciplina = Validate.notBlank(disciplina, "Disciplina obrigatória");
		Validate.isTrue(disciplina.length() <= 150, "Disciplina inválida");
		this.acompanhado = acompanhado;
		this.criadoEm = Objects.requireNonNull(criadoEm);
	}

	@Override
	public Optional<AlertaIdentificador> getIdentificador() {
		return identificador;
	}

	public UsuarioIdentificador getPessoa() {
		return pessoa;
	}

	public String getPapel() {
		return papel;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public boolean getAcompanhado() {
		return acompanhado;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public Alerta acompanhar() {
		Validate.validState(!acompanhado, "Alerta já acompanhado");
		return new Alerta(identificador, pessoa, papel, disciplina, true, criadoEm);
	}
}
