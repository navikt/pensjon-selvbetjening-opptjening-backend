package no.nav.pensjon.selvbetjeningopptjening.unleash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.finn.unleash.FakeUnleash;
import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnleashStatusEndpoint.class)
@ContextConfiguration(classes = SelvbetjeningOpptjeningApplication.class)
class UnleashStatusEndpointTest {

    private static final String URI = "/api/unleash";
    private static FakeUnleash featureToggler;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setUp() {
        featureToggler = new FakeUnleash();
        UnleashProvider.initialize(featureToggler);
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
