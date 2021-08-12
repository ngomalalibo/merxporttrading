package com.merxport.trading.services;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserScopes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService
{
    User save(User user, MultipartFile file) throws IOException;
    
    User findUser(String id) throws IOException;
    
    List<User> getActiveUsers() throws IOException;
    
    List<User> getArchivedUsers() throws IOException;
    
    User deleteUser(String id) throws IOException;
    
    void sendMessage();
    
    User verifyAccount(String id);
    
    // List<Commodity> searchCommodity();
    // List<RFQ> searchRFQ();
    void setScope(UserScopes scope);
}
