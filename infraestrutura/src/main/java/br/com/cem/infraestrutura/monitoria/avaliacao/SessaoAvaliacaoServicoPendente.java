package br.com.cem.infraestrutura.monitoria.avaliacao;

import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.SessaoAvaliavel;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;
import br.com.cem.dominio.monitoria.avaliacao.spi.ISessaoAvaliacaoServico;

@Component
public final class SessaoAvaliacaoServicoPendente implements ISessaoAvaliacaoServico {
	@Override
	public SessaoAvaliavel buscarParaAvaliacao(SessaoIdentificador sessao, UsuarioIdentificador participante) {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(participante);
		throw new IllegalStateException("A integração com sessões e presenças ainda não está disponível");
	}
}
