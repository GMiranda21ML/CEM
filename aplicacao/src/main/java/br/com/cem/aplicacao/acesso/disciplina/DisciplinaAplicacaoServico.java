package br.com.cem.aplicacao.acesso.disciplina;

import br.com.cem.aplicacao.acesso.disciplina.spi.IDisciplinaAplicacaoRepositorio;
import br.com.cem.dominio.acesso.disciplina.Disciplina;
import br.com.cem.dominio.acesso.disciplina.DisciplinaAutorizacoes;
import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.disciplina.DisciplinaServico;
import br.com.cem.dominio.acesso.curso.CursoIdentificador;
import java.util.List;
import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.NaoAutorizadoExcecao;
import java.util.Optional;

public final class DisciplinaAplicacaoServico extends AgregadoAplicacaoServicoAbstrato<DisciplinaIdentificador,
		Disciplina, DisciplinaAutorizacoes, DisciplinaProjecao, DisciplinaConsulta> {
	private final IDisciplinaAplicacaoRepositorio repositorio;
	private final DisciplinaServico servico;
	public DisciplinaAplicacaoServico(IDisciplinaAplicacaoRepositorio repositorio, DisciplinaServico servico) {
		super(repositorio, servico);
		this.repositorio = repositorio;
		this.servico = servico;
	}
	public List<ProfessorProjecao> buscarProfessores(CursoIdentificador curso) {
		if (!servico.criarAutorizacoes(Optional.empty()).getBuscar()) {
			throw new NaoAutorizadoExcecao("Ação não autorizada");
		}
		return repositorio.buscarProfessores(curso);
	}
}
