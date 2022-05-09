FROM docker.io/library/eclipse-temurin:11-jre-focal
WORKDIR app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} todos-mysql.jar
ENTRYPOINT ["java", "-jar", "/app/todos-mysql.jar"]