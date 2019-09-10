FROM openjdk:12-alpine
COPY ./target/graphdblp-backend.jar /usr/app/
COPY ./scripts/application.properties /usr/app/
COPY ./files /usr/files
WORKDIR /usr/app
RUN sh -c 'touch graphdblp-backend.jar'
ENTRYPOINT ["java","-jar","graphdblp-backend.jar"]
EXPOSE 8081
