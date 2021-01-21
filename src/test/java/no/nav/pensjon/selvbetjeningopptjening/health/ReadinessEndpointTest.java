package no.nav.pensjon.selvbetjeningopptjening.health;

import no.nav.pensjon.selvbetjeningopptjening.SelvbetjeningOpptjeningApplication;
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
    Selftest selftest;

    @Test
    void isAlive() throws Exception {
        mvc.perform(get("/api/internal/isAlive"))
                .andExpect(status().isOk());
    }

    @Test
    void isReady() throws Exception {
        mvc.perform(get("/api/internal/isReady"))
                .andExpect(status().isOk());
    }

    @Test
    void selftest() throws Exception {
        mvc.perform(get("/api/internal/selftest"))
                .andExpect(status().isOk());
    }
}
