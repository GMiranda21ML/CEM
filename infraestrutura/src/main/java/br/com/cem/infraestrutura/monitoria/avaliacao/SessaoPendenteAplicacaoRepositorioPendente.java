package br.com.cem.infraestrutura.monitoria.avaliacao;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.cem.aplicacao.monitoria.avaliacao.SessaoPendenteProjecao;
import br.com.cem.aplicacao.monitoria.avaliacao.spi.ISessaoPendenteAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

@Component
public final class SessaoPendenteAplicacaoRepositorioPendente implements ISessaoPendenteAplicacaoRepositorio {
	@Override
	public List<SessaoPendenteProjecao> pesquisar(UsuarioIdentificador participante) {
		Objects.requireNonNull(participante);
		throw new IllegalStateException("A integração para consultar sessões pendentes ainda não está disponível");
	}
}
