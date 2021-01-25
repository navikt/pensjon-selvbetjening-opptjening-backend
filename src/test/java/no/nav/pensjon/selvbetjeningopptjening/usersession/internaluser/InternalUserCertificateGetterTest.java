package no.nav.pensjon.selvbetjeningopptjening.usersession.internaluser;

import no.nav.pensjon.selvbetjeningopptjening.mock.WebClientTest;
import no.nav.pensjon.selvbetjeningopptjening.security.oidc.OidcConfigGetter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class InternalUserCertificateGetterTest extends WebClientTest {

    private static final String CERTIFICATE = "x5c";
    private static final String KEY_ID = "jibNbkFSSbmxPYrN9CFqRk4K4gw";
    private static String keySetResponse = prepareKeySetResponse(CERTIFICATE);
    private InternalUserCertificateGetter certificateGetter;
    private WebClient webClient;

    @Mock
    OidcConfigGetter oidcConfigGetter;

    @BeforeEach
    void initialize() {
        webClient = spy(WebClient.create());
        certificateGetter = new InternalUserCertificateGetter(webClient, oidcConfigGetter);
        when(oidcConfigGetter.getJsonWebKeySetUri()).thenReturn(baseUrl());
    }

    @Test
    void when_matching_key_then_getX509Certificate_returns_certificate() throws Exception {
        prepareResponse(keySetResponse);
        String certificate = certificateGetter.getCertificate(KEY_ID);
        assertEquals(CERTIFICATE, certificate);
    }

    @Test
    void when_no_matching_key_then_getX509Certificate_throws_RuntimeException() {
        prepareResponse(keySetResponse);
        var exception = assertThrows(CertificateException.class, () -> certificateGetter.getCertificate("no-match"));
        assertEquals("No certificate found for key ID 'no-match'", exception.getMessage());
    }

    @Test
    void getX509Certificate_uses_refreshable_cache() throws Exception {
        String newCertificate = "new-cert";
        prepareResponse(keySetResponse);

        String initialCertificate = certificateGetter.getCertificate(KEY_ID);
        verify(webClient, times(1)).get();
        assertEquals(CERTIFICATE, initialCertificate);

        prepareResponse(prepareKeySetResponse(newCertificate));
        String cachedCertificate = certificateGetter.getCertificate(KEY_ID);
        verify(webClient, times(1)).get();
        assertEquals(CERTIFICATE, cachedCertificate);

        certificateGetter.refresh();
        String freshCertificate = certificateGetter.getCertificate(KEY_ID);
        verify(webClient, times(2)).get();
        assertEquals(newCertificate, freshCertificate);
    }

    private static void prepareResponse(String responseBody) {
        prepare(jsonResponse().setBody(responseBody));
    }

    private static String prepareKeySetResponse(String certificate) {
        return "{\n" +
                "  \"keys\": [{\n" +
                "    \"kty\": \"RSA\",\n" +
                "    \"use\": \"sig\",\n" +
                "    \"kid\": \"kg2LYs2T0CTjIfj4rt6JIynen38\",\n" +
                "    \"x5t\": \"kg2LYs2T0CTjIfj4rt6JIynen38\",\n" +
                "    \"n\": \"yTKa6m5GFOllz7oIHFCkvRJoBv7wLMuKIPLHbFGh5yOiO8o3akoqMhf1x6MxINGhZo6dkIrhVlVfWJhEJZPVaQdvyvVmlIZruhcbz3PGMqPAbjq2JqbB1mMnsyGHx-ovP0Cm5xj8sgI8wm67p3nosqzqFvg6mPKVO-w1QBr5seDU2AwU2DR88LF2v03Zjgn4mGvPdUOXihTQoNlf-nJFduXMDyRgZabnR2HlYHhagHwy1beWW1WtEaPz8iBN_0bGkGw705aDBUHJkdTty1mzsCZRur_n0imqXu9IzoSyiq5d0yKrRA5xkA-K3DMeRMquZ5QvPT9Eee4EZfFL97zBfQ\",\n" +
                "    \"e\": \"AQAB\",\n" +
                "    \"x5c\": [\"MIIDBTCCAe2gAwIBAgIQQiR8gZNKuYpH6cP+KIE5ijANBgkqhkiG9w0BAQsFADAtMSswKQYDVQQDEyJhY2NvdW50cy5hY2Nlc3Njb250cm9sLndpbmRvd3MubmV0MB4XDTIwMDgyODAwMDAwMFoXDTI1MDgyODAwMDAwMFowLTErMCkGA1UEAxMiYWNjb3VudHMuYWNjZXNzY29udHJvbC53aW5kb3dzLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMkymupuRhTpZc+6CBxQpL0SaAb+8CzLiiDyx2xRoecjojvKN2pKKjIX9cejMSDRoWaOnZCK4VZVX1iYRCWT1WkHb8r1ZpSGa7oXG89zxjKjwG46tiamwdZjJ7Mhh8fqLz9ApucY/LICPMJuu6d56LKs6hb4OpjylTvsNUAa+bHg1NgMFNg0fPCxdr9N2Y4J+Jhrz3VDl4oU0KDZX/pyRXblzA8kYGWm50dh5WB4WoB8MtW3lltVrRGj8/IgTf9GxpBsO9OWgwVByZHU7ctZs7AmUbq/59Ipql7vSM6EsoquXdMiq0QOcZAPitwzHkTKrmeULz0/RHnuBGXxS/e8wX0CAwEAAaMhMB8wHQYDVR0OBBYEFGcWXwaqmO25Blh2kHHAFrM/AS2CMA0GCSqGSIb3DQEBCwUAA4IBAQDFnKQ98CBnvVd4OhZP0KpaKbyDv93PGukE1ifWilFlWhvDde2mMv/ysBCWAR8AGSb1pAW/ZaJlMvqSN/+dXihcHzLEfKbCPw4/Mf2ikq4gqigt5t6hcTOSxL8wpe8OKkbNCMcU0cGpX5NJoqhJBt9SjoD3VPq7qRmDHX4h4nniKUMI7awI94iGtX/vlHnAMU4+8y6sfRQDGiCIWPSyypIWfEA6/O+SsEQ7vZ/b4mXlghUmxL+o2emsCI1e9PORvm5yc9Y/htN3Ju0x6ElHnih7MJT6/YUMISuyob9/mbw8Vf49M7H2t3AE5QIYcjqTwWJcwMlq5i9XfW2QLGH7K5i8\"],\n" +
                "    \"issuer\": \"https://login.microsoftonline.com/62366534-1ec3-4962-8869-9b5535279d0b/v2.0\"\n" +
                "  }, {\n" +
                "    \"kty\": \"RSA\",\n" +
                "    \"use\": \"sig\",\n" +
                "    \"kid\": \"" + KEY_ID + "\",\n" +
                "    \"x5t\": \"" + KEY_ID + "\",\n" +
                "    \"n\": \"2YX-YDuuTzPiaiZKt04IuUzAjCjPLLmBCVA6npKuZyIouMuaSEuM7BP8QctfCprUY16Rq2-KDrAEvaaKJvsD5ZONddt79yFdCs1E8wKlYIPO74fSpePdVDizflr5W-QCFH9tokbZrHBBuluFojgtbvPMXAhHfZTGC4ItZ0i_Lc9eXwtENHJQC4e4m7olweK1ExM-OzsKGzDlOsOUOU5pN2sHY74nXPqQRH1dQKfB0NT0YrfkbnR8fiq8z-soixfECUXkF8FzWnMnqL6X90wngnuIi8OtH2mvDcnsvUVh3K2JgvSgjRWZbsDx6G-mVQL2vEuHXMXoIoe8hd1ZpV16pQ\",\n" +
                "    \"e\": \"AQAB\",\n" +
                "    \"x5c\": [\"" + certificate + "\"],\n" +
                "    \"issuer\": \"https://login.microsoftonline.com/62366534-1ec3-4962-8869-9b5535279d0b/v2.0\"\n" +
                "  }, {\n" +
                "    \"kty\": \"RSA\",\n" +
                "    \"use\": \"sig\",\n" +
                "    \"kid\": \"18pnMg3UmrWvBK_tkDAbjgM5CmA\",\n" +
                "    \"x5t\": \"18pnMg3UmrWvBK_tkDAbjgM5CmA\",\n" +
                "    \"n\": \"v3tn90CVkqJ57gTZu8bbC37NX0RloPlEnelHmqobAEiDLRuqw7Hv2M5o9iRFhF4sSw64fr6P33stLWKpzVmm4y6HUi89QeQmYCNYzxQy2V-tBiLxWX3vtVYgUFwfZDz4TIEu_Ia7rgTg8aHJ8t_b6mz_xPaWlLJWSFBlNY22z2KX87ULrE5AVNMr125aaPWLhxCGWYrnk5KdMrDGb1cuOExzX4S-_fQrRAWTpQWhqi0bEn9Y0vIWKD9-2CkLmZlJGgOueICSuKwwWXm87RKergHVS9sEGkSaBwWOtCPWLsv01Nc0sZymNs3BkPZsQKioYkdox6beXSQwYsmXtBZHjQ\",\n" +
                "    \"e\": \"AQAB\",\n" +
                "    \"x5c\": [\"MIIC8TCCAdmgAwIBAgIQSoIG9pq9C4lOtPUfxLmCSDANBgkqhkiG9w0BAQsFADAjMSEwHwYDVQQDExhsb2dpbi5taWNyb3NvZnRvbmxpbmUudXMwHhcNMjAwOTEzMDAwMDAwWhcNMjUwOTEzMDAwMDAwWjAjMSEwHwYDVQQDExhsb2dpbi5taWNyb3NvZnRvbmxpbmUudXMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC/e2f3QJWSonnuBNm7xtsLfs1fRGWg+USd6UeaqhsASIMtG6rDse/Yzmj2JEWEXixLDrh+vo/fey0tYqnNWabjLodSLz1B5CZgI1jPFDLZX60GIvFZfe+1ViBQXB9kPPhMgS78hruuBODxocny39vqbP/E9paUslZIUGU1jbbPYpfztQusTkBU0yvXblpo9YuHEIZZiueTkp0ysMZvVy44THNfhL799CtEBZOlBaGqLRsSf1jS8hYoP37YKQuZmUkaA654gJK4rDBZebztEp6uAdVL2wQaRJoHBY60I9Yuy/TU1zSxnKY2zcGQ9mxAqKhiR2jHpt5dJDBiyZe0FkeNAgMBAAGjITAfMB0GA1UdDgQWBBTuNouNBOGNFuUdRuSpaTYO2ZRL6DANBgkqhkiG9w0BAQsFAAOCAQEAKZD3Hn+79D/S9Xby/8zSHhYlsXM4FrxDUggt29o15EdBdxyLHCwc3bXZI2PMn0u5vBoz9T0U/MnzoUIxdkmSI9qhfpaxrz7LPEFsMLN6uqRfN9XbypaGcHXP4Qb4xLjLqlmqc8TCUAi0MAokCVSLl83lIbF8/Kw4hSYWMs7HdiuJLHwIgWSd9mguaPks+w64268bw7yZnKutE8xj1x3f6f6AqD3yZr6B1IfnVgrWX4kQIF8z0XB4J5jPaCcORRRB1GV/aSKhNFXWbJ7Z2UnX5f27dX1mgrnFb48jAZLuoeRn7M7+gtj/wPzRiYN845c9wyx91HDo4xqvq/dFtRQ3dQ==\"],\n" +
                "    \"issuer\": \"https://login.microsoftonline.com/62366534-1ec3-4962-8869-9b5535279d0b/v2.0\"\n" +
                "  }]\n" +
                "}";
    }
}
