// 

package com.jaywant.demo.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailService {

  private final String from = "jaywantmhala928@gmail.com";
  private final String host = "smtp.gmail.com";
  private final String password = "mcjd ebsg arvj ftca";

  // Get mail session properties
  private Properties getMailProperties() {
    Properties properties = System.getProperties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", 465);
    properties.put("mail.smtp.ssl.enable", true);
    properties.put("mail.smtp.auth", true);
    return properties;
  }

  // Get authenticated session
  private Session getSession() {
    return Session.getInstance(getMailProperties(), new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(from, password);
      }
    });
  }

  // Send HTML email (existing method)
  public boolean sendHtmlEmail(String message, String subject, String to) {
    boolean f = false;
    Session session = getSession();

    session.setDebug(true);
    MimeMessage m = new MimeMessage(session);

    try {
      m.setFrom(from);
      m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      m.setSubject(subject, "UTF-8");
      m.setContent(message, "text/html ; charset=UTF-8");

      Transport.send(m);
      f = true;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return f;
  }

  // New method to send email with attachment
  public boolean sendWithAttachment(String to, String subject, String message, MultipartFile attachmentFile) {
    boolean success = false;
    Session session = getSession();
    session.setDebug(true);

    try {
      // Create message
      MimeMessage mimeMessage = new MimeMessage(session);
      mimeMessage.setFrom(new InternetAddress(from));
      mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      mimeMessage.setSubject(subject);

      // Create multipart message
      MimeMultipart multipart = new MimeMultipart();

      // Text body part
      MimeBodyPart textBodyPart = new MimeBodyPart();
      textBodyPart.setText(message);
      multipart.addBodyPart(textBodyPart);

      // Attachment part
      MimeBodyPart attachmentBodyPart = new MimeBodyPart();
      File attachmentFile2 = convertMultipartFileToFile(attachmentFile);
      attachmentBodyPart.attachFile(attachmentFile2);
      attachmentBodyPart.setFileName(attachmentFile.getOriginalFilename());
      multipart.addBodyPart(attachmentBodyPart);

      // Set content
      mimeMessage.setContent(multipart);

      // Send message
      Transport.send(mimeMessage);
      success = true;

      // Delete temp file
      if (attachmentFile2 != null && attachmentFile2.exists()) {
        attachmentFile2.delete();
      }

    } catch (MessagingException | IOException e) {
      e.printStackTrace();
    }

    return success;
  }

  // Helper method to convert MultipartFile to File
  private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
    File file = File.createTempFile("attachment", "_" + multipartFile.getOriginalFilename());
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(multipartFile.getBytes());
    }
    return file;
  }
}