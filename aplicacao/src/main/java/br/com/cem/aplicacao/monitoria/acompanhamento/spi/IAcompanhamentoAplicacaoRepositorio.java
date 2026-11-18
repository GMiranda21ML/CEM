package br.com.cem.aplicacao.monitoria.acompanhamento.spi;

import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoConsulta;
import br.com.cem.aplicacao.monitoria.acompanhamento.AcompanhamentoProjecao;
import br.com.cem.aplicacao.monitoria.acompanhamento.AlunoDisciplinaOpcaoProjecao;

import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

import java.util.List;

public interface IAcompanhamentoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<AcompanhamentoProjecao, AcompanhamentoConsulta> {
	List<AlunoDisciplinaOpcaoProjecao> listarOpcoesAbertura();
}
