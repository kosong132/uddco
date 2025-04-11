package com.uddco.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Value("${email.from}") // Load from application.properties
    private String fromEmail;

    @Value("${email.password}") // Load from application.properties
    private String password;

    @Value("${email.smtp.host}") // Load from application.properties
    private String smtpHost;

    @Value("${email.smtp.port}") // Load from application.properties
    private String smtpPort;

    public void sendResetPasswordEmail(String toEmail, String message, boolean isMobile) {
    if (toEmail == null || toEmail.isEmpty() || message == null || message.isEmpty()) {
        throw new IllegalArgumentException("Email and message cannot be null or empty");
    }

    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(fromEmail, password);
        }
    });

    try {
        MimeMessage emailMessage = new MimeMessage(session);
        emailMessage.setFrom(new InternetAddress(fromEmail));
        emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        emailMessage.setSubject("Reset Your Password");

        if (isMobile) {
            emailMessage.setText(message); // plain text for mobile OTP
        } else {
            emailMessage.setContent(message, "text/html"); // HTML for web reset link
        }

        Transport.send(emailMessage);
        System.out.println("Email sent to: " + toEmail);
    } catch (Exception e) {
        System.err.println("Failed to send email: " + e.getMessage());
        throw new RuntimeException("Failed to send email", e);
    }
}

}