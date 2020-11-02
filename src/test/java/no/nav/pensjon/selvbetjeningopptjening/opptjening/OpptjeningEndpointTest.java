package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.finn.unleash.FakeUnleash;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.config.OpptjeningFeature;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.unleash.UnleashProvider;
import no.nav.pensjon.selvbetjeningopptjening.util.FnrExtractor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpptjeningEndpoint.class)
class OpptjeningEndpointTest {

    private static final String URI = "/api/opptjening";
    private static final Pid PID = PidGenerator.generatePidAtAge(65);
    private static FakeUnleash featureToggler;

    @Autowired
    private MockMvc mvc;
    @MockBean
    OpptjeningProvider provider;
    @MockBean
    FnrExtractor fnrExtractor;

    @BeforeAll
    static void setUp() {
        featureToggler = new FakeUnleash();
        UnleashProvider.initialize(featureToggler);
    }

    @BeforeEach
    void initialize() {
        when(fnrExtractor.extract()).thenReturn(PID.getPid());
    }

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_feature_enabled() throws Exception {
        featureToggler.enable(OpptjeningFeature.PL1441);
        when(provider.calculateOpptjeningForFnr(PID)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json("{'opptjeningData':null,'numberOfYearsWithPensjonspoeng':1}"));
    }

    @Test
    void getOpptjeningForFnr_returns_statusForbidden_when_feature_disabled() throws Exception {
        featureToggler.disable(OpptjeningFeature.PL1441);
        when(provider.calculateOpptjeningForFnr(PID)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("403 FORBIDDEN \"The service is not made available for the specified user yet\"", result.getResolvedException().getMessage()))
                .andExpect(content().string(""));
    }

    @Test
    void getOpptjeningForFnr_returns_statusInternalServerError_when_failedCallingExternalService() throws Exception {
        featureToggler.enable(OpptjeningFeature.PL1441);
        when(provider.calculateOpptjeningForFnr(PID)).thenThrow(new FailedCallingExternalServiceException("sp", "sid", "details", new Exception("cause")));

        mvc.perform(get(URI))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(content().string(""));
    }

    @Test
    void getOpptjeningForFnr_returns_statusBadRequest_when_invalidPid() throws Exception {
        featureToggler.enable(OpptjeningFeature.PL1441);
        when(provider.calculateOpptjeningForFnr(any())).thenThrow(new PidValidationException(""));

        mvc.perform(get(URI))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(content().string(""));
    }

    private static OpptjeningResponse response() {
        var response = new OpptjeningResponse(1950);
        response.setNumberOfYearsWithPensjonspoeng(1);
        return response;
    }
}
