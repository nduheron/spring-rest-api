FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y openssl

WORKDIR /usr/share/spring-rest-api

RUN openssl req -newkey rsa:2048 -new -nodes -x509 -days 365 -subj "/C=${CERT_COUNTRY_CODE}/ST=${CERT_STATE}/L=${CERT_LOCALITY}/O=${CERT_ORGANIZATION}/OU=${CERT_ORGANIZATIONAL_UNIT} Department/CN=${DOMAIN}"  -keyout key.pem -out cert.pem

COPY target/spring-rest-api.jar spring-rest-api.jar

ENTRYPOINT  ["java", "-Xmx512m", "-jar", "spring-rest-api.jar"]
