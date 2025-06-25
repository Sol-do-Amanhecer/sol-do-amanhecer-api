package br.com.sol_do_amanhecer.util;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);
    private final JavaMailSender javaMailSender;

    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        try {
            LOGGER.info("Enviando e-mail para: {}", destinatario);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(mensagem, false);

            javaMailSender.send(mimeMessage);

            LOGGER.info("E-mail enviado com sucesso para: {}", destinatario);
        } catch (Exception e) {
            LOGGER.error("Erro ao enviar e-mail para: {}. Exceção: {}", destinatario, e.getMessage(), e);
            throw new UsuarioException("Falha ao enviar e-mail.");
        }
    }
}