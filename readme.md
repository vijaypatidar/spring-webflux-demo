# Spring Webflux

[![Build](https://github.com/vijaypatidar/cool-spring-webflux/actions/workflows/gradle.yml/badge.svg)](https://github.com/vijaypatidar/cool-spring-webflux/actions/workflows/gradle.yml)

## Swagger UI

https://localhost:8080/swagger-ui.html

## Configure SSL to enable secure communication by allowing traffic on the HTTPS protocol instead of HTTP.

1. Generate certificate(*.jks file)

 ```
keytool -genkeypair -keyalg RSA -keystore keystore.jks
 ```

2. Setup spring webflux config to use this file(application.yaml or .properties)

```yaml
server:
  port: ${PORT:8080}
  ssl:
    key-store: classpath:keystore.jks
    key-store-password: changeme
```

3. Once these two steps are done successfully, the spring webflux will start the server with configured port but this
   time it will use the https protocol.

```
https://localhost:8080
```

## Create separate dir for integration test

1. Create folder under src

```text
--src
  |--main
  |--test
  |--integrationTest
```

2. Now update build.gradle

```groovy
sourceSets {
    integrationTest {
        java.srcDir file("src/integrationTest/java")
        resources.srcDir file("src/integrationTest/resources")
        runtimeClasspath += sourceSets.main.runtimeClasspath + sourceSets.test.runtimeClasspath
        compileClasspath += sourceSets.main.compileClasspath + sourceSets.test.compileClasspath
    }
}
```
