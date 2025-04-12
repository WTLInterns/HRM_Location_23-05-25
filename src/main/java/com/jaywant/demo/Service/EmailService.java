package com.jaywant.demo.Service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

  public boolean sendHtmlEmail(String message, String subject, String to) {
    boolean f = false;
    String from = "jaywantmhala928@gmail.com";
    String host = "smtp.gmail.com";

    Properties properties = System.getProperties();
    System.out.println(properties);

    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", 465);
    properties.put("mail.smtp.ssl.enable", true);
    properties.put("mail.smtp.auth", true);

    Session session = Session.getInstance(properties, new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("jaywantmhala928@gmail.com", "mcjd ebsg arvj ftca");
      }

    });

    session.setDebug(true);
    MimeMessage m = new MimeMessage(session);

    try {
      m.setFrom(from);
      m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      m.setSubject(subject);
      m.setText(message);
      m.setContent(message, "text/html");

      Transport.send(m);
      f = true;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return f;

  }
}
