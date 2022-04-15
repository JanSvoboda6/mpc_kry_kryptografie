package com.web.security.verification;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService
{
    private final ITemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public EmailService(ITemplateEngine templateEngine, JavaMailSender mailSender)
    {

        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailContext emailContext)
    {
        try
        {
            String body = templateEngine.process(emailContext.getTemplateLocation(), emailContext.getContext());
            MimeMessage message = mailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, emailContext.getTo());
            message.setSubject(emailContext.getSubject());
            message.setFrom(emailContext.getFrom());
            message.setContent(body, "text/html; charset=utf-8");
            mailSender.send(message);

        } catch (MessagingException e)
        {
            throw new RuntimeException("Exception occurred while sending email!");
        }
    }
}
