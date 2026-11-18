package br.com.cem.aplicacao.monitoria.candidatura;

import br.com.cem.aplicacao.monitoria.candidatura.spi.ICandidaturaAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.candidatura.Candidatura;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaAutorizacoes;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaIdentificador;
import br.com.cem.dominio.monitoria.candidatura.CandidaturaServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CandidaturaAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				CandidaturaIdentificador,
				Candidatura,
				CandidaturaAutorizacoes,
				CandidaturaProjecao,
				CandidaturaConsulta> {
	private final ICandidaturaAplicacaoRepositorio repositorio;
	private final CandidaturaServico servico;

	public CandidaturaAplicacaoServico(
			ICandidaturaAplicacaoRepositorio repositorio, CandidaturaServico servico) {
		super(repositorio, servico);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.servico = Objects.requireNonNull(servico);
	}

	public List<SelecaoAbertaProjecao> listarSelecoesAbertasAluno() {
		if (!servico.criarAutorizacoes(Optional.empty()).getCandidatar()) {
			throw new NaoAutorizadoExcecao("Ação não autorizada");
		}
		return repositorio.listarSelecoesAbertasAluno();
	}
}
