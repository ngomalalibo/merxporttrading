package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.Address;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserScopes;
import com.merxport.trading.services.UserService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class UserServiceImplTest
{
    @Autowired
    UserService userService;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @Autowired
    private GridFsOperations operations;
    
    @Test
    void save() throws IOException
    {
        System.out.println("Current working directory: " + Paths.get(".").toAbsolutePath().normalize().toString());
        File file = new File("./src/main/resources/static/images/team4.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("image.jpg", file.getName(), Files.probeContentType(file.toPath()), IOUtils.toByteArray(input));
        
        Address address = new Address("street", "city", "state", "country");
        
        User user = new User("firstName", "lastName", "middleName", "test@emial.com", "password", "08974938292", Collections.singletonList(address), false, UserScopes.DOMESTIC, null, true);
        User saved = userService.save(user, multipartFile);
        assertEquals(saved.getFirstName(), user.getFirstName());
        assertEquals(saved.getFileID(), user.getFileID());
    }
    
    @Test
    void deleteUser() throws IOException
    {
        String id = "6115658f6cdc51682a71a084";
        User user = userService.deleteUser(id);
        
        assertFalse(user.isActive());
    }
    
    @Test
    void findUser() throws IOException
    {
        String id = "6115658f6cdc51682a71a084";
        User user = userService.findUser(id);
        
        assertNotNull(user);
        
        File ff = new File("C:/imageGet/team3.jpg");
        
        String fileID = user.getFileID();
        if (!Objects.isNull(fileID))
        {
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileID)));
            assertNotNull(file);
            assertEquals(file.getFilename(), "team4.jpg");
        }
    }
    
    @Test
    void sendMessage()
    {
    }
    
    @Test
    void verifyAccount()
    {
    }
    
    @Test
    void setScope()
    {
    }
}
