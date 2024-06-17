package fpt.aptech.project4_server.service;

public interface MailService {
    boolean sendMail(String to, String subject, String body);
}
