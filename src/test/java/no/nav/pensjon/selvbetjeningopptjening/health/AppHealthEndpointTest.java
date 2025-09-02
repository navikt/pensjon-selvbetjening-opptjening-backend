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

@WebMvcTest(AppHealthEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
@Import(MockSecurityConfiguration.class)
class AppHealthEndpointTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private Auditor auditor;
    @MockBean
    private Selftest selftest;
    @MockBean
    private TargetPidExtractor pidExtractor;
    @MockBean
    private GroupMembershipService groupMembershipService;

    @Test
    void isAlive() throws Exception {
        mvc.perform(get("/internal/alive"))
                .andExpect(status().isOk());
    }

    @Test
    void isReady() throws Exception {
        mvc.perform(get("/internal/ready"))
                .andExpect(status().isOk());
    }
}
