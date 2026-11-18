package br.com.cem.aplicacao.monitoria.selecao;

import br.com.cem.aplicacao.monitoria.selecao.spi.ISelecaoAplicacaoRepositorio;
import br.com.cem.dominio.monitoria.selecao.Selecao;
import br.com.cem.dominio.monitoria.selecao.SelecaoAutorizacoes;
import br.com.cem.dominio.monitoria.selecao.SelecaoIdentificador;
import br.com.cem.dominio.monitoria.selecao.SelecaoServico;

import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SelecaoAplicacaoServico
		extends AgregadoAplicacaoServicoAbstrato<
				SelecaoIdentificador, Selecao, SelecaoAutorizacoes, SelecaoProjecao, SelecaoConsulta> {
	private final ISelecaoAplicacaoRepositorio repositorio;
	private final SelecaoServico servico;

	public SelecaoAplicacaoServico(
			ISelecaoAplicacaoRepositorio repositorio, SelecaoServico servico) {
		super(repositorio, servico);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.servico = Objects.requireNonNull(servico);
	}

	public List<DisciplinaProfessorProjecao> listarDisciplinasDoProfessor() {
		if (!servico.criarAutorizacoes(Optional.empty()).getAbrir()) {
			throw new NaoAutorizadoExcecao("Ação não autorizada");
		}
		return repositorio.listarDisciplinasDoProfessor();
	}
}
