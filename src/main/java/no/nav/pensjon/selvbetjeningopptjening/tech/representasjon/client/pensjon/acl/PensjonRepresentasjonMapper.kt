package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon

object PensjonRepresentasjonMapper {

    fun fromDto(source: PensjonRepresentasjonResult) =
        Representasjon(
            isValid = source.hasValidRepresentasjonsforhold == true,
            fullmaktGiverNavn = source.fullmaktsgiverNavn ?: ""
        )
}
