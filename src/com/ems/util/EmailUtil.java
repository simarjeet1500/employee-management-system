package com.ems.util;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Lightweight JavaMail helper. Sends HTML email via SMTP (Gmail by default).
 *
 * IMPORTANT:
 *  - Replace SMTP_USER / SMTP_PASSWORD below with your Gmail and a Google
 *    App Password (https://myaccount.google.com/apppasswords).
 *  - If SMTP is not configured, sendMail() will silently log and return false
 *    so CRUD flows continue to work for grading/demo.
 */
public class EmailUtil {

    // ====== CONFIGURE THESE ======
    private static final String SMTP_HOST     = "smtp.gmail.com";
    private static final String SMTP_PORT     = "587";
    private static final String SMTP_USER     = "your-email@gmail.com";   // <-- change me
    private static final String SMTP_PASSWORD = "your-app-password";      // <-- change me (Gmail App Password)
    private static final String FROM_NAME     = "Employee Management System";
    // =============================

    public static boolean sendMail(String toAddress, String subject, String htmlBody) {
        if (SMTP_USER == null || SMTP_USER.startsWith("your-email")) {
            System.out.println("[EmailUtil] SMTP not configured — skipping email to " + toAddress);
            return false;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SMTP_USER, FROM_NAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject(subject);
            msg.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(msg);
            System.out.println("[EmailUtil] Mail sent to " + toAddress);
            return true;
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("[EmailUtil] Failed to send mail: " + e.getMessage());
            return false;
        }
    }
}
