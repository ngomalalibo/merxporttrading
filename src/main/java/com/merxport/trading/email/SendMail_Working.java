package com.merxport.trading.email;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.security.VerificationPOJO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SendMail_Working
{
    private static String template;
    
    @Value("${google.mail.username}")
    private String username;
    @Value("${google.mail.password}")
    private String password = System.getenv().get("WEBLIBRARIAN_GPASSWORD");
    
    public String host = "smtp.gmail.com";
    public String port = "465";
    
    
    public SendMail_Working()
    {
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("templates/accessemail.html")).getFile());
        
        try
        {
            template = new String(Files.readAllBytes(file.toPath()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public ActionableEmail getMailInstance(User user)
    {
        ActionableEmail mailObject = new ActionableEmail();
        mailObject.setSubject("Merxport - Account Verification");
        mailObject.setToAddresses(user.getEmail());
        mailObject.setPersonName(user.getFirstName());
        mailObject.setFromAddresses(username != null ? username : "weblibrarianapp@gmail.com");
        mailObject.setLine1(
                "Verify your account using this verification code "
                        + user.getVerificationPOJO().getVerificationCode() + ". It expires in 24 hrs at "
                        + user.getVerificationPOJO().getCreationDateTime().plusDays(1).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss z")) + ".");
        mailObject.setMessage(populateTemplate(mailObject));
        
        return mailObject;
    }
    
    
    public boolean sendMailSSL(ActionableEmail actionableEmail)
    {
        // System.out.println("Gmail Password: " + password);
        // System.out.println("Gmail Username: " + username);
        // System.out.println("Gmail To: " + actionableEmail.getToAddresses());
        // System.out.println("Token: " + template.substring(1, 50));
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        
        Session session = Session.getInstance(properties, new Authenticator()
        {
            
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username != null ? username : "weblibrarianapp@gmail.com", password != null ? password : System.getenv().get("WEBLIBRARIAN_GPASSWORD"));
            }
            
        });
        
        // Used to debug SMTP issues
        session.setDebug(false);
        
        try
        {
            MimeMessage message = new MimeMessage(session);
            InternetAddress internetAddress = new InternetAddress(username != null ? username : "weblibrarianapp@gmail.com");
            internetAddress.setPersonal("Merxport Trading");
            message.setFrom(internetAddress);
            List<String> toAddresses = Arrays.asList(username != null ? username : "weblibrarianapp@gmail.com", actionableEmail.getToAddresses());
            System.out.print("To addresses: ");
            toAddresses.forEach(System.out::println);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(", ", toAddresses)));
            message.setSubject(actionableEmail.getSubject());
            message.setSentDate(new Date());
            message.setContent(actionableEmail.getMessage(), "text/html");
            Transport.send(message);
            return true;
        }
        catch (MessagingException | UnsupportedEncodingException mex)
        {
            mex.printStackTrace();
            return false;
        }
    }
    
    
    public String populateTemplate(ActionableEmail actionableEmail)
    {
        
        return template
                .replaceAll("##LINE1##", stringSafe(actionableEmail.getLine1()))//
                .replaceAll("##companyname##", stringSafe("Merxport Trading"))//
                .replaceAll("##messagetitle##", stringSafe(actionableEmail.getSubject()))//
                .replaceAll("##personname##", stringSafe(actionableEmail.getPersonName()))//
                .replaceAll("##emailaddress##", stringSafe(actionableEmail.getToAddresses()));
    }
    
    public String sendMail(User user)
    {
        ActionableEmail mailInstance = getMailInstance(user);
        return sendMailSSL(mailInstance) ? "Message sent successfully" : "Message not sent";
    }
    
    public static void main(String[] args)
    {
        SendMail_Working sendMail = new SendMail_Working();
        User user = User.builder().firstName("Ngo").lastName("Alalibo").email("ngomalalibo@gmail.com").password("password")
                        .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).userRoles(List.of(UserRole.BUYER, UserRole.SELLER)).build();
        System.out.println(sendMail.sendMail(user));
    }
    
    public static String stringSafe(String raw)
    {
        return raw != null ? raw : "";
    }
}
