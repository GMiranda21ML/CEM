package br.com.cem.dominio.monitoria.acompanhamento.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface IAcompanhamentoRepositorio
		extends IAgregadoRepositorio<AcompanhamentoIdentificador, Acompanhamento> {
	boolean existeAtivoPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);

	boolean existeEncerradoPorAlunoEDisciplina(
			UsuarioIdentificador aluno, DisciplinaIdentificador disciplina);

	boolean existeBloqueioPorEncerramento(
			UsuarioIdentificador aluno,
			String disciplinaNomeOuCodigo,
			UsuarioIdentificador professor);
}
