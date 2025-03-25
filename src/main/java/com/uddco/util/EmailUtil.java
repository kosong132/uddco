package com.uddco.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

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

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        // Validate inputs
        if (toEmail == null || toEmail.isEmpty() || resetLink == null || resetLink.isEmpty()) {
            throw new IllegalArgumentException("Email and reset link cannot be null or empty");
        }

        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost); // SMTP Host
        props.put("mail.smtp.port", smtpPort); // TLS Port
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Create a MimeMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Reset Your Password");

            // HTML content for the email body
            String htmlContent = "<h1>Reset Your Password</h1>"
                    + "<p>Click the link below to reset your password:</p>"
                    + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                    + "<p>If you did not request this, please ignore this email.</p>";

            message.setContent(htmlContent, "text/html");

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}