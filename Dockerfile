FROM openjdk:25-slim-bullseye
WORKDIR /app
COPY ./target/*.jar /app/app.jar
RUN mkdir /app/uploaded_files && chmod -R 777 /app/uploaded_files
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]