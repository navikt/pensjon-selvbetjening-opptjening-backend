package no.nav.pensjon.selvbetjeningopptjening;

import io.prometheus.client.hotspot.DefaultExports;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class LocalOpptjeningApplication {

    private static final int AUTH_SERVER_PORT = 8081;
    private static MockOAuth2Server authServer;

    public static void main(String[] args) {
        DefaultExports.initialize();
        startMockAuthServer();
        SpringApplication.run(SelvbetjeningOpptjeningApplication.class, args);
    }

    public static MockOAuth2Server getAuthServer() {
        return authServer;
    }

    private static void startMockAuthServer() {
        authServer = new MockOAuth2Server();

        try {
            authServer.start(AUTH_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
