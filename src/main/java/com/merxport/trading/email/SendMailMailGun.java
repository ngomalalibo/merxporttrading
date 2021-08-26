package com.merxport.trading.email;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.security.VerificationPOJO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
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
    
    public String sendSimpleMessage(ActionableEmail actionableEmail, String template) throws UnirestException
    {
        HttpResponse<String> response = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                                               .basicAuth("api", MAILGUN_KEY)
                                               .field("from", actionableEmail.getFromAddresses())
                                               .field("to", actionableEmail.getToAddresses())
                                               .field("subject", actionableEmail.getSubject())
                                               .field("html", template)
                                               .asString();
        return response.getBody();
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
            User user = User.builder().firstName("Ngo").lastName("Alalibo").email("ngomalalibo@gmail.com").password("password")
                            .isVerified(true).verificationPOJO(new VerificationPOJO("123456", LocalDateTime.now())).userRoles(List.of(UserRole.BUYER, UserRole.SELLER)).build();
            ActionableEmail mailInstance = sendMail.getMailInstance(user);
            String temp = sendMail.populateTemplate(mailInstance);
            
            String response = sendMail.sendSimpleMessage(mailInstance, temp);
            System.out.println("Response " + response);
        }
        catch (Exception e)
        {
            System.out.println("UnirestException " + e.getMessage());
            e.printStackTrace();
            
        }
    }
    
    public static String stringSafe(String raw)
    {
        return raw != null ? raw : "";
    }
}
