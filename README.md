# pensjon-selvbetjening-opptjening-backend

Backend-applikasjon for Navs tjeneste for visualisering av pensjonsopptjening.

Applikasjonen brukes primært av frontend-applikasjonen [pensjon-selvbetjening-opptjening-frontend](https://github.com/navikt/pensjon-selvbetjening-opptjening-frontend).

## Endepunkt

* `GET /api/opptjening`

Endepunktet krever et Bearer-token fra TokenX eller Entra ID.

## Teknologi

* [Java 21](https://openjdk.org/projects/jdk/21/)
* [Kotlin](https://kotlinlang.org/)
* [Spring Boot 4](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [Nais](https://nais.io/)
* [Kotest](https://kotest.io/)

## Dokumentasjon

Nav-intern dokumentasjon:

* [Systemdokumentasjon](https://pensjon-dokumentasjon.ansatt.dev.nav.no/pensjon-selvbetjening-opptjening-backend/main/index.html)
* [Tilleggsdokumentasjon i Confluence](https://confluence.adeo.no/spaces/PEN/pages/395760861/Opptjening-app)


## Henvendelser

Nav-interne henvendelser kan sendes via Slack i kanalen [#team-planlegge-pensjon](https://nav-it.slack.com/archives/C09A5SC5KQF).
