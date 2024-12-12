package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppHealthEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class AppHealthEndpointTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private IngressTokenFinder ingressTokenFinder;
    @MockBean
    private EgressAccessTokenFacade egressAccessTokenFacade;
    @MockBean
    private TokenAudiencesVsApps tokenAudiencesVsApps;
    @MockBean
    private Logout logout;
    @MockBean
    private CookieBasedBrukerbytte brukerbytte;
    @MockBean
    private GroupChecker groupChecker;
    @MockBean
    private Auditor auditor;
    @MockBean
    private Selftest selftest;

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
