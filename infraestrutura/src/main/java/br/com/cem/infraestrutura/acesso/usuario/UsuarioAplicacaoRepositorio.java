package br.com.cem.infraestrutura.acesso.usuario;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.cem.aplicacao.acesso.usuario.UsuarioConsulta;
import br.com.cem.aplicacao.acesso.usuario.UsuarioOrdenacao;
import br.com.cem.aplicacao.acesso.usuario.UsuarioProjecao;
import br.com.cem.aplicacao.acesso.usuario.spi.IUsuarioAplicacaoRepositorio;

import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class UsuarioAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<UsuarioProjecao, UsuarioOrdenacao, UsuarioConsulta>
		implements IUsuarioAplicacaoRepositorio {
	private final IUsuarioRepositorioJpa repositorioJpa;

	public UsuarioAplicacaoRepositorio(IUsuarioRepositorioJpa repositorioJpa) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected Page<UsuarioProjecao> pesquisar(UsuarioConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);

		var nome = paraLike(consulta.getNome());
		var papel = consulta.getPapel().orElse(null);
		var situacao = consulta.getSituacao().orElse(null);
		return repositorioJpa.pesquisar(nome, papel, situacao, pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(UsuarioOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);

		return switch (ordenacao) {
		case NOME -> "nome";
		case EMAIL -> "email";
		case PAPEL -> "papel";
		case SITUACAO -> "situacao";
		};
	}
}
