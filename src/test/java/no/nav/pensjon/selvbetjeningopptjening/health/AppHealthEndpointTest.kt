package no.nav.pensjon.selvbetjeningopptjening.health

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication
import no.nav.pensjon.selvbetjeningopptjening.mock.MockSecurityConfiguration
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(AppHealthEndpoint::class)
@ContextConfiguration(classes = [SelvbetjeningOpptjeningApplication::class])
@Import(MockSecurityConfiguration::class)
class AppHealthEndpointTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var auditor: Auditor

    @MockkBean
    private lateinit var pidExtractor: TargetPidExtractor

    @MockkBean
    private lateinit var groupMembershipService: GroupMembershipService

    init {
        beforeSpec {
            every { auditor.audit(any(), any()) } returns Unit
            every { pidExtractor.pid() } returns pid
            every { groupMembershipService.innloggetBrukerHarTilgang(any()) } returns true
        }

        test("isAlive") {
            mvc.get("/internal/alive").andReturn().response.status shouldBe 200
        }

        test("isReady") {
            mvc.get("/internal/ready").andReturn().response.status shouldBe 200
        }
    }
}