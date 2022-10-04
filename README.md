# pensjon-selvbetjening-opptjening-backend
Applikasjon som samler inn rådata fra primært POPP 
(register for pensjonsopptjening) og PEN (pensjonsfaglig kjerne) og orkestrerer disse dataene slik at de kan hentes
 og presenteres til bruker av applikasjonen [pensjon-selvbetjening-opptjening-frontend](https://github.com/navikt/pensjon-selvbetjening-opptjening-frontend).

Applikasjonen er laget med utgangspunkt i logikk fra det gamle JSF-baserte skjermbildet "Din pensjonsopptjening" i PSELV som applikasjonen skal erstatte.

## Dokumentasjon

Dokumentasjon for applikasjonen ligger sammen med koden som .adoc-filer under src/site/asciidoc.
Disse filene danner grunnlaget for dokumentasjonen man finner om denne applikasjonen
på pensjonsområdets samleside for dokumentasjon: [PO Pensjon - Systemdokumentasjon](https://pensjon-dokumentasjon.dev.intern.nav.no/pensjon-selvbetjening-opptjening-backend/index.html)

## Kjøring av appen lokalt

Se NAV-intern dokumentasjon: [Confluence: Lokal kjøring av Opptjening backend](https://confluence.adeo.no/pages/viewpage.action?pageId=500958747)

### Endepunkter

#### Pensjonsrelatert:
* Opptjening: http://localhost:8080/api/opptjening/

NB: Denne krever et token for tilgang; det kan skaffes ved først å gjøre en "liksom-innlogging":
* http://localhost:8080/api/mocklogin/FØDSELSNUMMER

#### Applikasjonshelse:
* Liveness: http://localhost:8080/internal/alive
* Readiness: http://localhost:8080/internal/ready
* Helse: http://localhost:8080/api/mgmt/health

#### Applikasjonsmetrikker:
* Prometheus: http://localhost:8080/internal/prometheus

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).
