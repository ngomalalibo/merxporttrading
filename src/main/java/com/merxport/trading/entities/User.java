package com.merxport.trading.entities;

import com.merxport.trading.enumerations.UserScopes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User extends PersistingBaseEntity
{
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String password;
    private String phoneNo;
    private List<Address> address = new ArrayList<>();
    private boolean isVerified;
    private UserScopes scope;
    private String fileID;
    private boolean isActive = true;
}
