FROM gradle:jdk17-alpine as build
ENV GRADLE_HOME /opt/gradle
ADD src caller/src
COPY build.gradle caller/build.gradle
WORKDIR caller
RUN ["gradle", "build"]
RUN ["pwd"]
RUN ls build/libs

FROM openjdk:17-alpine
COPY --from=build /home/gradle/caller/build/libs/caller-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]