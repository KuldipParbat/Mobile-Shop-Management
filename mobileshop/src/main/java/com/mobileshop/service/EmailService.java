package com.mobileshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail,
                                       String name,
                                       String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset — Mobile Shop");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            String html = """
                <div style="font-family:'DM Sans',Arial,sans-serif;
                            max-width:520px;margin:0 auto;padding:32px;">
                  <div style="background:#2563eb;border-radius:12px 12px 0 0;
                              padding:24px;text-align:center;">
                    <h1 style="color:#fff;margin:0;font-size:22px;">
                      📱 Mobile Shop
                    </h1>
                  </div>
                  <div style="background:#fff;border:1px solid #e5e7eb;
                              border-top:none;border-radius:0 0 12px 12px;
                              padding:32px;">
                    <h2 style="color:#111827;margin:0 0 8px;">
                      Password Reset Request
                    </h2>
                    <p style="color:#6b7280;margin:0 0 24px;">
                      Hi <strong>%s</strong>, we received a request to reset
                      your password. Click the button below to proceed.
                    </p>
                    <a href="%s"
                       style="display:inline-block;background:#2563eb;
                              color:#fff;padding:12px 28px;border-radius:8px;
                              text-decoration:none;font-weight:600;
                              font-size:15px;">
                      Reset Password
                    </a>
                    <p style="color:#9ca3af;font-size:13px;margin:24px 0 0;">
                      This link expires in <strong>30 minutes</strong>.
                      If you did not request this, ignore this email.
                    </p>
                    <hr style="border:none;border-top:1px solid #f3f4f6;
                               margin:24px 0;">
                    <p style="color:#9ca3af;font-size:12px;margin:0;">
                      Or copy this link:<br>
                      <span style="color:#2563eb;word-break:break-all;">
                        %s
                      </span>
                    </p>
                  </div>
                </div>
                """.formatted(name, resetUrl, resetUrl);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail,
                                 String name,
                                 String shopName,
                                 String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Mobile Shop — Your Account is Ready");

            String loginUrl = frontendUrl + "/login";

            String html = """
                <div style="font-family:'DM Sans',Arial,sans-serif;
                            max-width:520px;margin:0 auto;padding:32px;">
                  <div style="background:#2563eb;border-radius:12px 12px 0 0;
                              padding:24px;text-align:center;">
                    <h1 style="color:#fff;margin:0;font-size:22px;">
                      📱 Mobile Shop
                    </h1>
                  </div>
                  <div style="background:#fff;border:1px solid #e5e7eb;
                              border-top:none;border-radius:0 0 12px 12px;
                              padding:32px;">
                    <h2 style="color:#111827;margin:0 0 8px;">
                      Welcome, %s! 🎉
                    </h2>
                    <p style="color:#6b7280;margin:0 0 20px;">
                      Your account for <strong>%s</strong> has been created.
                    </p>
                    <div style="background:#f9fafb;border-radius:8px;
                                padding:16px;margin-bottom:24px;">
                      <p style="margin:0 0 8px;color:#374151;font-weight:600;">
                        Login Details:
                      </p>
                      <p style="margin:0 0 4px;color:#6b7280;">
                        Email: <strong>%s</strong>
                      </p>
                      <p style="margin:0;color:#6b7280;">
                        Password: <strong>%s</strong>
                      </p>
                    </div>
                    <a href="%s"
                       style="display:inline-block;background:#2563eb;
                              color:#fff;padding:12px 28px;border-radius:8px;
                              text-decoration:none;font-weight:600;
                              font-size:15px;">
                      Login Now
                    </a>
                    <p style="color:#ef4444;font-size:13px;margin:20px 0 0;">
                      ⚠️ Please change your password after first login.
                    </p>
                  </div>
                </div>
                """.formatted(name, shopName, toEmail, tempPassword, loginUrl);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}