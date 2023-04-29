package fr.nduheron.poc.springrestapi.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class MailSteps {
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;

    public MailSteps(JavaMailSender javaMailSender, MessageSource messageSource) {
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
    }

    @Then("email password is sent")
    public void email_password_is_send() {
        ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(capture.capture());
        assertThat(capture.getValue().getSubject()).isEqualTo(messageSource.getMessage("user.mdp.create.objet", null, Locale.FRANCE));
        assertThat(capture.getValue().getText()).isNotNull();
    }

    @Then("email reinit password is sent")
    public void email_reinit_password_is_send() {
        ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(capture.capture());
        assertThat(capture.getValue().getSubject()).isEqualTo(messageSource.getMessage("user.mdp.reinit.objet", null, Locale.ENGLISH));
        assertThat(capture.getValue().getText()).isNotNull();
    }

    @Before
    public void reset() {
        Mockito.reset(javaMailSender);
    }
}
