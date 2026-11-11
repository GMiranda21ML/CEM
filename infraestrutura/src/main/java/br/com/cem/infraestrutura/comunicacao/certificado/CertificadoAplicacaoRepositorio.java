package br.com.cem.infraestrutura.comunicacao.certificado;

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.cem.aplicacao.comunicacao.certificado.CertificadoConsulta;
import br.com.cem.aplicacao.comunicacao.certificado.CertificadoOrdenacao;
import br.com.cem.aplicacao.comunicacao.certificado.CertificadoProjecao;
import br.com.cem.aplicacao.comunicacao.certificado.spi.ICertificadoAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.aplicacao.impl.ConsultaResultado;
import dev.sauloaraujo.d5.framework.infraestrutura.AgregadoAplicacaoRepositorioAbstrato;

public class CertificadoAplicacaoRepositorio
		extends AgregadoAplicacaoRepositorioAbstrato<CertificadoProjecao, CertificadoOrdenacao, CertificadoConsulta>
		implements ICertificadoAplicacaoRepositorio {
	private final ICertificadoRepositorioJpa repositorioJpa;

	public CertificadoAplicacaoRepositorio(ICertificadoRepositorioJpa repositorioJpa) {
		this.repositorioJpa = Objects.requireNonNull(repositorioJpa);
	}

	@Override
	protected Page<CertificadoProjecao> pesquisar(CertificadoConsulta consulta, Pageable pageable) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(pageable);
		return pesquisar(consulta, null, pageable);
	}

	@Override
	public IConsultaResultado<CertificadoProjecao> pesquisarDoBeneficiario(CertificadoConsulta consulta,
			UsuarioIdentificador beneficiario) {
		Objects.requireNonNull(consulta);
		Objects.requireNonNull(beneficiario);
		var pagina = pesquisar(consulta, Long.valueOf(beneficiario.toString()), paraPageable(consulta));
		return new ConsultaResultado<>(pagina.getContent(), pagina.getTotalElements());
	}

	private Page<CertificadoProjecao> pesquisar(CertificadoConsulta consulta, @Nullable Long beneficiario,
			Pageable pageable) {
		var tipo = consulta.getTipo().orElse(null);
		var disciplina = consulta.getDisciplina().map(Object::toString).orElse(null);
		return repositorioJpa.pesquisar(beneficiario, tipo, disciplina, pageable);
	}

	@Override
	protected String paraPropriedadeOrdenacao(CertificadoOrdenacao ordenacao) {
		Objects.requireNonNull(ordenacao);
		return switch (ordenacao) {
		case BENEFICIARIO -> "beneficiarioNome";
		case DISCIPLINA -> "disciplinaNome";
		case EMITIDO_EM -> "emitidoEm";
		};
	}

	private Pageable paraPageable(CertificadoConsulta consulta) {
		var ordenacao = consulta.getOrdenacao().map(valor -> {
			var direcao = consulta.isDescendente() ? Sort.Direction.DESC : Sort.Direction.ASC;
			return Sort.by(direcao, paraPropriedadeOrdenacao(valor));
		}).orElse(Sort.unsorted());
		return PageRequest.of(consulta.getIndicePagina(), consulta.getTamanhoPagina(), ordenacao);
	}
}
