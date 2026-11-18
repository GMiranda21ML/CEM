package br.com.cem.aplicacao.monitoria.acompanhamento;

import br.com.cem.aplicacao.monitoria.acompanhamento.spi.IAcompanhamentoAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.acompanhamento.Acompanhamento;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoAutorizacoes;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoIdentificador;
import br.com.cem.dominio.monitoria.acompanhamento.AcompanhamentoServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AcompanhamentoAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				AcompanhamentoIdentificador,
				Acompanhamento,
				AcompanhamentoAutorizacoes,
				AcompanhamentoProjecao,
				AcompanhamentoConsulta> {
	private final IAcompanhamentoAplicacaoRepositorio repositorio;
	private final AcompanhamentoServico servico;

	public AcompanhamentoAplicacaoServico(
			IAcompanhamentoAplicacaoRepositorio repositorio, AcompanhamentoServico servico) {
		super(repositorio, servico);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.servico = Objects.requireNonNull(servico);
	}

	public List<AlunoDisciplinaOpcaoProjecao> listarOpcoesAbertura() {
		if (!servico.criarAutorizacoes(Optional.empty()).getAbrir()) {
			throw new NaoAutorizadoExcecao("Ação não autorizada");
		}
		return repositorio.listarOpcoesAbertura();
	}
}
