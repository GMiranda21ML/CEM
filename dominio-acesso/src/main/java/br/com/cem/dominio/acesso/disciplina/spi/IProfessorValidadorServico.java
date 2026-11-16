package br.com.cem.dominio.acesso.disciplina.spi;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

public interface IProfessorValidadorServico {
	boolean isProfessorValido(UsuarioIdentificador professor, CursoIdentificador curso,
			InstituicaoIdentificador instituicao);
}
