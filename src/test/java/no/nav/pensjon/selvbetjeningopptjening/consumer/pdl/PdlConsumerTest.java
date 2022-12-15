package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.config.AppIds;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.RequestContextCreator;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.RequestContext;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class PdlConsumerTest extends WebClientTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private PdlConsumer consumer;

    @BeforeEach
    void initialize() {
        consumer = new PdlConsumer(baseUrl());
    }

    @Test
    void should_get_name_and_fodselsdato_when_calling_PDL() throws PdlException {
        prepare(pdlNoErrorsResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PERSONDATALOSNINGEN.appName)) {
            Person response = consumer.getPerson(PID);

            assertEquals("SMART", response.getFornavn());
            assertNull(response.getMellomnavn());
            assertEquals("POTET", response.getEtternavn());
            assertEquals(LocalDate.of(1972, 11, 5), response.getFodselsdato());
        }
    }

    @Test
    void getBirthDates_shall_throwPdlException_when_PDL_returns_error() {
        prepare(pdlErrorResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PERSONDATALOSNINGEN.appName)) {
            var exception = assertThrows(PdlException.class,
                    () -> consumer.getPerson(PID));

            assertEquals("Ikke tilgang til å se person", exception.getMessage());
            assertEquals("unauthorized", exception.getErrorCode());
        }
    }

    @Test
    void getBirthDates_shall_throwFailedCallingExternalServiceException_when_PDL_returns_multipleErrors() {
        prepare(pdlMultipleErrorResponse());

        try (RequestContext ignored = RequestContextCreator.createForExternal(AppIds.PERSONDATALOSNINGEN.appName)) {
            var exception = assertThrows(FailedCallingExternalServiceException.class,
                    () -> consumer.getPerson(PID));

            assertEquals("Error when calling the external service PDL." +
                            " Fant ikke person, Ukjent problem oppsto, feilen har blitt logget og følges opp",
                    exception.getMessage());
        }
    }

    private static MockResponse pdlErrorResponse() {
        return jsonResponse()
                .setBody("""
                        {
                          "errors": [{
                            "message": "Ikke tilgang til å se person",
                            "locations": [{
                              "line": 1,
                              "column": 20
                            }],
                            "extensions": {
                              "code": "unauthorized",
                              "classification": "ExecutionAborted"
                            },
                            "path": ["hentPerson"]
                          }]
                        }""");
    }

    private static MockResponse pdlMultipleErrorResponse() {
        return jsonResponse()
                .setBody("""
                        {
                          "errors": [{
                            "message": "Fant ikke person",
                            "locations": [{
                              "line": 2,
                              "column": 5
                            }],
                            "path": [
                              "hentPerson"
                            ],
                            "extensions": {
                              "code": "not_found",
                              "classification": "ExecutionAborted"
                            }
                          }, {
                            "message": "Ukjent problem oppsto, feilen har blitt logget og følges opp",
                            "locations": [{
                              "line": 2,
                              "column": 5
                            }],
                            "path": [
                              "hentPerson", "bostedsadresse"
                            ],
                            "extensions": {
                              "code": "server_error",
                              "classification": "ExecutionAborted"
                            }
                          }],
                          "data": {
                            "hentPerson": null
                          }
                        }""");
    }

    private static MockResponse pdlNoErrorsResponse() {
        return jsonResponse()
                .setBody(
                        """
                                {
                                    "data": {
                                        "hentPerson": {
                                            "navn": [
                                                {
                                                    "fornavn": "SMART",
                                                    "mellomnavn": null,
                                                    "etternavn": "POTET",
                                                    "folkeregistermetadata": {
                                                        "ajourholdstidspunkt": "2021-03-26T10:56:01"
                                                    },
                                                    "metadata": {
                                                        "master": "FREG",
                                                        "endringer": [
                                                            {
                                                                "registrert": "2021-03-26T10:56:01"
                                                            }
                                                        ]
                                                    }
                                                }
                                            ],
                                            "foedsel": [
                                                {
                                                    "foedselsdato": "1972-11-05",
                                                    "foedselsaar": null
                                                }
                                            ]
                                        }
                                    }
                                }"""
                );
    }
}
