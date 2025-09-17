package br.com.sol_do_amanhecer.util;

import br.com.sol_do_amanhecer.exception.UsuarioException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailUtil - Testes de Unidade")
class EmailUtilTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private Logger logger;

    @InjectMocks
    private EmailUtil emailUtil;

    private static final String DESTINATARIO = "teste@exemplo.com";
    private static final String ASSUNTO = "Assunto de Teste";
    private static final String MENSAGEM = "Mensagem de teste";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailUtil, "LOGGER", logger);
    }

    @Test
    @DisplayName("Deve enviar email com sucesso")
    void deveEnviarEmailComSucesso() {
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        
        emailUtil.enviarEmail(DESTINATARIO, ASSUNTO, MENSAGEM);
        
        verify(logger).info("Enviando e-mail para: {}", DESTINATARIO);
        verify(logger).info("E-mail enviado com sucesso para: {}", DESTINATARIO);
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);

        verify(logger, never()).error(anyString(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando JavaMailSender.createMimeMessage() falha")
    void deveLancarExcecaoQuandoCreateMimeMessageFalha() {
        
        RuntimeException causaOriginal = new RuntimeException("Erro ao criar MimeMessage");
        when(javaMailSender.createMimeMessage()).thenThrow(causaOriginal);

        
        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> emailUtil.enviarEmail(DESTINATARIO, ASSUNTO, MENSAGEM));

        assertEquals("Falha ao enviar e-mail.", exception.getMessage());

        verify(logger).info("Enviando e-mail para: {}", DESTINATARIO);
        verify(logger).error("Erro ao enviar e-mail para: {}. Exceção: {}",
                DESTINATARIO, causaOriginal.getMessage(), causaOriginal);
        verify(logger, never()).info("E-mail enviado com sucesso para: {}", DESTINATARIO);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando JavaMailSender.send() falha")
    void deveLancarExcecaoQuandoSendFalha() {
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        MailException causaOriginal = mock(MailException.class);
        when(causaOriginal.getMessage()).thenReturn("Erro ao enviar email");
        doThrow(causaOriginal).when(javaMailSender).send(any(MimeMessage.class));

        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> emailUtil.enviarEmail(DESTINATARIO, ASSUNTO, MENSAGEM));

        assertEquals("Falha ao enviar e-mail.", exception.getMessage());

        verify(logger).info("Enviando e-mail para: {}", DESTINATARIO);
        verify(logger).error("Erro ao enviar e-mail para: {}. Exceção: {}",
                DESTINATARIO, causaOriginal.getMessage(), causaOriginal);
        verify(logger, never()).info("E-mail enviado com sucesso para: {}", DESTINATARIO);
    }

    @Test
    @DisplayName("Deve lançar UsuarioException quando MessagingException ocorre")
    void deveLancarExcecaoQuandoMessagingExceptionOcorre() {
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("Erro de configuração")).when(javaMailSender).send(any(MimeMessage.class));

        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> emailUtil.enviarEmail(DESTINATARIO, ASSUNTO, MENSAGEM));

        assertEquals("Falha ao enviar e-mail.", exception.getMessage());

        verify(logger).info("Enviando e-mail para: {}", DESTINATARIO);
        verify(logger).error(eq("Erro ao enviar e-mail para: {}. Exceção: {}"),
                eq(DESTINATARIO), anyString(), any(Exception.class));
        verify(logger, never()).info("E-mail enviado com sucesso para: {}", DESTINATARIO);
    }

    @Test
    @DisplayName("Deve processar corretamente diferentes tipos de parâmetros")
    void deveProcessarDiferentesTiposDeParametros() {
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        String destinatarioComCaracteresEspeciais = "usuario+teste@dominio.com.br";
        String assuntoComCaracteresEspeciais = "Assunto com acentos: ção, ã, ê";
        String mensagemComHTML = "<p>Mensagem com <strong>HTML</strong></p>";

        emailUtil.enviarEmail(destinatarioComCaracteresEspeciais, assuntoComCaracteresEspeciais, mensagemComHTML);

        verify(logger).info("Enviando e-mail para: {}", destinatarioComCaracteresEspeciais);
        verify(logger).info("E-mail enviado com sucesso para: {}", destinatarioComCaracteresEspeciais);
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar parâmetros nulos sem falhar na configuração inicial")
    void deveTratarParametrosNulos() {
        RuntimeException causaOriginal = new RuntimeException("Parâmetro inválido");
        when(javaMailSender.createMimeMessage()).thenThrow(causaOriginal);

        UsuarioException exception = assertThrows(UsuarioException.class,
                () -> emailUtil.enviarEmail(null, null, null));

        assertEquals("Falha ao enviar e-mail.", exception.getMessage());

        verify(logger).info("Enviando e-mail para: {}", (Object) null);
        verify(logger).error("Erro ao enviar e-mail para: {}. Exceção: {}",
                null, causaOriginal.getMessage(), causaOriginal);
        verify(logger, never()).info("E-mail enviado com sucesso para: {}", (Object) null);
    }
}