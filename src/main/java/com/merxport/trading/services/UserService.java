package com.merxport.trading.services;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.enumerations.UserRole;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface UserService
{
    User save(User user) throws UnirestException;
    
    User findUser(String id) throws IOException;
    
    void resendCode(String id) throws UnirestException;
    
    User findByEmail(String email);
    
    List<User> getActiveUsers() throws IOException;
    
    List<User> getArchivedUsers() throws IOException;
    
    User deleteUser(User user) throws IOException;
    
    User addRoleToUser(String email, UserRole role);
    
    String resetPassword(String username) throws UnirestException;
    
    String changePassword(String id, String oldPassword, String newPassword) throws UnirestException;
    
    void sendMessage();
    
    User verifyAccount(String id, String code);
    
    // List<Commodity> searchCommodity();
    // List<RFQ> searchRFQ();
    void setScope(Scopes scope);
    
    User authenticateUser(String username, String password, HttpServletRequest request) throws Exception;
    
    String upload(MultipartFile file) throws IOException;
    
    String getImage(String imageID, int width, int height, String format) throws Exception;
}
