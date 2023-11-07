FROM gcr.io/distroless/java17-debian12:nonroot
ENV TZ="Europe/Oslo"
COPY target/selvbetjening-opptjening.jar /app/app.jar
CMD ["-jar", "/app/app.jar"]