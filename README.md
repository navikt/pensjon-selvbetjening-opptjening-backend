# pensjon-selvbetjening-opptjening-backend
Applikasjon som samler inn rådata fra primært POPP 
(register for pensjonsopptjening) og PEN (pensjonsfaglig kjerne) og orkestrerer disse dataene slik at de kan hentes
 og presenteres til bruker av applikasjonen [pensjon-selvbetjening-opptjening-frontend](https://github.com/navikt/pensjon-selvbetjening-opptjening-frontend).

Applikasjonen er laget med utgangspunkt i logikk fra det gamle JSF-baserte skjermbildet "Din pensjonsopptjening" i PSELV som applikasjonen skal erstatte.

## Dokumentasjon

Dokumentasjon for applikasjonen er lagt sammen med koden og eksponeres på følgende URL: https://pensjon-selvbetjening-opptjening-backend.nais.preprod.local
(tilgjengelig i fagsystemsonen)

## Kjøring av appen lokalt

I et Java-IDE kjør `LocalOpptjeningApplication`.

Bruk Spring-profil `laptop` for kjøring på mobilitetsløsning, eller `uimage` på Utviklerimage.

Angi fødselsnummeret/D-nummeret til personen man skal hente data for med VM-option `fnr`.

Eksempel VM-options i Run/Debug config: `-Dspring.profiles.active=laptop -Dfnr=01020312345`

På laptop bruk Kubernetes port-forwarding for tjenestene PEN, POPP, PDL og STS (se [application-laptop.properties](https://github.com/navikt/pensjon-selvbetjening-opptjening-backend/blob/feature/PL-3090/src/main/resources/application-laptop.properties) for detaljer).

### Endepunkter

#### Pensjonsrelatert:
* Opptjening: http://localhost:8080/api/opptjening/

NB: Denne krever et token for tilgang; det kan skaffes ved først å gjøre en "liksom-innlogging":
* http://localhost:8080/api/mocklogin/

#### Applikasjonshelse:
* Liveness: http://localhost:8080/api/internal/isAlive
* Readiness: http://localhost:8080/api/internal/isReady
* Helse: http://localhost:8080/api/mgmt/health

#### Applikasjonsmetrikker:
* Prometheus: http://localhost:8080/api/mgmt/prom

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).
