FROM openjdk:24-slim-bullseye
WORKDIR /app
COPY ./target/*.jar /app/app.jar
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]