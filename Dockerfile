# Build angular
FROM node:23 AS ng-build

#ghost directory in docker container
WORKDIR /src

RUN npm i -g @angular/cli

COPY news-client/public public
COPY news-client/src src
COPY news-client/*.json .
# COPY ALL json files

RUN npm ci
RUN ng build


#Build spring boot
FROM openjdk:23-jdk AS j-build

WORKDIR /src
COPY news-server/.mvn .mvn
COPY news-server/src src
COPY news-server/mvnw .
COPY news-server/pom.xml .

RUN sed -i 's/\r$//' ./mvnw && chmod a+x ./mvnw

# copy from angular 
COPY --from=ng-build /src/dist/news-client/browser/ src/main/resources/static
# COPY --from=ng-build /src/dist/client-side/browser/* src/main/resources/static

#compile mvn project and run as executable
RUN  ./mvnw package -Dmaven.test.skip=true

#copy the JAR file over to the final container
FROM openjdk:23-jdk

WORKDIR /app
COPY --from=j-build /src/target/news-server-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080

ENV SPRING_SERVLET_MULTIPART_ENABLED=
ENV SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=
ENV SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=
ENV SPRING_SERVLET_MULTIPART_FILE_SIZE_THRESHOLD=

ENV S3_KEY_ACCESS=
ENV S3_KEY_SECRET=
ENV S3_BUCKET_ENDPOINT=
ENV S3_BUCKET_REGION=
ENV S3_BUCKET_BUCKET=

ENV SPRING_DATA_MONGODB_URI=
ENV SPRING_DATA_MONGODB_DATABASE=

EXPOSE ${PORT}

SHELL ["/bin/sh", "-c"]

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar




# docker image build -t sphuar/csf-day35:v1.0.0 .
# docker run -d -p 12345:8080 sphuar/csf-day35:v1.0.0