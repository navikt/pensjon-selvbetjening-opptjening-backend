package no.nav.pensjon.selvbetjeningopptjening.config;

import java.util.Map;

import no.nav.security.token.support.core.configuration.IssuerProperties;
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableJwtTokenValidation
@Configuration
public class SecurityConfiguration {
}
