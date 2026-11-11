package br.com.cem.aplicacao.monitoria.avaliacao;

import java.time.LocalDateTime;
import java.util.Objects;

import br.com.cem.dominio.monitoria.avaliacao.AvaliacaoPerfil;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

public final class SessaoPendenteProjecao {
	private final SessaoIdentificador sessao;
	private final LocalDateTime realizadaEm;
	private final AvaliacaoPerfil perfil;

	public SessaoPendenteProjecao(SessaoIdentificador sessao, LocalDateTime realizadaEm, AvaliacaoPerfil perfil) {
		this.sessao = Objects.requireNonNull(sessao);
		this.realizadaEm = Objects.requireNonNull(realizadaEm);
		this.perfil = Objects.requireNonNull(perfil);
	}

	public SessaoIdentificador getSessao() {
		return sessao;
	}

	public LocalDateTime getRealizadaEm() {
		return realizadaEm;
	}

	public AvaliacaoPerfil getPerfil() {
		return perfil;
	}
}
