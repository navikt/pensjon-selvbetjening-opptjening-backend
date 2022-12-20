package no.nav.pensjon.selvbetjeningopptjening.unleash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import no.finn.unleash.FakeUnleash;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.RequestBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnleashStatusEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class UnleashStatusEndpointTest {

    private static final Pid PID = new Pid("04925398980");
    private static final String URI = "/api/unleash";
    private static FakeUnleash featureToggler;

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
    private RequestBasedBrukerbytte requestBased;
    @Mock
    private Claims claims;

    @BeforeAll
    static void setUp() {
        featureToggler = new FakeUnleash();
        UnleashProvider.initialize(featureToggler);
    }

    @BeforeEach
     void initialize() {
        when(ingressTokenFinder.getIngressTokenInfo(any(), anyBoolean())).thenReturn(TokenInfo.valid("jwt1", UserType.EXTERNAL, claims, PID.getPid()));
    }

    @Test
    void getUnleashStatus_returns_Status_OK_when_valid_input() throws Exception {
        mvc.perform(post(URI)
                .contentType("application/json")
                .content(UnleashStatusEndpointTest.request()))
                .andExpect(status().isOk());
    }

    @Test
    void getUnleashStatus_returns_UnleashStatusResponseJson_when_requestBody_has_toggleList() throws Exception {
        mvc.perform(post(URI)
                .contentType("application/json")
                .content(UnleashStatusEndpointTest.request()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"toggles\":{\"toggle1\":false}}"));
    }

    @Test
    void getUnleashStatus_returns_UnleashStatus_true_when_toggle_is_enabled() throws Exception {
        featureToggler.enable("toggle1");

        mvc.perform(post(URI)
                .contentType("application/json")
                .content(UnleashStatusEndpointTest.request()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"toggles\":{\"toggle1\":true}}"));
    }

    @Test
    void getUnleashStatus_returns_empty_json_when_empty_request() throws Exception {
        mvc.perform(post(URI)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    private static String request() {
        var request = new UnleashStatusRequest();
        request.setToggleList(Collections.singletonList("toggle1"));
        var mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
