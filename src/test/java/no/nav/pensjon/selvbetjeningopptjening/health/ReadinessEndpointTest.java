package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.mock.MockSecurityConfiguration;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TargetPidExtractor;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group.GroupMembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadinessEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@Import(MockSecurityConfiguration.class)
class ReadinessEndpointTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    Auditor auditor;
    @MockBean
    Selftest selftest;
    @MockBean
    TargetPidExtractor pidExtractor;
    @MockBean
    GroupMembershipService groupMembershipService;

    @Test
    void selftest() throws Exception {
        mvc.perform(get("/internal/selftest"))
                .andExpect(status().isOk());
    }
}
