package fr.nduheron.poc.springrestapi.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

public class MailSteps {
    @Autowired
    protected JavaMailSender javaMailSender;
    @Autowired
    private MessageSource messageSource;

    @Then("^email password is sent$")
    public void email_password_is_send() throws Throwable {
        ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(capture.capture());
        assertEquals(messageSource.getMessage("user.mdp.create.objet", null, Locale.FRANCE),
                capture.getValue().getSubject());
        assertNotNull(capture.getValue().getText());
    }

    @Then("^email reinit password is sent$")
    public void email_reinit_password_is_send() throws Throwable {
        ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(capture.capture());
        assertEquals(messageSource.getMessage("user.mdp.reinit.objet", null, Locale.ENGLISH),
                capture.getValue().getSubject());
        assertNotNull(capture.getValue().getText());
    }

    @Before
    public void reset() {
        Mockito.reset(javaMailSender);
    }
}
