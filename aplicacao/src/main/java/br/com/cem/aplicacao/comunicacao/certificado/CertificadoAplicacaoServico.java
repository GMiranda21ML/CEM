package br.com.cem.aplicacao.comunicacao.certificado;

import java.util.Objects;

import br.com.cem.aplicacao.comunicacao.certificado.spi.ICertificadoAplicacaoRepositorio;
import br.com.cem.dominio.acesso.usuario.UsuarioEmail;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.acesso.usuario.spi.IUsuarioRepositorio;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoAutorizacoes;
import br.com.cem.dominio.comunicacao.certificado.CertificadoIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.aplicacao.impl.AgregadoAplicacaoServicoAbstrato;
import dev.sauloaraujo.d5.framework.dominio.spi.ISegurancaServico;

public final class CertificadoAplicacaoServico extends
		AgregadoAplicacaoServicoAbstrato<CertificadoIdentificador, Certificado, CertificadoAutorizacoes, CertificadoProjecao, CertificadoConsulta> {
	private final ICertificadoAplicacaoRepositorio repositorio;
	private final ISegurancaServico segurancaServico;
	private final IUsuarioRepositorio usuarioRepositorio;

	public CertificadoAplicacaoServico(ICertificadoAplicacaoRepositorio repositorio, CertificadoServico servico,
			ISegurancaServico segurancaServico, IUsuarioRepositorio usuarioRepositorio) {
		super(repositorio, servico);
		this.repositorio = Objects.requireNonNull(repositorio);
		this.segurancaServico = Objects.requireNonNull(segurancaServico);
		this.usuarioRepositorio = Objects.requireNonNull(usuarioRepositorio);
	}

	public IConsultaResultado<CertificadoProjecao> pesquisarMeus(CertificadoConsulta consulta) {
		Objects.requireNonNull(consulta);
		return repositorio.pesquisarDoBeneficiario(consulta, usuarioAtual());
	}

	private UsuarioIdentificador usuarioAtual() {
		return usuarioRepositorio.buscarPorEmail(new UsuarioEmail(segurancaServico.getUsuario())).getIdentificador()
				.orElseThrow();
	}
}
