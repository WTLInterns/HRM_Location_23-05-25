package com.jaywant.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaywant.demo.Entity.Employee;

@Service
public class EmployeeEmailService {

  @Autowired
  private EmailService emailService;

  /**
   * Sends the employee their login credentials.
   * Uses EmailService.sendHtmlEmail to send an HTML‐formatted email.
   */
  public boolean sendEmployeeCredentials(Employee employee) {
    String to = employee.getEmail();
    String subject = "🎉  Your Employee Access";

    // Capitalize names
    String fullName = capitalize(employee.getFirstName()) + " "
        + capitalize(employee.getLastName());

    // Build a simple HTML message
    StringBuilder sb = new StringBuilder();
    sb.append("<p>Hello ").append(fullName).append(",</p>");

    sb.append("Your employee account has been created successfully. Below are your login details:</p>");
    sb.append("<ul>")
        .append("<li><strong>Email:</strong> ").append(employee.getEmail()).append("</li>")
        .append("<li><strong>Password:</strong> ").append(employee.getPassword()).append("</li>")
        .append("</ul>");
    sb.append("<p><a href=\"https://managifyhr.com\" target=\"_blank\">Click here to log in</a></p>");
    sb.append(
        "<p><strong>⚠️ Please change your password after your first login to keep your account secure.</strong></p>");
    sb.append("<p>If you have any issues, contact our support team.</p>");
    sb.append("<p>Thanks &amp; Regards,<br>Team WTL Tourism Pvt. Ltd.</p>");

    return emailService.sendHtmlEmail(sb.toString(), subject, to);
  }

  private String capitalize(String str) {
    if (str == null || str.isEmpty())
      return str;
    return Character.toUpperCase(str.charAt(0))
        + str.substring(1).toLowerCase();
  }
}
