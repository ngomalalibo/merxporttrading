package com.merxport.trading.serviceImpl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.config.GenerateVerificationCode;
import com.merxport.trading.email.SendMail_Working;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.DuplicateEntityException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.UserRepository;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.security.PasswordEncoder;
import com.merxport.trading.security.VerificationPOJO;
import com.merxport.trading.services.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;
    
    @Qualifier("sendMail")
    @Autowired
    private SendMail_Working sendMail;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Qualifier("authenticationManagerBean")
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private GenerateVerificationCode generateVerificationCode;
    
    @Autowired
    private PasswordEncoder getPasswordEncoder;
    
    @Autowired
    private DeleteServiceImpl deleteService;
    
    
    @Loggable
    @Override
    public User save(User user)
    {
        if (!Strings.isNullOrEmpty(user.getPassword()))
        {
            String encodedPassword = getPasswordEncoder.getPasswordEncoder().encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        
        String token = jwtTokenProvider.createToken(user);
        user.setToken(token);
        user.setVerificationPOJO(generateVerificationCode.verificationCode());
        User u = userRepository.findByEmail(user.getEmail());
        if (!Objects.isNull(u) && user.getId() == null)
        {
            throw new DuplicateEntityException("Duplicate user. User exists!");
        }
        User saved = userRepository.save(user);
        System.out.println(sendMail.sendMail(saved));
        return saved;
    }
    
    /**
     * save entity plus pile
     *
     * @Loggable
     * @Override public User save(User user, MultipartFile file) throws IOException
     * {
     * // store file
     * String fileID = upload(file);
     * if (!Strings.isNullOrEmpty(user.getPassword()))
     * {
     * String encodedPassword = new BCryptPasswordEncoder(11).encode(user.getPassword());
     * user.setPassword(encodedPassword);
     * }
     * <p>
     * <p>
     * String token = jwtTokenProvider.createToken(user);
     * user.setToken(token);
     * user.setVerificationPOJO(generateVerificationCode.verificationCode());
     * // user.setVerificationPOJO("https://merxporttrading.herokuapp.com/api/user/verify/" + user.getId() + "?token=" + user.getToken());
     * if (!Objects.isNull(fileID))
     * {
     * user.setImageID(fileID);
     * }
     * // save user and return
     * return userRepository.save(user);
     * }
     */
    
    @Override
    public User addRoleToUser(String email, UserRole role)
    {
        User byEmail = userRepository.findByEmail(email);
        if (!Objects.isNull(byEmail))
        {
            byEmail.getUserRoles().add(role);
        }
        return userRepository.save(byEmail);
    }
    
    
    @Override
    public String upload(MultipartFile file) throws IOException
    {
        if (!Objects.isNull(file))
        {
            // Get image metadata
            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", file.getSize());
            metadata.put("fileName", file.getOriginalFilename());
            
            // store file in gridfs mongo
            // return fileID in user object for retrieval purpose
            return gridFsTemplate.store(file.getInputStream(), metadata.get("fileName").toString(), file.getContentType(), metadata).toString();
        }
        // save use and return
        return null;
    }
    
    /*@Transactional
    public String storeImageGridFS(MultipartFile file) throws IOException
    {
        if (!Objects.isNull(file))
        {
            // Get image metadata
            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", file.getSize());
            metadata.put("fileName", file.getOriginalFilename());
            
            // store file in gridfs mongo
            // return fileID in user object for retrieval purpose
            return gridFsTemplate.store(file.getInputStream(), metadata.get("fileName").toString(), file.getContentType(), metadata).toString();
        }
        // save use and return
        return null;
    }*/
    
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
    
    @Loggable
    @Override
    public User deleteUser(User user)
    {
        return deleteService.deleteEntity(user, mongoTemplate, userRepository);
    }
    
    @Override
    public User findUser(String id) throws IOException
    {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public User findByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User authenticateUser(String username, String password, HttpServletRequest request) throws Exception
    {
        User login = userRepository.findByEmail(username);
        if (login == null)
        {
            throw new AccessDeniedException("Login was not successful");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(login.getToken(), request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Gson gson = new Gson();
        // System.out.println("Auth User: " + gson.toJson(login));
        return login;
    }
    
    /*@Override
    public User login(String username, String password, String token, HttpSession session)
    {
        final Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, username));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByEmail(username);
        
        if (user != null)
        {
            session.setAttribute("user", user);
            session.setAttribute("roles", user.getUserRoles());
            session.setAttribute("username", user.getEmail());
            return user;
        }
        
        return null;
    }*/
    
    @Override
    public void sendMessage()
    {
    
    }
    
    @Override
    public User verifyAccount(String id, String code)
    {
        if (Strings.isNullOrEmpty(code) || Strings.isNullOrEmpty(id))
        {
            throw new RequestRejectedException("Kindly provide a valid code or user ID");
        }
        
        User user = userRepository.findById(id).orElse(null);
        if (!Objects.isNull(user))
        {
            // validate token and retrieve token username and compare with id username
            LocalDateTime now = LocalDateTime.now();
            if (user.getVerificationPOJO().getVerificationCode().equals(code) && user.getVerificationPOJO().getCreationDateTime().plusDays(3).isAfter(now))
            {
                Query q = new Query(Criteria.where("_id").is(id));
                Update update = new Update();
                update.set("isVerified", true);
                update.set("audit.modifiedDate", now);
                update.set("audit.modifiedBy", user.getEmail());
                UpdateResult updateResult = mongoTemplate.updateFirst(q, update, User.class);
                if (updateResult.wasAcknowledged())
                {
                    user.setVerified(true);
                    user.getAudit().setModifiedDate(now);
                    user.getAudit().setModifiedBy(user.getEmail());
                    return user;
                }
                else
                {
                    throw new CustomNullPointerException("User not verified");
                }
            }
            else
            {
                throw new RequestRejectedException("Invalid code");
            }
            
        }
        else
        {
            throw new CustomNullPointerException("User not found");
        }
    }
    
    @Override
    public void resendCode(String id)
    {
        if (Strings.isNullOrEmpty(id))
        {
            throw new EntityNotFoundException("User not found");
        }
        VerificationPOJO verificationPOJO = generateVerificationCode.verificationCode();
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setVerificationPOJO(verificationPOJO);
        User saved = userRepository.save(user);
        System.out.println(sendMail.sendMail(saved));
    }
    
    /*@Loggable
    @Override
    public User verifyAccount(String id, String token)
    {
        if (Strings.isNullOrEmpty(id))
        {
            throw new RequestRejectedException("Kindly provide a valid user id");
        }
        if (Strings.isNullOrEmpty(token))
        {
            throw new RequestRejectedException("Kindly provide a valid token");
        }
        
        User user = userRepository.findById(id).orElse(null);
        if (!Objects.isNull(user))
        {
            // validate token and retrieve token username and compare with id username
            if (jwtTokenProvider.validateToken(token))
            {
                String usernameFromToken = jwtTokenProvider.getUsernameFromToken(token);
                if (usernameFromToken.equalsIgnoreCase(user.getEmail()))
                {
                    Query q = new Query(Criteria.where("_id").is(id));
                    Update update = new Update();
                    update.set("isVerified", true);
                    UpdateResult updateResult = mongoTemplate.updateFirst(q, update, User.class);
                    if (updateResult.wasAcknowledged())
                    {
                        return user;
                    }
                    else
                    {
                        throw new CustomNullPointerException("User not verified");
                    }
                }
                else
                {
                    throw new RequestRejectedException("Wrong token");
                }
            }
            else
            {
                throw new RequestRejectedException("Invalid token");
            }
            
        }
        else
        {
            throw new CustomNullPointerException("User not found");
        }
    }*/
    
    @Override
    public void setScope(Scopes scope)
    {
    
    }
}
