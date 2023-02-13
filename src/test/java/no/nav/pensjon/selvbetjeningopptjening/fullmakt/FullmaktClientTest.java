package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FullmaktClientTest extends WebClientTest {

    private FullmaktClient consumer;

    @Autowired
    @Qualifier("epoch-support")
    WebClient webClient;

    @BeforeEach
    void initialize() {
        consumer = new FullmaktClient(webClient, baseUrl());
    }

    @Test
    void harFullmaktsforhold_returns_true_when_true_response(){
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockTrueResponse());
            assertTrue(consumer.harFullmaktsforhold("", "" ));
        }
    }

    @Test
    void harFullmaktsforhold_returns_false_when_false_response(){
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockFalseResponse());
            assertFalse(consumer.harFullmaktsforhold("", "" ));
        }
    }

    @Test
    void harFullmaktsforhold_returns_false_when_null_response(){
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockNullResponse());
            assertFalse(consumer.harFullmaktsforhold("", "" ));
        }
    }

    private static MockResponse mockTrueResponse(){
        return jsonResponse()
                .setBody("true");
    }

    private static MockResponse mockFalseResponse(){
        return jsonResponse()
                .setBody("false");
    }

    private static MockResponse mockNullResponse(){
        return jsonResponse()
                .setBody("null");
    }
}
