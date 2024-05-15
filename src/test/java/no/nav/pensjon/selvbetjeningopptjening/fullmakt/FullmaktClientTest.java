package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.RepresentasjonValidity;
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
            RepresentasjonValidity response = consumer.hasValidRepresentasjonsforhold("");
            assertTrue(response.hasValidRepresentasjonsforhold());
            assertEquals("Navn Navnesen", response.fullmaktsgiverNavn());
        }
    }

    @Test
    void harFullmaktsforhold_should_map_to_null_when_missing_field_erPersonligFullmakt() {
        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.FULLMAKT.appName)) {
            prepare(mockFalseResponse());
            RepresentasjonValidity response = consumer.hasValidRepresentasjonsforhold("");
            assertFalse(response.hasValidRepresentasjonsforhold());
            assertNull(response.fullmaktsgiverNavn());
        }
    }

    private static MockResponse mockResponse() {
        return jsonResponse()
                .setBody("""
                        {"hasValidRepresentasjonsforhold":true,"fullmaktsgiverNavn":"Navn Navnesen"}
                        """);
    }

    private static MockResponse mockFalseResponse() {
        return jsonResponse()
                .setBody("""
                        {"hasValidRepresentasjonsforhold":false,"fullmaktsgiverNavn": null}
                        """);
    }
}
