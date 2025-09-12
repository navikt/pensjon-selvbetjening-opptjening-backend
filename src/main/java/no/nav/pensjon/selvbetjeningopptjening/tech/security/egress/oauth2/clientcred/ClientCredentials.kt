package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.clientcred

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ClientCredentials(
    @param:Value("\${azure-app.client-id}") val clientId: String,
    @param:Value("\${azure-app.client-secret}") val clientSecret: String
)
