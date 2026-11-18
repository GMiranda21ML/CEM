package br.com.cem.dominio.monitoria.selecao.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

import java.util.Optional;

public interface ISelecaoRepositorio extends IAgregadoRepositorio<SelecaoIdentificador, Selecao> {
	boolean existeAbertaPorDisciplina(
			DisciplinaIdentificador disciplina, Optional<SelecaoIdentificador> identificadorIgnorado);
}
