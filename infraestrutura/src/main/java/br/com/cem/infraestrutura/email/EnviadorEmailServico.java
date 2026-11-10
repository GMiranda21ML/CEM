package br.com.cem.infraestrutura.email;

import java.util.Objects;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import br.com.cem.dominio.spi.IEnviadorEmailServico;

@Component
public class EnviadorEmailServico implements IEnviadorEmailServico {
	private final JavaMailSender mailSender;

	public EnviadorEmailServico(JavaMailSender mailSender) {
		this.mailSender = Objects.requireNonNull(mailSender);
	}

	@Override
	public void enviar(String destinatario, String assunto, String corpo) {
		Objects.requireNonNull(destinatario);
		Objects.requireNonNull(assunto);
		Objects.requireNonNull(corpo);

		var mensagem = new SimpleMailMessage();
		mensagem.setTo(destinatario);
		mensagem.setSubject(assunto);
		mensagem.setText(corpo);
		mailSender.send(mensagem);
	}
}
