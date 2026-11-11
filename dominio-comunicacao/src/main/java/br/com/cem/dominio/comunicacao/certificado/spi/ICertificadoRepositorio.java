package br.com.cem.dominio.comunicacao.certificado.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;

import dev.sauloaraujo.d5.framework.dominio.spi.IAgregadoRepositorio;

public interface ICertificadoRepositorio extends IAgregadoRepositorio<CertificadoIdentificador, Certificado> {
	boolean existePorCodigo(CodigoVerificacao codigo);

	Certificado buscarPorCodigo(CodigoVerificacao codigo);

	boolean existePorIdentidade(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, Periodo periodo);
}
