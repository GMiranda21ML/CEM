package br.com.cem.ui.backend.seguranca;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import dev.sauloaraujo.d5.framework.ui.Backend;
import dev.sauloaraujo.d5.framework.ui.seguranca.SegurancaConfiguracaoAbstrata;

@Configuration
public class SegurancaConfiguracao extends SegurancaConfiguracaoAbstrata {
	// Autocadastro de aluno e aceite de convite acontecem sem login.
	private static final String[] PUBLICAS = { "/" + Backend.PREFIXO + "acesso/usuario/criar-conta",
			"/" + Backend.PREFIXO + "acesso/convite/verificacao", "/" + Backend.PREFIXO + "acesso/convite/aceitar" };

	@Override
	protected void autorizar(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry autorizacao) {
		autorizacao.requestMatchers(PUBLICAS).permitAll();
		autorizacao.requestMatchers(BACKEND).authenticated();
	}
}
