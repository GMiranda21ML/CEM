package br.com.cem.dominio.acesso.campus.spi;

import br.com.cem.dominio.acesso.campus.InstituicaoIdentificador;

public interface IInstituicaoUsuarioServico {
	InstituicaoIdentificador buscarPorUsuario(String usuario);
}
