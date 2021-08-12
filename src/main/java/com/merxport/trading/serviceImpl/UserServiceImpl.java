package com.merxport.trading.serviceImpl;

import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserScopes;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.UserRepository;
import com.merxport.trading.services.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @Autowired
    private GridFsOperations operations;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public User save(User user, MultipartFile file) throws IOException
    {
        /*if (!Objects.isNull(file))
        {
            user.setImageBytes(file.getBytes());
        }*/
        
        String fileID = storeImageGridFS(file);
        if (!Objects.isNull(fileID))
        {
            user.setFileID(fileID);
        }
        // save user and return
        return userRepository.save(user);
    }
    
    @Transactional
    public String storeImageGridFS(MultipartFile file) throws IOException
    {
        if (!Objects.isNull(file))
        {
            // Get image metadata
            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", file.getSize());
            metadata.put("fileName", file.getOriginalFilename());
            
            // store file in gridfs mongo
            
            // set fileID in user object for retrieval purpose
            return gridFsTemplate.store(file.getInputStream(), metadata.get("fileName").toString(), file.getContentType(), metadata).toString();
        }
        // save use and return
        return null;
    }
    
    @Override
    public List<User> getActiveUsers() throws IOException
    {
        return userRepository.findUsers(true, Sort.by(Sort.Direction.ASC, "firstName")).orElse(null);
    }
    
    @Override
    public List<User> getArchivedUsers() throws IOException
    {
        return userRepository.findUsers(false, Sort.by(Sort.Direction.ASC, "firstName")).orElse(null);
    }
    
    @Override
    public User deleteUser(String id) throws IOException
    {
        User user = userRepository.findById(id).orElse(null);
        if (!Objects.isNull(user))
        {
            Query q = new Query(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("isActive", false);
            UpdateResult updateResult = mongoTemplate.updateFirst(q, update, User.class);
            if (updateResult.wasAcknowledged())
            {
                return user;
            }
            else
            {
                throw new CustomNullPointerException("User not deleted");
            }
            // userRepository.deleteById(id);
            // gridFsTemplate.delete(new Query(Criteria.where("_id").is(user.getFileID())));
        }
        else
        {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
    }
    
    @Override
    public User findUser(String id) throws IOException
    {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public void sendMessage()
    {
    
    }
    
    @Override
    public boolean verifyAccount(String id)
    {
        return false;
    }
    
    @Override
    public void setScope(UserScopes scope)
    {
    
    }
}
