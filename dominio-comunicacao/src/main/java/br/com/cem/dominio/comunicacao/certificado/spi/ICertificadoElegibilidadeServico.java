package br.com.cem.dominio.comunicacao.certificado.spi;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.EvidenciaCertificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;

public interface ICertificadoElegibilidadeServico {
	EvidenciaCertificacao calcular(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, Periodo periodo);
}
