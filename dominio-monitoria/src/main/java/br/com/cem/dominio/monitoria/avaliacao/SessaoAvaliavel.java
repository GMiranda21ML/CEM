package br.com.cem.dominio.monitoria.avaliacao;

import java.time.LocalDateTime;
import java.util.Objects;

public record SessaoAvaliavel(SessaoIdentificador sessao, boolean realizada, LocalDateTime realizadaEm,
		boolean participou, AvaliacaoPerfil perfil) {
	public SessaoAvaliavel {
		Objects.requireNonNull(sessao);
		Objects.requireNonNull(realizadaEm);
		Objects.requireNonNull(perfil);
	}
}
