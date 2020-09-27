# pensjon-selvbetjening-opptjening-backend
Applikasjon som samler inn rådata fra primært POPP 
(register for pensjonsopptjening) og PEN (pensjonsfaglig kjerne) og orkestrerer disse dataene slik at de kan hentes
 og presenteres til bruker av applikasjonen pensjon-selvbetjening-opptjening-frontend. (https://github.com/navikt/pensjon-selvbetjening-opptjening-frontend)

Applikasjonen er laget med utgangspunkt i logikk fra det gamle JSF-baserte skjermbildet "Din pensjonsopptjening" i PSELV som applikasjonen skal erstatte.

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).

## Dokumentasjon
Dokumentasjon for applikasjonen er lagt sammen med koden og eksponeres på følgende url: https://pensjon-selvbetjening-opptjening-backend.nais.preprod.local
(tilgjengelig i fagsystemsonen)

## Utvikling lokalt

I et Java-IDE kjør `SelvbetjeningOpptjeningApplication`.

Bruk profil `laptop` for test på mobilitetsløsning, eller `uimage` for Utviklerimage.

Eksempel VM options i Run/Debug config: `-Dspring.profiles.active=laptop -Dfnr=<fnr>`

På laptop bruk k8s port-forwarding for tjenestene STS, PEN og POPP.

### Endepunkter

#### Pensjonsrelaterte (sikrede):
* Opptjening: http://localhost:8080/api/opptjening/

#### Applikasjonshelse (usikrede):
* Liveness: http://localhost:8080/api/internal/isAlive
* Readiness: http://localhost:8080/api/internal/isReady
* Helse: http://localhost:8080/api/mgmt/health

#### Metrikker (usikret):
* Prometheus: http://localhost:8080/api/mgmt/prom
