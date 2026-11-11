package br.com.cem.infraestrutura.comunicacao.certificado;

import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.cem.dominio.acesso.disciplina.DisciplinaIdentificador;
import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoTipo;
import br.com.cem.dominio.comunicacao.certificado.EvidenciaCertificacao;
import br.com.cem.dominio.comunicacao.certificado.Periodo;
import br.com.cem.dominio.comunicacao.certificado.spi.ICertificadoElegibilidadeServico;

@Component
public final class CertificadoElegibilidadeServicoPendente implements ICertificadoElegibilidadeServico {
	@Override
	public EvidenciaCertificacao calcular(UsuarioIdentificador beneficiario, CertificadoTipo tipo,
			DisciplinaIdentificador disciplina, Periodo periodo) {
		Objects.requireNonNull(beneficiario);
		Objects.requireNonNull(tipo);
		Objects.requireNonNull(disciplina);
		Objects.requireNonNull(periodo);
		throw new IllegalStateException("A integração com disciplinas, sessões e presenças ainda não está disponível");
	}
}
