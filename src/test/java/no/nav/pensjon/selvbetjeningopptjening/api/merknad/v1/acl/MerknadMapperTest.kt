package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode

class MerknadMapperTest : ShouldSpec({

    should("map from domain representation to transferable representation") {
        MerknadMapper.transferable(
            Merknader(
                perAar = mapOf(
                    2001 to listOf(MerknadCode.INGEN_OPPTJENING),
                    2002 to listOf(MerknadCode.AFP, MerknadCode.DAGPENGER)
                )
            )
        ) shouldBe
                MerknaderV1(
                    merknaderPerAar = mapOf(
                        2001 to listOf(MerknadCodeV1.INGEN_OPPTJENING),
                        2002 to listOf(MerknadCodeV1.AFP, MerknadCodeV1.DAGPENGER)
                    )
                )
    }
})