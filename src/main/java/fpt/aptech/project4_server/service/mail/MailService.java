package fpt.aptech.project4_server.service.mail;

public interface MailService {
    boolean sendMail(String to, String subject, String body);

    boolean sendResetPassword(String to, String code);
}
