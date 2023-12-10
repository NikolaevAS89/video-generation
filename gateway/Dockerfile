FROM gradle:jdk17-alpine as build
ENV GRADLE_HOME /opt/gradle
ADD src gateway/src
COPY build.gradle gateway/build.gradle
WORKDIR gateway
RUN ["gradle", "build"]
RUN ["pwd"]
RUN ls build/libs

FROM openjdk:17-alpine
COPY --from=build /home/gradle/gateway/build/libs/gateway-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]