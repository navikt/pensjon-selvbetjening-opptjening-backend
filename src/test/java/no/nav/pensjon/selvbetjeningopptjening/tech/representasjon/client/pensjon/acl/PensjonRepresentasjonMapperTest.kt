package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon

class PensjonRepresentasjonMapperTest : FunSpec({

    test("'fromDto' should map validity and 'fullmaktsgivers navn'") {
        PensjonRepresentasjonMapper.fromDto(
            source = PensjonRepresentasjonResult(
                hasValidRepresentasjonsforhold = true,
                fullmaktsgiverNavn = "X",
                fullmaktsgiverFnrKryptert = "Y",
                fullmaktsgiverFnr = "Z"
            )
        ) shouldBe Representasjon(
            isValid = true,
            fullmaktGiverNavn = "X"
        )
    }
})
