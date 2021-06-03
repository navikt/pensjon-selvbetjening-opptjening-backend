package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupChecker;
import no.nav.pensjon.selvbetjeningopptjening.security.jwt.JwsValidator;
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

    private static final Pid PID = PidGenerator.generatePidAtAge(65);
    private static final String URI = "/api/opptjeningonbehalf?fnr=" + PID;
    private static final String VEILEDER_GROUP_ID = "959ead5b-99b5-466b-a0ff-5fdbc687517b";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OpptjeningProvider provider;
    @MockBean
    private GroupChecker groupChecker;
    @MockBean
    private JwsValidator jwsValidator;
    @MockBean
    private Jws<Claims> jws;
    @Mock
    private Claims claims;

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_authorized() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID, LoginSecurityLevel.INTERNAL)).thenReturn(response());
        when(groupChecker.isUserAuthorized(eq(PID), any())).thenReturn(true);
        logInAsAuthorizedInternalUser();

        mvc.perform(
                get(URI)
                        .cookie(new Cookie("iu-idtoken", "foo")))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "  'opptjeningData': {\n" +
                        "    '1992': {\n" +
                        "      'pensjonsgivendeInntekt': null,\n" +
                        "      'pensjonsbeholdning': 1234,\n" +
                        "      'omsorgspoeng': null,\n" +
                        "      'omsorgspoengType': null,\n" +
                        "      'pensjonspoeng': null,\n" +
                        "      'merknader': [],\n" +
                        "      'restpensjon': null,\n" +
                        "      'maksUforegrad': 0,\n" +
                        "      'endringOpptjening': null\n" +
                        "    }\n" +
                        "  },\n" +
                        "  'numberOfYearsWithPensjonspoeng': 1,\n" +
                        "  'fodselsaar': 1950,\n" +
                        "  'andelPensjonBasertPaBeholdning': 10\n" +
                        "}"));
    }

    @Test
    void getOpptjeningForFnr_returns_statusForbidden_when_userNotMemberOfGroup() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID, LoginSecurityLevel.INTERNAL)).thenReturn(response());
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
        when(jwsValidator.validate(anyString())).thenReturn(jws);
        when(jws.getBody()).thenReturn(claims);
        when(claims.get("groups")).thenReturn(List.of(groupId));
    }

    private static OpptjeningResponse response() {
        LocalDate birthDate = LocalDate.of(1950, 1, 1);

        var response = new OpptjeningResponse(new Person(
                PidGenerator.generatePid(birthDate),
                null,
                null,
                null,
                new BirthDate(birthDate)),
                10);

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
