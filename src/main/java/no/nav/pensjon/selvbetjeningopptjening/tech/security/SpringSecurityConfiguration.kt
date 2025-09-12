package no.nav.pensjon.selvbetjeningopptjening.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.AuthenticationEnricherFilter
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.TokenAudienceValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import kotlin.apply

@Configuration
open class SpringSecurityConfiguration {

    @Bean
    open fun filterChain(
        http: HttpSecurity,
        authResolver: AuthenticationManagerResolver<HttpServletRequest>,
        securityContextEnricher: SecurityContextEnricher
    ): SecurityFilterChain =
        http
            .addFilterAfter(
                AuthenticationEnricherFilter(securityContextEnricher),
                BasicAuthenticationFilter::class.java
            )
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/devenv",
                        "/api/status",
                        "/logout",
                        "/favicon.ico",
                        "/internal/**",
                        "/api/v1/status",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.authenticationManagerResolver(authResolver)
            }
            .build()

    @Bean
    open fun tokenAuthenticationManagerResolver(
        @Qualifier("combo-provider") universalProviderManager: ProviderManager
    ): AuthenticationManagerResolver<HttpServletRequest> =
        AuthenticationManagerResolver { universalProviderManager }

    @Bean("combo-provider")
    @Primary
    open fun comboProvider(
        @Value("\${id-porten.issuer}") idPortenIssuer: String,
        @Value("\${id-porten.audience}") idPortenAudience: String,
        @Value("\${token-x.issuer}") tokenXIssuer: String,
        @Value("\${token-x.client.id}") tokenXClientId: String,
        @Value("\${azure-app.issuer}") entraIdIssuer: String,
        // Use sob.frontend.client-id until frontend exchanges Entra token into OBO token:
        //@Value("\${azure-app.client-id}") entraIdClientId: String
        @Value("\${sob.frontend.client-id}") entraIdClientId: String
    ): ProviderManager =
        ProviderManager(
            JwtAuthenticationProvider(
                jwtDecoder(idPortenIssuer, tokenValidator = TokenAudienceValidator(idPortenAudience))
            ),
            JwtAuthenticationProvider(
                jwtDecoder(tokenXIssuer, tokenValidator = TokenAudienceValidator(tokenXClientId))
            ),
            JwtAuthenticationProvider(
                jwtDecoder(entraIdIssuer, tokenValidator = TokenAudienceValidator(entraIdClientId))
            )
        )

    private companion object {

        private fun jwtDecoder(issuer: String, tokenValidator: OAuth2TokenValidator<Jwt>): JwtDecoder =
            jwtDecoder(issuer).apply {
                setJwtValidator(
                    DelegatingOAuth2TokenValidator(
                        JwtValidators.createDefaultWithIssuer(issuer),
                        tokenValidator
                    )
                )
            }

        private fun jwtDecoder(issuer: String) =
            JwtDecoders.fromIssuerLocation(issuer) as NimbusJwtDecoder
    }
}
