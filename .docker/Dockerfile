FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-opptjening.jar /app/app.jar
CMD ["-jar", "/app/app.jar"]