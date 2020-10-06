package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import no.finn.unleash.FakeUnleash;

import no.nav.pensjon.selvbetjeningopptjening.config.OpptjeningFeature;
import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.unleash.UnleashProvider;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OpptjeningEndpoint.class)
class OpptjeningEndpointTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OpptjeningProvider opptjeningProvider;

    @MockBean
    StringExtractor stringExtractor;

    @BeforeEach
    void setUp(){
        FakeUnleash fakeUnleash = new FakeUnleash();
        fakeUnleash.enable(OpptjeningFeature.PEN_PL1441);
        UnleashProvider.initialize(fakeUnleash);
    }

    @Test
    void should_return_status_OK_when_toggle_isEnabled_and_OpptjeningProvider_returns_OpptjeningResponse() throws Exception {
        OpptjeningResponse opptjeningResponse = new OpptjeningResponse();

        when(opptjeningProvider.calculateOpptjeningForFnr(anyString())).thenReturn(opptjeningResponse);

        mvc.perform(get("/api/opptjening")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_403_when_toggle_isDisabled() throws Exception {
        OpptjeningResponse opptjeningResponse = new OpptjeningResponse();

        FakeUnleash fakeUnleash = new FakeUnleash();
        fakeUnleash.disable(OpptjeningFeature.PEN_PL1441);
        UnleashProvider.initialize(fakeUnleash);

        when(stringExtractor.extract()).thenReturn("fnr");
        when(opptjeningProvider.calculateOpptjeningForFnr(anyString())).thenReturn(opptjeningResponse);

        mvc.perform(get("/api/opptjening")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("403 FORBIDDEN \"The service is not made available for the specified user yet\"", Objects
                        .requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void should_return_InternalServerError_when_OpptjeningProvider_throws_Exception() throws Exception {

        when(stringExtractor.extract()).thenReturn("fnr");
        when(opptjeningProvider.calculateOpptjeningForFnr(anyString())).thenThrow(FailedCallingExternalServiceException.class);

        mvc.perform(get("/api/opptjening")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("exception is no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException")));
    }
}