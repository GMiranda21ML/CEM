package br.com.cem.aplicacao.monitoria.candidatura.spi;

import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaConsulta;
import br.com.cem.aplicacao.monitoria.candidatura.CandidaturaProjecao;
import br.com.cem.aplicacao.monitoria.candidatura.SelecaoAbertaProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

import java.util.List;

public interface ICandidaturaAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<CandidaturaProjecao, CandidaturaConsulta> {
	List<SelecaoAbertaProjecao> listarSelecoesAbertasAluno();
}
