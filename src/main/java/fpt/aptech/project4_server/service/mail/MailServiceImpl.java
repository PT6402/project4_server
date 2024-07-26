package fpt.aptech.project4_server.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String formEmail;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(formEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
    
   public void sendPlainTextEmail(String to, String subject, String textBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(textBody);
        javaMailSender.send(message);
    }

    @Override
    public boolean sendResetPassword(String to, String code) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(formEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("this is reset password");
            mimeMessageHelper.setText("this is code reset password: " + code);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
