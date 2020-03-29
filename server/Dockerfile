FROM openjdk:11-jre-slim-buster
EXPOSE 8080

ADD app.jar /app.jar

CMD java -Xmx6g -jar  /app.jar -m app.core
