FROM openjdk:11 as build
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} kerdy.jar
ENTRYPOINT ["java","-jar","/kerdy.jar"]
