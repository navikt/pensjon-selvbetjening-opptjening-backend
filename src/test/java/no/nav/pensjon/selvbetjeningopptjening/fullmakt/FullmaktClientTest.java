package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktsforholdDto;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

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
    void harFullmaktsforhold_maps_to_object_when_response() {
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockResponse());
            FullmaktsforholdDto response = consumer.harFullmaktsforhold("", "");
            assertTrue(response.getHarFullmaktsforhold());
            assertFalse(response.getErPersonligFullmakt());
        }
    }

    @Test
    void harFullmaktsforhold_should_map_to_null_when_missing_field_erPersonligFullmakt() {
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockFalseResponse());
            FullmaktsforholdDto response = consumer.harFullmaktsforhold("", "");
            assertFalse(response.getHarFullmaktsforhold());
            assertNull(response.getErPersonligFullmakt());
        }
    }

    private static MockResponse mockResponse() {
        return jsonResponse()
                .setBody("""
                        {"harFullmaktsforhold":true,"erPersonligFullmakt":false}
                        """);
    }

    private static MockResponse mockFalseResponse() {
        return jsonResponse()
                .setBody("""
                        {"harFullmaktsforhold":false}
                        """);
    }
}
