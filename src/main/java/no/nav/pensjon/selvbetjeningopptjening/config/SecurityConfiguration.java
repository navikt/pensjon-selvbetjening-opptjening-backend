package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.context.annotation.Configuration;

@EnableJwtTokenValidation
@Configuration
public class SecurityConfiguration {
}
