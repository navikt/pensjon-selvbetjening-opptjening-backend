package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.audit.Auditor;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.UserType;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.CookieBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.filter.RequestBasedBrukerbytte;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
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

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpptjeningOnBehalfEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class OpptjeningOnBehalfEndpointTest {

    private static final Pid PID = new Pid("04925398980");
    private static final String URI = "/api/opptjeningonbehalf?fnr=" + PID;
    private static final String VEILEDER_GROUP_ID = "959ead5b-99b5-466b-a0ff-5fdbc687517b";

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
    private OpptjeningProvider provider;
    @MockBean
    private GroupChecker groupChecker;
    @MockBean
    private JwsValidator jwsValidator;
    @MockBean
    private Jws<Claims> jws;
    @MockBean
    private Auditor auditor;
    @MockBean
    private RequestBasedBrukerbytte requestBased;
    @Mock
    private Claims claims;

    @BeforeEach
    void initialize() {
        when(ingressTokenFinder.getIngressTokenInfo(any(), anyBoolean())).thenReturn(TokenInfo.valid("jwt1", UserType.INTERNAL, claims, PID.getPid()));
    }

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_authorized() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID)).thenReturn(response());
        when(groupChecker.isUserAuthorized(eq(PID), any())).thenReturn(true);
        logInAsAuthorizedInternalUser();

        mvc.perform(
                        get(URI)
                                .cookie(new Cookie("iu-idtoken", "foo")))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                                                  {
                                                   "opptjeningData": {
                                                    "1992": {
                                                      "pensjonsgivendeInntekt": null,
                                                      "pensjonsbeholdning": 1234,
                                                      "omsorgspoeng": null,
                                                      "omsorgspoengType": null,
                                                      "pensjonspoeng": null,
                                                      "merknader": [],
                                                      "restpensjon": null,
                                                      "maksUforegrad": 0,
                                                      "endringOpptjening": null
                                                    }
                                                  },
                                                  "numberOfYearsWithPensjonspoeng": 1,
                                                  "fodselsaar": 1950,
                                                  "andelPensjonBasertPaBeholdning": 10
                            }
                        """));
    }

    @Test
    void getOpptjeningForFnr_returns_statusForbidden_when_userNotMemberOfGroup() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID)).thenReturn(response());
        when(groupChecker.isUserAuthorized(eq(PID), any())).thenReturn(false);
        logInAsUnauthorizedInternalUser();

        mvc.perform(
                        get(URI)
                                .cookie(new Cookie("iu-idtoken", "foo")))
                .andExpect(status().isForbidden());
    }

    private void logInAsAuthorizedInternalUser() {
        logIn(VEILEDER_GROUP_ID);
    }

    private void logInAsUnauthorizedInternalUser() {
        logIn("some irrelevant group");
    }

    private void logIn(String groupId) {
        when(jwsValidator.validate(anyString())).thenReturn(TokenInfo.valid("jwt", UserType.INTERNAL, claims, "user1"));
        when(jws.getBody()).thenReturn(claims);
        when(claims.get("groups")).thenReturn(List.of(groupId));
        when(claims.get("NAVident")).thenReturn("X123456");
    }

    private static OpptjeningResponse response() {
        LocalDate birthDate = LocalDate.of(1950, 1, 1);

        var response = new OpptjeningResponse(new Person(
                PidGenerator.generatePid(birthDate),
                null,
                null,
                null,
                new BirthDate(birthDate)),
                10,
                null,
                null);

        response.setOpptjeningData(opptjeningerByYear());
        response.setNumberOfYearsWithPensjonspoeng(1);
        return response;
    }

    private static Map<Integer, OpptjeningDto> opptjeningerByYear() {
        var opptjeningerByYear = new HashMap<Integer, OpptjeningDto>();
        var opptjening = new OpptjeningDto();
        opptjening.setPensjonsbeholdning(1234L);
        opptjeningerByYear.put(1992, opptjening);
        return opptjeningerByYear;
    }
}
