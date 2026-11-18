package br.com.cem.aplicacao.monitoria.candidatura;

import br.com.cem.aplicacao.monitoria.selecao.SelecaoProjecao;

import java.util.Objects;
import java.util.Optional;

public record SelecaoAbertaProjecao(
		SelecaoProjecao selecao,
		boolean elegivel,
		boolean cursoCompativel,
		boolean limiteDisponivel,
		long monitoriasNoSemestre,
		boolean semCandidaturaAtiva,
		Optional<String> situacaoCandidaturaAtiva,
		boolean dentroDoPrazo,
		Optional<CandidaturaProjecao> minhaCandidatura) {
	public SelecaoAbertaProjecao {
		Objects.requireNonNull(selecao);
		Objects.requireNonNull(situacaoCandidaturaAtiva);
		Objects.requireNonNull(minhaCandidatura);
	}
}
