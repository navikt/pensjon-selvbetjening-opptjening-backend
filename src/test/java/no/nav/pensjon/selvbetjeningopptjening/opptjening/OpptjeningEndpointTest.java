package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.finn.unleash.FakeUnleash;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpptjeningEndpoint.class)
class OpptjeningEndpointTest {

    private static final String FEATURE = "pesys.pen.PL-1441";
    private static final String URI = "/api/opptjening";
    private static final String FNR = "foo";
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
        when(fnrExtractor.extract()).thenReturn(FNR);
    }

    @Test
    void getOpptjeningForFnr_returns_opptjeningJson_when_feature_enabled() throws Exception {
        featureToggler.enable(FEATURE);
        when(provider.calculateOpptjeningForFnr(FNR)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json("{'opptjeningData':null,'numberOfYearsWithPensjonspoeng':1}"));
    }

    @Test
    void getOpptjeningForFnr_returns_statusForbidden_when_feature_disabled() throws Exception {
        featureToggler.disable(FEATURE);
        when(provider.calculateOpptjeningForFnr(FNR)).thenReturn(response());

        mvc.perform(get(URI))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    void getOpptjeningForFnr_returns_statusInternalServerError_when_failedCallingExternalService() throws Exception {
        featureToggler.enable(FEATURE);
        when(provider.calculateOpptjeningForFnr(FNR)).thenThrow(new FailedCallingExternalServiceException("sp", "sid", "details", new Exception("cause")));

        mvc.perform(get(URI))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    private static OpptjeningResponse response() {
        var response = new OpptjeningResponse();
        response.setNumberOfYearsWithPensjonspoeng(1);
        return response;
    }
}
