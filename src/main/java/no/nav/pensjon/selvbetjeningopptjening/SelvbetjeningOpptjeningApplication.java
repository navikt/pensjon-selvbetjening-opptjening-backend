package no.nav.pensjon.selvbetjeningopptjening;

import io.prometheus.client.hotspot.DefaultExports;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SelvbetjeningOpptjeningApplication {

    public static void main(String[] args) {
        DefaultExports.initialize();
        SpringApplication.run(SelvbetjeningOpptjeningApplication.class, args);
    }
}
