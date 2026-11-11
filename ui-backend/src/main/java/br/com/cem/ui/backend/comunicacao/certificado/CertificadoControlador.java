package br.com.cem.ui.backend.comunicacao.certificado;

import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.comunicacao.certificado.CertificadoAplicacaoServico;
import br.com.cem.aplicacao.comunicacao.certificado.CertificadoConsulta;
import br.com.cem.aplicacao.comunicacao.certificado.CertificadoProjecao;
import br.com.cem.dominio.comunicacao.certificado.Certificado;
import br.com.cem.dominio.comunicacao.certificado.CertificadoAutorizacoes;
import br.com.cem.dominio.comunicacao.certificado.CertificadoIdentificador;
import br.com.cem.dominio.comunicacao.certificado.CertificadoServico;
import br.com.cem.dominio.comunicacao.certificado.CodigoVerificacao;

import dev.sauloaraujo.d5.framework.aplicacao.IConsultaResultado;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "comunicacao/certificado")
public class CertificadoControlador extends
		AgregadoControladorAbstrato<CertificadoIdentificador, Certificado, CertificadoAutorizacoes, CertificadoProjecao, CertificadoConsulta> {
	private final CertificadoServico servico;
	private final CertificadoAplicacaoServico servicoAplicacao;

	public CertificadoControlador(CertificadoServico servico, CertificadoAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
		this.servico = Objects.requireNonNull(servico);
		this.servicoAplicacao = Objects.requireNonNull(servicoAplicacao);
	}

	@PostMapping("emitir")
	public Certificado emitir(@RequestBody EmitirCertificadoRequisicao requisicao) {
		Objects.requireNonNull(requisicao);
		return servico.emitir(requisicao.beneficiario(), requisicao.tipo(), requisicao.disciplina(),
				requisicao.periodo());
	}

	@GetMapping("meus")
	public IConsultaResultado<CertificadoProjecao> pesquisarMeus(@ModelAttribute CertificadoConsulta consulta) {
		return servicoAplicacao.pesquisarMeus(Objects.requireNonNull(consulta));
	}

	@GetMapping("validacao/{codigo}")
	public CertificadoPublicoRecurso validar(@PathVariable CodigoVerificacao codigo) {
		return new CertificadoPublicoRecurso(servico.validar(Objects.requireNonNull(codigo)));
	}
}
