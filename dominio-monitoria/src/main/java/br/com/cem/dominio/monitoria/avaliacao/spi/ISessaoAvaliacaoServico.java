package br.com.cem.dominio.monitoria.avaliacao.spi;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.SessaoAvaliavel;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

public interface ISessaoAvaliacaoServico {
	SessaoAvaliavel buscarParaAvaliacao(SessaoIdentificador sessao, UsuarioIdentificador participante);
}
