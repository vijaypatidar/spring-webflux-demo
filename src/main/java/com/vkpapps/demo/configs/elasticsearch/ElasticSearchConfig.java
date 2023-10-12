package com.vkpapps.demo.configs.elasticsearch;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elastic")
@Data
@NoArgsConstructor
public class ElasticSearchConfig {
  private String hostname;
  private String username;
  private String password;
}
