FROM ghcr.io/navikt/poao-baseimages/java:17
COPY /application/target/poao-tilgang-app.jar app.jar
