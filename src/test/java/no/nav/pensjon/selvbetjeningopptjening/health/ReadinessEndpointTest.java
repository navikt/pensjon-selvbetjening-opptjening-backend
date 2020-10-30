package no.nav.pensjon.selvbetjeningopptjening.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadinessEndpoint.class)
class ReadinessEndpointTest {

    @Autowired
    private MockMvc mvc;

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
}