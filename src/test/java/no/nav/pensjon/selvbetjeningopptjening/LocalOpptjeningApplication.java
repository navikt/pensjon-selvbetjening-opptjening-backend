package no.nav.pensjon.selvbetjeningopptjening;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.prometheus.client.hotspot.DefaultExports;

import no.nav.security.mock.oauth2.MockOAuth2Server;

@SpringBootApplication
public class LocalOpptjeningApplication {

    // On Utviklerimage we need 2 mock auth servers; for internal users and external users
    // (due to token-support's proxy requirement that both must run on localhost).
    // On laptop we need only one (for external users); for internal users the regular Azure AD server is used
    private static final int DEFAULT_AUTH_SERVER_PORT = 8081;
    private static List<MockOAuth2Server> authServers = new ArrayList<>();

    // args syntax: authServerPorts=<port1>,<port2>,...
    // If no args then one server is started on port 8081
    public static void main(String[] args) {
        DefaultExports.initialize();
        startMockAuthServers(args);
        SpringApplication.run(SelvbetjeningOpptjeningApplication.class, args);
    }

    public static MockOAuth2Server getAuthServer() {
        return authServers.get(0);
    }

    private static List<Integer> ports(String[] args) {
        return Arrays.stream(portsAsCsv(args).split(","))
                .map(Integer::parseInt)
                .collect(toList());
    }

    private static String portsAsCsv(String[] args) {
        return args[0].split("=")[1];
    }

    private static void startMockAuthServers(String[] args) {
        List<Integer> ports = args.length < 1 ? List.of(DEFAULT_AUTH_SERVER_PORT) : ports(args);
        ports.forEach(LocalOpptjeningApplication::startMockAuthServer);
    }

    private static void startMockAuthServer(int port) {
        var server = new MockOAuth2Server();

        try {
            server.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        authServers.add(server);
    }
}
