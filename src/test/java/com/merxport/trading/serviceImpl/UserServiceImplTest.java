package com.merxport.trading.serviceImpl;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.AbstractIntegrationTest;
import com.merxport.trading.entities.Address;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.UserType;
import com.merxport.trading.services.UserService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

// @TestPropertySource(locations = "classpath:application.properties")
class UserServiceImplTest extends AbstractIntegrationTest
{
    @Autowired
    private UserService userService;
    
    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration
    {
        @Bean
        public UserService userService()
        {
            return new UserServiceImpl()
            {
                public User save()
                {
                    return null; // return dummy data
                }
            };
        }
    }
    
    @Test
    void save() throws IOException, UnirestException
    {
        System.out.println("Current working directory: " + Paths.get(".").toAbsolutePath().normalize().toString());
        File file = new File("./src/main/resources/static/images/team4.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile( file.getName(), file.getName(), Files.probeContentType(file.toPath()), IOUtils.toByteArray(input));
        
        String imageID = userService.upload(multipartFile);
        
        Address address = new Address("street", "city", "state", "country");
        
        User user = new User("firstName", "lastName", "middleName", "test@email.com", "password", "08974938292", Collections.singletonList(address), false, Scopes.DOMESTIC, imageID, null,List.of(UserRole.BUYER, UserRole.BUYER), null, null, null, null, null, null, false, null, UserType.BUSINESS);
        User saved = userService.save(user);
        assertEquals(saved.getFirstName(), user.getFirstName());
        assertEquals(saved.getImageID(), user.getImageID());
    }
    
    @Test
    void deleteUser() throws Exception
    {
        String id = "6122e74dcdab19483bdce58a";
        User user = userService.deleteUser(userService.findByID(id));
        
        System.out.println("Test Method: " + user.isActive());
        System.out.println("Arch Date: " + user.getAudit().getArchivedDate());
        System.out.println("Arch By: " + user.getAudit().getArchivedBy());
        System.out.println("Mod Date: " + user.getAudit().getModifiedDate());
        System.out.println("Mod By: " + user.getAudit().getModifiedBy());
        assertFalse(user.isActive());
        assertNotNull(user.getAudit().getArchivedDate());
        assertNotNull(user.getAudit().getArchivedBy());
    }
    
    @Test
    void findUser() throws Exception
    {
        String id = "6126806273aade16270429c4";
        User user = userService.findByID(id);
        
        assertNotNull(user);
        
        String fileID = user.getImageID();
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
    
    @Test
    void getImage() throws Exception
    {
        String imageID = "6126a4817f80646d7836a04f";
        String image = userService.getImage(imageID, 300, 300, "JPEG");
        System.out.println(image);
    }
}
