package no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.acl

import io.kotest.core.spec.style.ShouldSpec
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.acl.PoppPensjonspoeng
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Restpensjon
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate

class PoppRestpensjonTest : ShouldSpec({

    should("map relevant values") {
        val result: Restpensjon = PoppRestpensjon(
            fnr = "12345678901",
            fomDato = LocalDate.of(1991, 2, 4),
            tomDato = LocalDate.of(1992, 3, 5),
            restGrunnpensjon = 1.1,
            restTilleggspensjon = 2.1,
            restPensjonstillegg = 3.1,
            vedtakId = 1L,
            oppdateringArsak = "årsak",
            pensjonspoengListe = listOf(PoppPensjonspoeng()),
        ).fromDto()

        with(result) {
            assertEquals(1.1, restGrunnpensjon)
            assertEquals(2.1, restTilleggspensjon)
            assertEquals(3.1, restPensjonstillegg)
            assertEquals(LocalDate.of(1991, 2, 4), fomDate)
        }

        // Note: No mapping for:
        // - fnr
        // - tomDato
        // - vedtakId
        // - oppdateringArsak
        // - pensjonspoengListe
    }

    should("replace null amounts by zero") {
        with(PoppRestpensjon().fromDto()) {
            assertEquals(0.0, restGrunnpensjon)
            assertEquals(0.0, restTilleggspensjon)
            assertEquals(0.0, restPensjonstillegg)
        }
    }
})