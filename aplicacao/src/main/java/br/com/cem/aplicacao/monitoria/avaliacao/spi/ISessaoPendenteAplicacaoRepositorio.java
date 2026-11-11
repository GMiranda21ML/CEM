package br.com.cem.aplicacao.monitoria.avaliacao.spi;

import java.util.List;

import br.com.cem.aplicacao.monitoria.avaliacao.SessaoPendenteProjecao;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

public interface ISessaoPendenteAplicacaoRepositorio {
	List<SessaoPendenteProjecao> pesquisar(UsuarioIdentificador participante);
}
