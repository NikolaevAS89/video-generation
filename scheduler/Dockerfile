FROM gradle:jdk17-alpine as build
ENV GRADLE_HOME /opt/gradle
ADD src scheduler/src
COPY build.gradle scheduler/build.gradle
WORKDIR scheduler
RUN ["gradle", "build"]
RUN ["pwd"]
RUN ls build/libs

FROM openjdk:17-alpine
COPY --from=build /home/gradle/scheduler/build/libs/scheduler-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]