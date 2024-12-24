FROM openjdk:25-slim-bullseye
WORKDIR /app
RUN apt-get update && apt-get install -y libfreetype6
RUN mkdir /app/uploaded_files && chmod -R 777 /app/uploaded_files
COPY ./target/*.jar /app/app.jar
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]