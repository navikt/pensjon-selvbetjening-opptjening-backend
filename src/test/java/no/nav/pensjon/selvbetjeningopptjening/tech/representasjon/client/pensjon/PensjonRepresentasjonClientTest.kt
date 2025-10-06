package no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.client.pensjon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.Representasjon
import no.nav.pensjon.selvbetjeningopptjening.tech.trace.TraceAid
import no.nav.pensjon.selvbetjeningopptjening.testutil.Arrange
import no.nav.pensjon.selvbetjeningopptjening.testutil.arrangeOkJsonResponse
import okhttp3.mockwebserver.MockWebServer
import org.intellij.lang.annotations.Language
import org.springframework.web.reactive.function.client.WebClient

class PensjonRepresentasjonClientTest : FunSpec({

    var server: MockWebServer? = null
    var baseUrl: String? = null

    @Language("json")
    val responseBody = """{
    "hasValidRepresentasjonsforhold": true,
    "fullmaktsgiverNavn": "Abc Æøå",
    "fullmaktsgiverFnrKryptert": "kryptisk",
    "fullmaktsgiverFnr": "$pid"
}"""

    beforeSpec {
        Arrange.security()
        server = MockWebServer().apply { start() }
        baseUrl = "http://localhost:${server.port}"
    }

    afterSpec {
        server?.shutdown()
    }

    test("hasValidRepresentasjonsforhold should return fullmaktsgiver if valid representasjon") {
        server!!.arrangeOkJsonResponse(responseBody)

        Arrange.webClientContextRunner().run {
            val client = PensjonRepresentasjonClient(
                baseUrl = baseUrl!!,
                webClientBuilder = it.getBean(WebClient.Builder::class.java),
                traceAid = mockk<TraceAid>(relaxed = true),
                retryAttempts = "0"
            )

            client.hasValidRepresentasjonsforhold(pid) shouldBe
                    Representasjon(isValid = true, fullmaktGiverNavn = "Abc Æøå")

            server.takeRequest().requestUrl?.query shouldBe "validRepresentasjonstyper=PENSJON_FULLSTENDIG" +
                    "&validRepresentasjonstyper=PENSJON_BEGRENSET" +
                    "&validRepresentasjonstyper=PENSJON_SKRIV" +
                    "&validRepresentasjonstyper=PENSJON_KOMMUNISER" +
                    "&validRepresentasjonstyper=PENSJON_LES" +
                    "&validRepresentasjonstyper=PENSJON_PENGEMOTTAKER" +
                    "&validRepresentasjonstyper=PENSJON_VERGE" +
                    "&validRepresentasjonstyper=PENSJON_VERGE_PENGEMOTTAKER" +
                    "&includeFullmaktsgiverNavn=false"
        }
    }
})
