package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfo;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpptjeningEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class OpptjeningEndpointTest {

    private static final String URI = "/api/opptjening";
    private static final Pid PID = PidGenerator.generatePidAtAge(65);

    @Autowired
    private MockMvc mvc;
    @MockBean
    OpptjeningProvider provider;
    @MockBean
    LoginInfoGetter loginInfoGetter;

    @BeforeEach
    void initialize() {
        when(loginInfoGetter.getLoginInfo()).thenReturn(new LoginInfo(PID, LoginSecurityLevel.LEVEL4));
    }

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_OK() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID, LoginSecurityLevel.LEVEL4)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json("{'opptjeningData':null,'numberOfYearsWithPensjonspoeng':1}"));
    }

    @Test
    void getOpptjeningForFnr_returns_statusInternalServerError_when_failedCallingExternalService() throws Exception {
        when(provider.calculateOpptjeningForFnr(PID, LoginSecurityLevel.LEVEL4)).thenThrow(new FailedCallingExternalServiceException("sp", "sid", "details", new Exception("cause")));

        mvc.perform(get(URI))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(content().string(""));
    }

    @Test
    void getOpptjeningForFnr_returns_statusBadRequest_when_invalidPid() throws Exception {
        when(provider.calculateOpptjeningForFnr(any(), eq(LoginSecurityLevel.LEVEL4))).thenThrow(new PidValidationException(""));

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
                10);
        response.setNumberOfYearsWithPensjonspoeng(1);
        return response;
    }
}
