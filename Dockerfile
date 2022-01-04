FROM openjdk:8-jre
COPY project.jar /app/
EXPOSE 8082
CMD java -jar ./app/project.jar