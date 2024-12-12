package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.Claims;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.TokenInfo;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.egress.EgressAccessTokenFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.IngressTokenFinder;
import no.nav.pensjon.selvbetjeningopptjening.security.token.TokenAudiencesVsApps;
import no.nav.pensjon.selvbetjeningopptjening.usersession.Logout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpptjeningEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class OpptjeningEndpointTest {

    private static final String URI = "/api/opptjening";
    private static final Pid PID = new Pid("04925398980");

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OpptjeningProvider provider;
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
    @Mock
    private Claims claims;

    @BeforeEach
    void initialize() {
        when(ingressTokenFinder.getIngressTokenInfo(any(), anyBoolean())).thenReturn(TokenInfo.valid("jwt1", UserType.EXTERNAL, claims, PID.getPid()));
    }

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_OK() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                {"opptjeningData":null,"numberOfYearsWithPensjonspoeng":1}
                        """));
    }

    @Test
    void getOpptjeningForFnr_returns_statusInternalServerError_when_failedCallingExternalService() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID)).thenThrow(new FailedCallingExternalServiceException("sp", "sid", "details", new Exception("cause")));

        mvc.perform(get(URI))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(content().string(""));
    }

    @Test
    void getOpptjeningForFnr_returns_statusBadRequest_when_invalidPid() throws Exception {
        when(provider.calculateOpptjeningForFnr(any())).thenThrow(new PidValidationException(""));

        mvc.perform(get(URI))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(content().string(""));
    }

    private static OpptjeningResponse response() {
        var response = new OpptjeningResponse(new Person(
                PID,
                null,
                null,
                null,
                null),
                10,
                null,
                null);
        response.setNumberOfYearsWithPensjonspoeng(1);
        return response;
    }
}
