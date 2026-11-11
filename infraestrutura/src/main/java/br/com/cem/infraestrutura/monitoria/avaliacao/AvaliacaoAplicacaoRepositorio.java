package br.com.cem.infraestrutura.monitoria.avaliacao;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoConsulta;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoOrdenacao;
import br.com.cem.aplicacao.monitoria.avaliacao.AvaliacaoProjecao;
import br.com.cem.aplicacao.monitoria.avaliacao.spi.IAvaliacaoAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.monitoria.avaliacao.SessaoIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.aplicacao.impl.ConsultaResultado;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class AvaliacaoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<AvaliacaoProjecao, AvaliacaoOrdenacao, AvaliacaoConsulta>
		implements IAvaliacaoAplicacaoRepositorio {
	private final IAvaliacaoRepositorioJpa repositorioJpa;
	private final ISegurancaServico segurancaServico;
	private final IUsuarioRepositorio usuarioRepositorio;

	public AvaliacaoAplicacaoRepositorio(IAvaliacaoRepositorioJpa repositorioJpa,
			ISegurancaServico segurancaServico, IUsuarioRepositorio usuarioRepositorio) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
		this.segurancaServico = Objects.requireNonNull(segurancaServico);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
	}

	@Override
	protected Page<AvaliacaoProjecao> pesquisar(AvaliacaoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		var autor = usuarioRepositorio.buscarPorEmail(new UsuarioEmail(segurancaServico.getUsuario())).getIdentificador()
				.orElseThrow();
		var sessao = consulta.getSessao().map(SessaoIdentificador::toString).orElse(null);
		return repositorioJpa.pesquisarProprias(Long.valueOf(autor.toString()), sessao, pageable);
	}

	@Override
	public IConsultaResultado<AvaliacaoProjecao> pesquisarDaSessao(AvaliacaoConsulta consulta,
			SessaoIdentificador sessao) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(sessao);
		var pagina = repositorioJpa.pesquisarDaSessao(sessao.toString(), paraPageable(consulta));
		return new ConsultaResultado<>(pagina.getContent(), pagina.getTotalElements());
	}

	@Override
	public double calcularMedia(SessaoIdentificador sessao) {
		Objects.requireNonNull(sessao);
		return repositorioJpa.calcularMedia(sessao.toString());
	}

	@Override
	protected String paraPropriedadeOrdenacao(AvaliacaoOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);
		return switch (ordenacao) {
		case REGISTRADA_EM -> "registradaEm";
		case NOTA -> "nota";
		};
	}

	private Pageable paraPageable(AvaliacaoConsulta consulta) {
		var ordenacao = consulta.getOrdenacao().map(valor -> {
			var direcao = consulta.isDescendente() ? Sort.Direction.DESC : Sort.Direction.ASC;
			return Sort.by(direcao, paraPropriedadeOrdenacao(valor));
		}).orElse(Sort.unsorted());
		return PageRequest.of(consulta.getIndicePagina(), consulta.getTamanhoPagina(), ordenacao);
	}
}
