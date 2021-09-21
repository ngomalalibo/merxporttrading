package com.merxport.trading.serviceImpl;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.merxport.trading.aspect.Loggable;
import com.merxport.trading.config.GenerateVerificationCode;
import com.merxport.trading.email.SendMailMailGun;
import com.merxport.trading.entities.User;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.exception.CustomNullPointerException;
import com.merxport.trading.exception.DuplicateEntityException;
import com.merxport.trading.exception.EntityNotFoundException;
import com.merxport.trading.repositories.UserRepository;
import com.merxport.trading.security.JwtTokenProvider;
import com.merxport.trading.security.PasswordEncoder;
import com.merxport.trading.security.VerificationPOJO;
import com.merxport.trading.services.UserService;
import com.merxport.trading.util.ImageUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.io.IOUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository userRepository;
    
    @Qualifier("sendMailMailGun")
    @Autowired
    private SendMailMailGun sendMail;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @Autowired
    private GridFsOperations operations;
    
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
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DeleteServiceImpl deleteService;
    
    @Autowired
    private UpdateEntityServiceImpl updateEntityService;
    
    @Autowired
    private UserService userService;
    
    protected final Faker faker = new Faker(Locale.getDefault());
    
    @Autowired
    private ImageUtil imageUtil;
    
    @Loggable
    @Override
    public User save(User user) throws UnirestException
    {
        if (!Strings.isNullOrEmpty(user.getPassword()))
        {
            String encodedPassword;
            if (user.getId() == null)
            {
                encodedPassword = passwordEncoder.getPasswordEncoder().encode(user.getPassword());
            }
            else
            {
                encodedPassword = user.getPassword();
            }
            user.setPassword(encodedPassword);
        }
        
        User u = userRepository.findByEmail(user.getEmail());
        if (!Objects.isNull(u) && user.getId() == null)
        {
            throw new DuplicateEntityException("Duplicate user. User exists!");
        }
        
        if (user.getId() == null)
        {
            String token = jwtTokenProvider.createToken(user);
            user.setToken(token);
            user.setVerificationPOJO(generateVerificationCode.verificationCode());
            String line1 = "Verify your account using this verification code "
                    + user.getVerificationPOJO().getVerificationCode() + ". It expires in 24 hrs at "
                    + user.getVerificationPOJO().getCreationDateTime().plusDays(1).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss z")) + ".";
            String subject = "Merxport - Account Verification";
            sendMail.sendMail(user, subject, line1);
        }
        return userRepository.save(user);
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
    public String resetPassword(String username) throws UnirestException
    {
        User user = userRepository.findByEmail(username);
        if (user == null)
        {
            throw new EntityNotFoundException("User does not exist");
        }
        String password = faker.internet().password(6, 8, true);
        updateEntityService.updateEntity(user, mongoTemplate, userRepository, "password", passwordEncoder.getPasswordEncoder().encode(password));
        String subject = "Merxport - Reset Password";
        // send email
        String line1 = "Your new password is <strong>" + password + "</strong><br> Please endeavour to change it after login.";
        sendMail.sendMail(user, subject, line1);
        
        return password;
    }
    
    @Override
    public String changePassword(String username, String oldPassword, String newPassword) throws UnirestException
    {
        User user = userService.findByEmail(username);
        if (user == null)
        {
            throw new EntityNotFoundException("User does not exist");
        }
        if (passwordEncoder.getPasswordEncoder().matches(oldPassword, user.getPassword()))
        {
            user.setPassword(passwordEncoder.getPasswordEncoder().encode(newPassword));
            updateEntityService.updateEntity(user, mongoTemplate, userRepository, "password", passwordEncoder.getPasswordEncoder().encode(newPassword));
            return newPassword;
        }
        return null;
    }
    
    @Override
    public String upload(MultipartFile file) throws IOException
    {
        if (!Objects.isNull(file))
        {
            // Get image metadata
            BufferedImage image = ImageIO.read(file.getInputStream());
            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", file.getSize());
            metadata.put("fileName", file.getOriginalFilename());
            metadata.put("fileWidth", image.getWidth());
            metadata.put("fileHeight", image.getHeight());
            
            
            // store file in gridfs mongo
            // return fileID in user object for retrieval purpose
            return gridFsTemplate.store(file.getInputStream(), metadata.get("fileName").toString(), file.getContentType(), metadata).toString();
        }
        // save use and return
        return null;
    }
    
    
    @Override
    public String getImage(String imageID, int width, int height, String format) throws Exception
    {
        GridFSFile image = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(imageID)));
        
        if (image == null)
        {
            String dummyImageID = "6149ce8ad31ade7e30ed93d7";
            image = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(dummyImageID)));
            format = "PNG";
        }
        assert image != null;
        format = format == null ? "JPEG" : format;
        if (width == 0 || height == 0)
        {
            assert image.getMetadata() != null;
            width = image.getMetadata().get("fileWidth") != null ? (int) image.getMetadata().get("fileWidth") : 200;
            height = image.getMetadata().get("fileHeight") != null ? (int) image.getMetadata().get("fileWidth") : 200;
        }
        String regex = "(((?i)(jpe?g|png|gif|bmp|wbmp))$)";
        Pattern p = Pattern.compile(regex);
        
        Matcher m = p.matcher(format);
        if (!m.matches())
        {
            throw new IllegalArgumentException("Provide a valid image format");
        }
        
        byte[] imageBytes = IOUtils.toByteArray(operations.getResource(image).getInputStream());
        
        InputStream is = new ByteArrayInputStream(imageBytes);
        BufferedImage newBi = ImageIO.read(is);
        newBi = imageUtil.resizeImage(newBi, width, height);
        
        //BufferedImage newBi = imageUtil.createRGBImage(imageBytes, width, height);
        
        Binary binary = new Binary(BsonBinarySubType.BINARY, imageUtil.toByteArray(newBi, format));
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(binary.getData());
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
    public User findByID(String id) throws IOException
    {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        try
        {
            user.setImage(userService.getImage(user.getImageID(), 0, 0, null));
            return user;
        }
        catch (Exception e)
        {
            throw new EntityNotFoundException("User not found");
        }
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
            throw new AccessDeniedException("User does not exist");
        }
        if (!passwordEncoder.getPasswordEncoder().matches(password, login.getPassword()))
        {
            throw new AccessDeniedException("Incorrect password");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(login.getToken(), request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Gson gson = new Gson();
        User user = userService.findByEmail(username);
        request.getSession().setAttribute("user", user);
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
    public void resendCode(String id) throws UnirestException
    {
        if (Strings.isNullOrEmpty(id))
        {
            throw new EntityNotFoundException("User not found");
        }
        VerificationPOJO verificationPOJO = generateVerificationCode.verificationCode();
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setVerificationPOJO(verificationPOJO);
        User saved = userRepository.save(user);
        String line1 = "Verify your account using this verification code "
                + user.getVerificationPOJO().getVerificationCode() + ". It expires in 24 hrs at "
                + user.getVerificationPOJO().getCreationDateTime().plusDays(1).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss z")) + ".";
        String subject = "Merxport - Account Verification";
        System.out.println(sendMail.sendMail(saved, subject, line1));
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
