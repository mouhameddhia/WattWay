package tn.esprit.services;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class EmailService {
    public static void sendEmail(String recipient, String subject, String messageText) throws Exception {
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("wattwayorg@gmail.com", "tgkt sbsk tssu jigx"));
        email.setStartTLSEnabled(true);
        email.setFrom("wattwayorg@gmail.com");
        email.setSubject(subject);
        email.setMsg(messageText);
        email.addTo(recipient);
        email.send();
        System.out.println("Email sent successfully to " + recipient);
    }
}
