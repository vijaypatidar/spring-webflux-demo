package com.vkpapps.demo.models;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "otps")
@Builder
public class Otp {
  @Id
  private String id;
  private int otpPin;
  private String username;
  private Date validUpTo;
}
