package com.web.security.verification;

import com.web.security.user.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService
{
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public EmailService(TemplateEngine templateEngine, JavaMailSender mailSender)
    {

        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void sendEmail(User user, Context context, String template)
    {

    }
}
