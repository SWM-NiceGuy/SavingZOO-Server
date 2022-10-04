FROM openjdk:11
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} .
ENTRYPOINT ["java","-jar","application.jar"]
