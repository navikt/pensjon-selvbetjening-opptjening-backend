FROM navikt/java:17
COPY init.sh /init-scripts/init.sh
COPY target/selvbetjening-opptjening.jar /app/app.jar

CMD ./init-scripts/init.sh
