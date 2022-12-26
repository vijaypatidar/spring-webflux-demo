package com.vkpapps.demo;

import com.vkpapps.demo.controllers.UserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoolSpringWebfluxApplicationTests {

    @Autowired
    UserController userController;
    @Test
    void contextLoads() {
        Assertions.assertNotNull(userController);
    }

}
