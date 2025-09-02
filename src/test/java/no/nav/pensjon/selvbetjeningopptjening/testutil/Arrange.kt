package no.nav.pensjon.selvbetjeningopptjening.testutil

import no.nav.pensjon.selvbetjeningopptjening.WebClientTestConfig
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.jwt
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.AuthType
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.jvm.java

object Arrange {

    fun security() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            initialAuth = TestingAuthenticationToken("TEST_USER", jwt),
            egressTokenSuppliersByService = EgressTokenSuppliersByService(mapOf()),
            target = RepresentasjonTarget(pid, rolle = RepresentertRolle.SELV),
            authType = AuthType.PERSON_SELF
        )
    }

    fun webClientContextRunner(): ApplicationContextRunner =
        ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientTestConfig::class.java)
        )
}

fun MockWebServer.arrangeOkJsonResponse(body: String) {
    this.enqueue(
        MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(HttpStatus.OK.value()).setBody(body)
    )
}

fun MockWebServer.arrangeOkXmlResponse(body: String) {
    this.enqueue(
        MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
            .setResponseCode(HttpStatus.OK.value()).setBody(body)
    )
}

fun MockWebServer.arrangeResponse(status: HttpStatus, body: String) {
    this.enqueue(MockResponse().setResponseCode(status.value()).setBody(body))
}
