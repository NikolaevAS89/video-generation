FROM gradle:jdk17

ADD db db
COPY build.gradle build.gradle
COPY db.properties db.properties

ENTRYPOINT ["gradle", "build", "update"]