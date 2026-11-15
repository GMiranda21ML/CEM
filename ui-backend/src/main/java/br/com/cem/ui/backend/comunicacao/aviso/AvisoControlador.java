package br.com.cem.ui.backend.comunicacao.aviso;

import br.com.cem.aplicacao.comunicacao.aviso.AvisoAplicacaoServico;
import br.com.cem.aplicacao.comunicacao.aviso.AvisoConsulta;
import br.com.cem.aplicacao.comunicacao.aviso.AvisoProjecao;
import br.com.cem.dominio.comunicacao.aviso.Aviso;
import br.com.cem.dominio.comunicacao.aviso.AvisoAutorizacoes;
import br.com.cem.dominio.comunicacao.aviso.AvisoIdentificador;
import br.com.cem.dominio.comunicacao.aviso.AvisoServico;

import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(Backend.PREFIXO + "comunicacao/aviso")
public class AvisoControlador
		extends AgregadoControladorAbstrato<
				AvisoIdentificador, Aviso, AvisoAutorizacoes, AvisoProjecao, AvisoConsulta> {
	private final AvisoServico servico;

	public AvisoControlador(AvisoServico servico, AvisoAplicacaoServico aplicacao) {
		super(servico, aplicacao);
		this.servico = Objects.requireNonNull(servico);
	}

	@PostMapping("publicar")
	public Aviso publicar(@RequestBody PublicarAvisoRequisicao dados) {
		Objects.requireNonNull(dados);
		return servico.publicar(dados.titulo(), dados.texto(), dados.validade());
	}

	@PostMapping("{identificador}/alternar")
	public void alternar(@PathVariable AvisoIdentificador identificador) {
		servico.alternar(identificador);
	}
}
