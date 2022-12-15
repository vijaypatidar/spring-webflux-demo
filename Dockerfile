FROM amazoncorretto:11
USER spring:spring
ENV PORT 8080
ENV DISCOVER_SERVER_URL "http://172.18.0.1:8761"
ENV AUTH_ROOT_URL "http://172.18.0.1"
ARG JAR_FILE=build/libs/*.jar
EXPOSE $PORT
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]