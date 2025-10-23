# pensjon-selvbetjening-opptjening-backend
Applikasjon som samler inn rådata fra primært POPP (register for pensjonsopptjening) og PEN (pensjonsfaglig kjerne) og orkestrerer disse dataene slik at de kan hentes
 og presenteres til bruker av applikasjonen [pensjon-selvbetjening-opptjening-frontend](https://github.com/navikt/pensjon-selvbetjening-opptjening-frontend).

## Endepunkter

* Pensjonsopptjening: `/api/opptjening`

## Dokumentasjon

Dokumentasjon for applikasjonen ligger sammen med koden som `.adoc`-filer under `src/site/asciidoc`.
Disse filene danner grunnlaget for dokumentasjonen man finner om denne applikasjonen
på pensjonsområdets samleside for dokumentasjon: [PO Pensjon - Systemdokumentasjon](https://pensjon-dokumentasjon.dev.intern.nav.no/pensjon-selvbetjening-opptjening-backend/index.html)

### Sikkerhet

NAV-intern dokumentasjon: [Confluence: Sikkerhet i Opptjening backend](https://confluence.adeo.no/display/PEN/Sikkerhet+i+Opptjening+backend)

## Kjøring av appen lokalt

Se NAV-intern dokumentasjon: [Confluence: Lokal kjøring av Opptjening backend](https://confluence.adeo.no/pages/viewpage.action?pageId=500958747)

## Kjøre lokalt - alternativ løsning
Du må være lagt til i teamet pensjonselvbetjening i Nais Console

For å kjøre backenden lokalt er du nødt til å tilgjengeligjøre noen miljøvariabler som ikke er sjekket inn.
Disse kan hentes ved å kjøre skriptet `./fetch-secrets.sh`. For å kjøre skriptet må du:
1. Ha installert [env-fetch](https://github.com/navikt/env-fetch)
2. Være innlogget i GCP: `nais login` eller `gcloud auth login`
3. Sett kontekst til dev gcp: `kubectl config use-context dev-gcp`

Skriptet lagrer miljøvariablene i mappen `/private/tmp` og fjernes når maskinen slåes av.

I IntelliJ kan du velge `edit configurations` ved siden av run-knappen.
* Sett `local` i  active profiles
* Huk av for Enable EnvFile og legge til `/private/tmp/opptjening-backend`

Nå skal du kunne kjøre backend lokalt.

For å kjøre requests mot backend må du ha access tokens. Dette kan du hente her:
* [Access token for borger](https://tokenx-token-generator.intern.dev.nav.no/api/obo?aud=dev-gcp:pensjonselvbetjening:pensjon-selvbetjening-opptjening-backend)
* [Access token for veileder](https://azure-token-generator.intern.dev.nav.no/api/obo?aud=dev-gcp:pensjonselvbetjening:pensjon-veiledning-opptjening-frontend)

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).
