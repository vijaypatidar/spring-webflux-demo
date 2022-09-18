package com.vkpapps.demo.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
@Document(collection = "otps")
@Builder
public class Otp {
    @Id
    private String id;
    private int otp;
    private String username;
    private Date validUpTo;
}
