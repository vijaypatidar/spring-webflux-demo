package com.vkpapps.demo;

import com.test.Person;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SpringBootApplication
@EnableWebFlux
@OpenAPIDefinition(info = @Info(title = "APIs",
    version = "1.0",
    description = "Documentation APIs v1.0"))
@ConfigurationPropertiesScan
public class CoolSpringWebfluxApplication {

  public static void main(String[] args) throws IOException {
    Person person = Person.newBuilder().setEmail("vijay@test.com").setName("Vijay h j Patidar").setId(71).build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(5000);
    person.writeTo(outputStream);
    System.out.println("Person:"+outputStream);
//    SpringApplication.run(CoolSpringWebfluxApplication.class, args);
  }

}
