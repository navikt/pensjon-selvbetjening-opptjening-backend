package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.TokenGetterFacade;
import no.nav.pensjon.selvbetjeningopptjening.security.token.StsException;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PdlConsumerTest extends WebClientTest {

    private static final Pid PID = new Pid(TestFnrs.NORMAL);
    private PdlConsumer consumer;

    @Mock
    private TokenValidationContextHolder tokenValidationContextHolder;
    @Mock
    private TokenGetterFacade tokenGetter;

    @BeforeEach
    void initialize() throws StsException {
        when(tokenValidationContextHolder.getTokenValidationContext()).thenReturn(tokenValidationContext());
        when(tokenGetter.getToken(anyString())).thenReturn("token");
        consumer = new PdlConsumer(baseUrl(), tokenGetter);
    }

    @Test
    void should_get_name_and_fodselsdato_when_calling_PDL() throws PdlException {
        prepare(pdlNoErrorsResponse());

        Person response = consumer.getPerson(PID, LoginSecurityLevel.LEVEL4);

        assertEquals("SMART", response.getFornavn());
        assertNull(response.getMellomnavn());
        assertEquals("POTET", response.getEtternavn());
        assertEquals(LocalDate.of(1972, 11, 5), response.getFodselsdato());
    }

    @Test
    void getBirthDates_shall_throwPdlException_when_PDL_returns_error() {
        prepare(pdlErrorResponse());

        var exception = assertThrows(PdlException.class,
                () -> consumer.getPerson(PID, LoginSecurityLevel.LEVEL4));

        assertEquals("Ikke tilgang til å se person", exception.getMessage());
        assertEquals("unauthorized", exception.getErrorCode());
    }

    @Test
    void getBirthDates_shall_throwFailedCallingExternalServiceException_when_PDL_returns_multipleErrors() {
        prepare(pdlMultipleErrorResponse());

        var exception = assertThrows(FailedCallingExternalServiceException.class,
                () -> consumer.getPerson(PID, LoginSecurityLevel.LEVEL4));

        assertEquals("Error when calling the external service PDL." +
                        " Fant ikke person, Ukjent problem oppsto, feilen har blitt logget og følges opp",
                exception.getMessage());
    }

    private static TokenValidationContext tokenValidationContext() {
        Map<String, JwtToken> tokenMap = new HashMap<>();
        tokenMap.put("selvbetjening", new JwtToken(jwt()));
        return new TokenValidationContext(tokenMap);
    }

    private static String jwt() {
        return "eyJraWQiOiJkZWZjZTlkYi05NTk0LTRhOTUtYThjOS1lNTZiZmY2ZDlmYmMiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJzcnZwZW5zam9uIiwiYXVkIjpbInNydnBlbnNqb24iLCJwcmVwcm9kLmxvY2FsIl0sInZlciI6IjEuMCIsIm5iZiI6MTYwMDk0NTAxNiwiYXpwIjoic3J2cGVuc2pvbiIsImlkZW50VHlwZSI6IlN5c3RlbXJlc3N1cnMiLCJhdXRoX3RpbWUiOjE2MDA5NDUwMTYsImlzcyI6Imh0dHBzOlwvXC9zZWN1cml0eS10b2tlbi1zZXJ2aWNlLm5haXMucHJlcHJvZC5sb2NhbCIsImV4cCI6MTYwMDk0ODYxNiwiaWF0IjoxNjAwOTQ1MDE2LCJqdGkiOiI2MDU3Mzc3MS04MTZhLTQxNjYtOWQ2Yi1hYTYxMGFmZDIwNmYifQ" +
                ".dFjzDGxwPbNvHVykflTC5v6Kl44LljoXMjPyxvKY5I6fGNVRUU6N9nzjVycog0IqPcNESeRjP3iw7Js7Rpns-hkaS5d1IhTqo0mWtr5fJ65mNkD9vf3tmGQYGVLmOl5MW8ySqSyiUeZXsD8JVm7inqKCfOpShwm8jNV2wikW3pfmI--vnsVdOTAtkFFCXMwzhLgsioFb9ajBBn5MYJ3Z87WH_2u40RGRc5vpvVf8Jc5KQXOY5LT_k_HgC1JklO4-AIfUsJlolx-KRlw_62nPLA1R3hZDfYkvBi-zU4yeVgLGH9IdlFK3sT2oIJTSUwiOLazTAOEL7MnX1gvDzz6anA";
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
