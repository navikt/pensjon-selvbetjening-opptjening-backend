package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon.acl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon

class PensjonRepresentasjonMapperTest : FunSpec({

    test("'fromDto' should map validity and 'representert navn'") {
        PensjonRepresentasjonMapper.fromDto(
            source = PensjonRepresentasjonResult(
                hasValidRepresentasjonsforhold = true,
                representertNavn = "X",
                representertPidKryptert = "Y",
                representertPid = "Z"
            )
        ) shouldBe Representasjon(
            isValid = true,
            fullmaktGiverNavn = "X"
        )
    }
})
