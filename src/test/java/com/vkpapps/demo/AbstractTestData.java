package com.vkpapps.demo;

import com.vkpapps.demo.models.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class AbstractTestData {
  @Autowired
  protected PasswordEncoder encoder;

  protected User getAdminUser() {
    return new User("vijaypatidar",
        encoder.encode("12345678"),
        "vijay@example.com",
        List.of("ROLE_ADMIN"),
        false
    );
  }

  protected User getNormalUser() {
    return new User("vijaypatidar",
        encoder.encode("12345678"),
        "vijay@example.com",
        List.of("ROLE_USER"),
        false
    );
  }
}