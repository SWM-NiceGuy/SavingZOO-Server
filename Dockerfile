FROM openjdk:11
RUN mkdir /app
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app/application.jar
ENTRYPOINT ["java","-jar","/app/application.jar"]