package br.com.cem.aplicacao.acesso.disciplina;

import java.util.Objects;
import java.util.Optional;

import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import dev.sauloaraujo.d5.framework.aplicacao.impl.Consulta;

public final class DisciplinaConsulta extends Consulta<DisciplinaOrdenacao> {
	private final Optional<String> texto;
	private final Optional<CursoIdentificador> curso;

	public DisciplinaConsulta(Optional<DisciplinaOrdenacao> ordenacao, Optional<Boolean> descendente,
			Optional<Integer> indicePagina, Optional<Integer> tamanhoPagina, Optional<String> texto,
			Optional<CursoIdentificador> curso) {
		super(ordenacao, descendente, indicePagina, tamanhoPagina);
		this.texto = Objects.requireNonNull(texto);
		this.curso = Objects.requireNonNull(curso);
	}

	public Optional<String> getTexto() { return texto; }
	public Optional<CursoIdentificador> getCurso() { return curso; }
}
