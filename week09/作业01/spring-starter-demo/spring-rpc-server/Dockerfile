FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
EXPOSE ${EXPORT_PORT}
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]