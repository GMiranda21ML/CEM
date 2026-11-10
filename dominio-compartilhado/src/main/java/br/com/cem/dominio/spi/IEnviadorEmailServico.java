package br.com.cem.dominio.spi;

public interface IEnviadorEmailServico {
	void enviar(String destinatario, String assunto, String corpo);
}
