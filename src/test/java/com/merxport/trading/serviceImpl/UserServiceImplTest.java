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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void deleteUser()
    {
    }
    
    @Test
    void findUser() throws IOException
    {
        String id = "61140f43700416074b828450";
        User user = userService.findUser(id);
        
        File ff = new File("C:/imageGet/team3.jpg");
        
        if (!Objects.isNull(user))
        {
            String fileID = user.getFileID();
            if (!Objects.isNull(fileID))
            {
                GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileID)));
                if (!Objects.isNull(file))
                {
                    byte[] fileBytes = IOUtils.toByteArray(operations.getResource(file).getInputStream());
                    OutputStream out = new FileOutputStream(ff);
                    out.write(fileBytes);
                    out.close();
                }
            }
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
