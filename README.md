# pensjon-selvbetjening-opptjening-backend
Backend-applikasjon for pensjonsopptjening og pensjonspoeng.

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).

## Utvikling lokalt

I et Java-IDE kjør `SelvbetjeningOpptjeningApplication`.

### Endepunkter

#### Pensjonsrelaterte (sikrede):
* Opptjening: http://localhost:8080/api/opptjening/
* Pensjonspoeng: http://localhost:8080/api/pensjonspoeng/

#### Applikasjonshelse (usikrede):
* Liveness: http://localhost:8080/api/internal/isAlive
* Readiness: http://localhost:8080/api/internal/isReady
* Helse: http://localhost:8080/api/mgmt/health

#### Metrikker (usikret):
* Prometheus: http://localhost:8080/api/mgmt/prom
