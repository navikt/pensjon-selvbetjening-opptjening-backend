package no.nav.pensjon.selvbetjeningopptjening.consumer.skjerming;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkjermingConsumerTest extends WebClientTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private SkjermingConsumer consumer;

    @BeforeEach
    void initialize() {
        consumer = new SkjermingConsumer(baseUrl());
    }

    @Test
    void isSkjermet_returns_false_when_userIsNotSkjermet() {
        prepare(response("false"));
        assertFalse(consumer.isSkjermet(PID));
    }

    @Test
    void isSkjermet_returns_true_when_userIsSkjermet() {
        prepare(response("true"));
        assertTrue(consumer.isSkjermet(PID));
    }

    @Test
    void isSkjermet_returns_true_when_badRequest() {
        prepare(badRequestResponse());
        assertTrue(consumer.isSkjermet(PID));
    }

    private static MockResponse response(String isSkjermet) {
        return jsonResponse()
                .setBody(isSkjermet);
    }

    private static MockResponse badRequestResponse() {
        return jsonResponse()
                .setBody("{\n" +
                        "    \"timestamp\": 1611593149366,\n" +
                        "    \"status\": 400,\n" +
                        "    \"error\": \"Bad Request\",\n" +
                        "    \"message\": \"\",\n" +
                        "    \"path\": \"/skjermet\"\n" +
                        "}");
    }
}
