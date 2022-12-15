# Cool Spring Webflux

## Swagger UI

http://localhost:8080/swagger-ui.html

## Setup SSL to allow traffic on https protocol instead of http

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
