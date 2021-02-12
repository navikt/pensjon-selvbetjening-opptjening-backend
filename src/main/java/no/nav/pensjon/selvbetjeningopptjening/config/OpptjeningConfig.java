package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsbeholdningCalculator;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.MerknadHandler;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;
import no.nav.pensjon.selvbetjeningopptjening.usersession.token.TokenLoginInfoExtractor;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
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

    @Bean
    public LoginInfoGetter loginInfoGetter(TokenValidationContextHolder context) {
        return new TokenLoginInfoExtractor(context);
    }
}
