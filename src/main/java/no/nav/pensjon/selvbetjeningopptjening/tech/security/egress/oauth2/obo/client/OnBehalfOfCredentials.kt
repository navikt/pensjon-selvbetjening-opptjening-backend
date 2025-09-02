package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Credentials used when requesting an 'on-behalf-of' token.
 */
@Component
class OnBehalfOfCredentials(
    @param:Value("\${azure-app.client-id}") val clientId: String,
    @param:Value("\${azure-app.client-secret}") val clientSecret: String
)
