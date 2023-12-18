#Build stage

FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

# Package stage

FROM openjdk:latest
ENV JAR_NAME=surveys-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .

# Volumes
VOLUME $APP_HOME/surveyImages
VOLUME $APP_HOME/surveyIcons
VOLUME $APP_HOME/exportsJson

EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME