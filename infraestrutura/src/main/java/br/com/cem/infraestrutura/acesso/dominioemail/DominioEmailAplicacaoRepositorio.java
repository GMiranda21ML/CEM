package br.com.cem.infraestrutura.acesso.dominioemail;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailConsulta;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailOrdenacao;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailProjecao;
import br.com.cem.aplicacao.acesso.dominioemail.spi.IDominioEmailAplicacaoRepositorio;

import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class DominioEmailAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<DominioEmailProjecao, DominioEmailOrdenacao, DominioEmailConsulta>
		implements IDominioEmailAplicacaoRepositorio {
	private final IDominioEmailRepositorioJpa repositorioJpa;

	public DominioEmailAplicacaoRepositorio(IDominioEmailRepositorioJpa repositorioJpa) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected Page<DominioEmailProjecao> pesquisar(DominioEmailConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);

		return repositorioJpa.pesquisar(pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(DominioEmailOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);

		return switch (ordenacao) {
		case DOMINIO -> "dominio";
		};
	}
}
