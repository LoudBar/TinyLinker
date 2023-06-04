FROM maven:3.8.1-openjdk-11-slim as build
COPY src /home/application/src
COPY build.sbt /home/application/
COPY target/scala-2.13/tinyLinker.jar /home/application/

FROM openjdk:11-jre-slim
COPY --from=build /home/application/tinyLinker.jar /usr/local/lib/tinyLinker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/tinyLinker.jar"]
