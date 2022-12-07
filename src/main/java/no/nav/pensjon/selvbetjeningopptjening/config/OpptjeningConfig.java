package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsbeholdningCalculator;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.MerknadHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpptjeningConfig {

    @Bean
    public EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator() {
        return new EndringPensjonsbeholdningCalculator();
    }

    @Bean
    public MerknadHandler merknadHandler() {
        return new MerknadHandler();
    }
}
