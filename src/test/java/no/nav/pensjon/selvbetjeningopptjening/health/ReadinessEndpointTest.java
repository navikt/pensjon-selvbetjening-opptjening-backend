package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.RequestBasedBrukerbytte;
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

@WebMvcTest(ReadinessEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class ReadinessEndpointTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    IngressTokenFinder ingressTokenFinder;
    @MockBean
    EgressAccessTokenFacade egressAccessTokenFacade;
    @MockBean
    TokenAudiencesVsApps tokenAudiencesVsApps;
    @MockBean
    Logout logout;
    @MockBean
    CookieBasedBrukerbytte brukerbytte;
    @MockBean
    GroupChecker groupChecker;
    @MockBean
    Auditor auditor;
    @MockBean
    RequestBasedBrukerbytte requestBased;
    @MockBean
    Selftest selftest;

    @Test
    void selftest() throws Exception {
        mvc.perform(get("/internal/selftest"))
                .andExpect(status().isOk());
    }
}
