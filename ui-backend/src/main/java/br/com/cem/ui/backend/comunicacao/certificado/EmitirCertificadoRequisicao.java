package br.com.cem.ui.backend.comunicacao.certificado;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.Periodo;

public record EmitirCertificadoRequisicao(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
		DisciplinaIdentificador disciplina, Periodo periodo) {
}
