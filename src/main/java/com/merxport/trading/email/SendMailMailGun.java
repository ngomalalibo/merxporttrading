package com.merxport.trading.email;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.security.VerificationPOJO;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class SendMailMailGun
{
    private String MAILGUN_KEY = System.getenv().get("MAILGUN_KEY");
    private String API_BASEURL = System.getenv().get("MAILGUN_API_BASEURL");
    private String DOMAIN = System.getenv().get("MAILGUN_DOMAIN");
    
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a");
    
    @Value("${google.mail.username}")
    private String username = "weblibrarianapp@gmail.com";
    @Value("${google.mail.password}")
    private String password = System.getenv().get("WEBLIBRARIAN_GPASSWORD");
    
    private static String template;
    
    public SendMailMailGun()
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
    
    public ActionableEmail getMailInstance(User user, String subject, String line1)
    {
        ActionableEmail mailObject = new ActionableEmail();
        mailObject.setSubject(subject);
        mailObject.setToAddresses(user.getEmail());
        mailObject.setPersonName(user.getFirstName());
        mailObject.setFromAddresses(username != null ? username : "weblibrarianapp@gmail.com");
        mailObject.setLine1(line1);
        mailObject.setMessage(populateTemplate(mailObject));
        
        return mailObject;
    }
    
    public String sendMail(User user, String subject, String line1) throws UnirestException
    {
        
        ActionableEmail mailInstance = getMailInstance(user, subject, line1);
        String mailHTML = populateTemplate(mailInstance);
        
        String response = sendSimpleMessage(mailInstance, mailHTML);
        System.out.println("Response: " + response);
        return response;
    }
    
    public String sendSimpleMessage(ActionableEmail actionableEmail, String template) throws UnirestException
    {
        try
        {
            InternetAddress internetAddress = new InternetAddress(username != null ? username : "weblibrarianapp@gmail.com");
            internetAddress.setPersonal("Merxport Trading");
            
            HttpResponse<String> response = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                                                   .basicAuth("api", MAILGUN_KEY)
                                                   .field("from", internetAddress)
                                                   .field("to", actionableEmail.getToAddresses())
                                                   .field("subject", actionableEmail.getSubject())
                                                   .field("html", template)
                                                   .asString();
            return response.getBody();
        }
        catch (MessagingException | UnsupportedEncodingException mex)
        {
            mex.printStackTrace();
            return "Message not sent";
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
    
    public static void main(String[] args)
    {
        try
        {
            SendMailMailGun sendMail = new SendMailMailGun();
            User user = User.builder().firstName("Ngo").lastName("Alalibo").email("ngomalalibo@yahoo.com").password("password")
                            .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).userRoles(List.of(UserRole.BUYER, UserRole.SELLER)).build();
            String line1 = "Verify your account using this verification code "
                    + user.getVerificationPOJO().getVerificationCode() + ". It expires in 24 hrs at "
                    + user.getVerificationPOJO().getCreationDateTime().plusDays(1).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss z")) + ".";
            String subject = "Merxport - Account Verification";
            sendMail.sendMail(user, subject, line1);
        }
        catch (Exception e)
        {
            System.out.println("Mail not sent!" + e.getMessage());
            e.printStackTrace();
            
        }
    }
    
    public static String stringSafe(String raw)
    {
        return raw != null ? raw : "";
    }
}
