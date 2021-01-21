package no.nav.pensjon.selvbetjeningopptjening.auth.serviceusertoken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OidcAuthTokenInterceptorTest {

    private static final String ACCESS_TOKEN = "foo";

    @Mock
    ServiceUserTokenGetter tokenGetter;
    @Mock
    ClientHttpRequestExecution requestExecution;
    @Mock
    HttpRequest request;
    @Mock
    ServiceUserToken token;

    @Test
    void intercept_adds_authorization_header() throws Exception {
        when(tokenGetter.getServiceUserToken()).thenReturn(token);
        when(token.getAccessToken()).thenReturn(ACCESS_TOKEN);
        var headers = spy(new HttpHeaders());
        when(request.getHeaders()).thenReturn(headers);

        new OidcAuthTokenInterceptor(tokenGetter).intercept(request, "body".getBytes(), requestExecution);

        verify(headers, times(1)).add("Authorization", "Bearer " + ACCESS_TOKEN);
    }
}
