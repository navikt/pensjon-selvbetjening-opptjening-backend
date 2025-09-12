package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Credentials used when requesting a token exchange.
 */
@Component
class TokenExchangeCredentials(
    @param:Value("\${token-x.client.id}") val clientId: String,
    @param:Value("\${token-x.private.jwk}") val jwk: String
)
