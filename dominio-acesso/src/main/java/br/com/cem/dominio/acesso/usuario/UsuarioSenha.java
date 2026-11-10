package br.com.cem.dominio.acesso.usuario;

import org.apache.commons.lang3.Validate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import dev.sauloaraujo.d5.framework.dominio.impl.ObjetoValorStringAbstrato;

public final class UsuarioSenha extends ObjetoValorStringAbstrato {
	public static final int TAMANHO = 100;

	private static final String FORMATO_FORTE = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

	public static final String SENHA_FRACA = "A senha precisa ter 8 ou mais caracteres, com letra maiúscula, letra minúscula, número e símbolo";

	public UsuarioSenha(String valor) {
		super(paraValorArmazenado(valor));
	}

	// Uma senha já cifrada (lida do banco) começa com "{", prefixo do codificador delegante do
	// Spring Security (por exemplo, "{bcrypt}..."). Nesse caso ela é mantida como está, para não
	// ser cifrada de novo toda vez que o agregado é reconstruído a partir da persistência.
	private static String paraValorArmazenado(String valor) {
		Validate.notNull(valor);

		if (valor.startsWith("{")) {
			return valor;
		}

		Validate.matchesPattern(valor, FORMATO_FORTE, SENHA_FRACA);
		return PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(valor);
	}
}
