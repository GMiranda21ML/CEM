package br.com.cem.aplicacao.comunicacao.certificado.spi;

import br.com.cem.aplicacao.comunicacao.certificado.CertificadoConsulta;
import br.com.cem.aplicacao.comunicacao.certificado.CertificadoProjecao;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.aplicacao.spi.IAgregadoAplicacaoRepositorio;

public interface ICertificadoAplicacaoRepositorio
		extends IAgregadoAplicacaoRepositorio<CertificadoProjecao, CertificadoConsulta> {
	IConsultaResultado<CertificadoProjecao> pesquisarDoBeneficiario(CertificadoConsulta consulta,
			UsuarioIdentificador beneficiario);
}
