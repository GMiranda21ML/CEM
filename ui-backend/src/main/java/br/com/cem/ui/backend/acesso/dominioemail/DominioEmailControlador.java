package br.com.cem.ui.backend.acesso.dominioemail;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailAplicacaoServico;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailConsulta;
import br.com.cem.aplicacao.acesso.dominioemail.DominioEmailProjecao;
import br.com.cem.dominio.acesso.dominioemail.DominioEmail;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailDominio;
import br.com.cem.dominio.acesso.dominioemail.DominioEmailServico;

import dev.sauloaraujo.d5.framework.dominio.impl.AgregadoAutorizacoes;
import dev.sauloaraujo.d5.framework.ui.AgregadoControladorAbstrato;
import dev.sauloaraujo.d5.framework.ui.Backend;

@RestController
@RequestMapping(Backend.PREFIXO + "acesso/dominio-email")
public class DominioEmailControlador extends
		AgregadoControladorAbstrato<DominioEmailDominio, DominioEmail, AgregadoAutorizacoes<DominioEmail>, DominioEmailProjecao, DominioEmailConsulta> {
	public DominioEmailControlador(DominioEmailServico servico, DominioEmailAplicacaoServico servicoAplicacao) {
		super(servico, servicoAplicacao);
	}
}
