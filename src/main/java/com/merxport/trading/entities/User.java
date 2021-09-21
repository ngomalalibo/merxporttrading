package com.merxport.trading.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merxport.trading.enumerations.Scopes;
import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.UserType;
import com.merxport.trading.security.VerificationPOJO;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@Component
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends PersistingBaseEntity
{
    private String firstName;
    private String lastName;
    private String middleName;
    @Email(message = "Kindly provide a valid email")
    private String email;
    private String password;
    private String phoneNo;
    private List<Address> address = new ArrayList<>();
    private boolean isVerified;
    private Scopes scope;
    private String imageID;
    @Transient
    @BsonIgnore
    private String image;
    private List<UserRole> userRoles = new ArrayList<>();
    private String token;
    private VerificationPOJO verificationPOJO;
    private String refreshToken;
    private String companyName;
    private String companyCountry;
    private String businessRegNo;
    private boolean hasExportLicense;
    private String exportLicenseID;
    private UserType userType;
}
