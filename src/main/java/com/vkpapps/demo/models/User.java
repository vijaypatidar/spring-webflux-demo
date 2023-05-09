package com.vkpapps.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User implements Serializable {
    @Id
    private String username;
    private String password;
    private String email;
    private List<String> roles;
    private boolean disabled;
}
