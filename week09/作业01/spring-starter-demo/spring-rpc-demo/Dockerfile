FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
#ADD dockerize/alpine-linux-amd64-v0.6.1/dockerize dockerize
EXPOSE ${EXPORT_PORT}
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]