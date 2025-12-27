FROM eclipse-temurin:18-jre

MAINTAINER Abhishek

#Copy the application jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar


#Entry point
# Use ENTRYPOINT when you want your container to run a specific application,
# and CMD for default arguments that users might want to override.
ENTRYPOINT ["java","-jar","/app.jar"]