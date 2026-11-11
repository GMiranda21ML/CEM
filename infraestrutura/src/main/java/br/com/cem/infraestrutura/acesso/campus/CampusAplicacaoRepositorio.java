package br.com.cem.infraestrutura.acesso.campus;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.campus.CampusConsulta;
import br.com.cem.aplicacao.acesso.campus.CampusOrdenacao;
import br.com.cem.aplicacao.acesso.campus.CampusProjecao;
import br.com.cem.aplicacao.acesso.campus.spi.ICampusAplicacaoRepositorio;
import br.com.cem.dominio.acesso.campus.spi.IInstituicaoUsuarioServico;

import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class CampusAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<CampusProjecao, CampusOrdenacao, CampusConsulta>
		implements ICampusAplicacaoRepositorio {
	private final ICampusRepositorioJpa repositorioJpa;
	private final ISegurancaServico segurancaServico;
	private final IInstituicaoUsuarioServico instituicaoUsuarioServico;

	public CampusAplicacaoRepositorio(ICampusRepositorioJpa repositorioJpa, ISegurancaServico segurancaServico,
			IInstituicaoUsuarioServico instituicaoUsuarioServico) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.segurancaServico = Objects.requireNonNull(segurancaServico);
		this.instituicaoUsuarioServico = Objects.requireNonNull(instituicaoUsuarioServico);
	}

	@Override
	protected Page<CampusProjecao> pesquisar(CampusConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		var instituicao = instituicaoUsuarioServico.buscarPorUsuario(segurancaServico.getUsuario()).toString();
		var nome = paraLike(consulta.getNome());
		var situacao = consulta.getSituacao().orElse(null);
		return repositorioJpa.pesquisar(instituicao, nome, situacao, pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(CampusOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);
		return switch (ordenacao) {
		case NOME -> "nome";
		case SITUACAO -> "situacao";
		};
	}
}
