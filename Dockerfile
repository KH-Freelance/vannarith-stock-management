FROM openjdk:25-slim-bullseye
WORKDIR /app
COPY ./target/*.jar /app/app.jar
RUN apt-get update && apt-get install -y libfreetype6
RUN mkdir /uploaded_files && chmod -R 777 /uploaded_files
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]