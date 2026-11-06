package no.nav.pensjon.selvbetjeningopptjening.api.opptjening.v1

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknadkode
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Beholdning
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Uttaksgrad
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.AarvisBeholdningOgMerknader
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknader
import no.nav.pensjon.selvbetjeningopptjening.opptjening.beholdning.BeholdningOgMerknaderService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.Month

@WebMvcTest(BeholdningOgMerknaderControllerV1::class)
@ContextConfiguration(classes = [SelvbetjeningOpptjeningApplication::class])
class BeholdningOgMerknaderControllerV1Test : ShouldSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var auditor: Auditor

    @MockkBean
    private lateinit var beholdningOgMerknaderService: BeholdningOgMerknaderService

    @MockkBean(relaxed = true)
    private lateinit var targetPidExtractor: TargetPidExtractor

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    init {
        beforeSpec {
            every { auditor.audit(any(), any()) } returns Unit
            every { targetPidExtractor.pid() } returns pid
            every { groupMembershipService.innloggetBrukerHarTilgang(any()) } returns true
        }

        context("OK request") {
            should("gi respons OK inneholdende årvise beholdninger og merknadslister") {
                every { beholdningOgMerknaderService.beholdningOgMerknader(any()) } returns AarvisBeholdningOgMerknader(
                    perAar = mapOf(
                        2021 to BeholdningOgMerknader(
                            beholdning = beholdning(aar = 2021, beloep = 210000),
                            merknadListe = listOf(
                                Merknad(
                                    kode = Merknadkode.GRADERT_UTTAK,
                                    uttaksgrad = Uttaksgrad(
                                        grad = 60,
                                        fom = LocalDate.of(2021, 11, 1)
                                    )
                                )
                            )
                        ),
                        2022 to BeholdningOgMerknader(
                            beholdning = beholdning(aar = 2022, beloep = 220000),
                            merknadListe = listOf(Merknad(kode = Merknadkode.AFP))
                        )
                    )
                )

                mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().json(OK_RESPONSE_BODY))
            }
        }
    }
}

private const val URL = "/api/v1/beholdninger-og-merknader"

@Language("json")
const val OK_RESPONSE_BODY = """{
    "perAar": {
        "2021": {
            "beholdning": {
                "beloep": 210000
            },
            "merknadListe": [
                {
                    "kode": "GRADERT_UTTAK",
                    "uttaksgrad": {
                        "grad": 60,
                        "kalendermaanedFom": 11
                    }
                }
            ]
        },
        "2022": {
            "beholdning": {
                "beloep": 220000
            },
            "merknadListe": [
                {
                    "kode": "AFP"
                }
            ]
        }
    }
}"""

private fun beholdning(aar: Int, beloep: Int) =
    Beholdning(
        null,
        null,
        null,
        null,
        beloep.toDouble(),
        null,
        LocalDate.of(aar, Month.JANUARY, 1),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )