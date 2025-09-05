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
        @Value("\${id-porten.issuer}") issuer: String,
        @Value("\${id-porten.audience}") audience: String,
        @Value("\${token-x.issuer}") issuer2: String,
        @Value("\${token-x.client.id}") clientId: String,
        @Value("\${azure-app.issuer}") issuer3: String,
        @Value("\${azure-app.client-id}") clientId2: String
    ): ProviderManager =
        ProviderManager(
            JwtAuthenticationProvider(
                jwtDecoder(issuer, tokenValidator = TokenAudienceValidator(audience))
            ),
            JwtAuthenticationProvider(
                jwtDecoder(issuer2, tokenValidator = TokenAudienceValidator(clientId))
            ),
            JwtAuthenticationProvider(
                jwtDecoder(issuer3, tokenValidator = TokenAudienceValidator(clientId2))
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
