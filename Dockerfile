FROM amazoncorretto:11
ENV PORT 8080
ARG JAR_FILE=build/libs/*.jar
EXPOSE $PORT
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]