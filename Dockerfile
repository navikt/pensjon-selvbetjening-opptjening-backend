FROM navikt/java:11
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-opptjening.jar /app/app.jar