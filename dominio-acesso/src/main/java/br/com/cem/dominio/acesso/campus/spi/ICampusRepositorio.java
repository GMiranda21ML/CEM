package br.com.cem.dominio.acesso.campus.spi;

import java.util.Optional;

import br.com.cem.dominio.acesso.campus.Campus;
import br.com.cem.dominio.acesso.campus.CampusIdentificador;
import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface ICampusRepositorio extends IAgregadoRepositorio<CampusIdentificador, Campus> {
	boolean existePorNome(String nome, InstituicaoIdentificador instituicao,
			Optional<CampusIdentificador> identificadorIgnorado);
}
