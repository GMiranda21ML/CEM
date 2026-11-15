package br.com.cem.ui.backend.monitoria.falta;

import br.com.cem.dominio.acesso.usuario.UsuarioIdentificador;
import br.com.cem.dominio.monitoria.sessao.SessaoIdentificador;

import java.util.Optional;

public record RegistrarFaltaRequisicao(
		SessaoIdentificador sessao,
		UsuarioIdentificador pessoa,
		String papel,
		Optional<String> observacao) {}
