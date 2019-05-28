FROM java:8
VOLUME /tmp
CMD ["mvn", "clean", "package"]
ARG JAR_FILE=/target/erlangshen.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=pro","-jar","/app.jar"]