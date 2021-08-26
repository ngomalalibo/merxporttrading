package com.merxport.trading.services;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.UserScopes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface UserService
{
    User save(User user);
    
    User findUser(String id) throws IOException;
    
    void resendCode(String id);
    
    User findByEmail(String email);
    
    List<User> getActiveUsers() throws IOException;
    
    List<User> getArchivedUsers() throws IOException;
    
    User deleteUser(User user) throws IOException;
    
    User addRoleToUser(String email, UserRole role);
    
    void sendMessage();
    
    User verifyAccount(String id, String code);
    
    // List<Commodity> searchCommodity();
    // List<RFQ> searchRFQ();
    void setScope(UserScopes scope);
    
    User authenticateUser(String username, String password, HttpServletRequest request) throws Exception;
    
    String upload(MultipartFile file) throws IOException;
}
