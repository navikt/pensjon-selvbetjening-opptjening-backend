package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object EgressAccess {

    fun token(service: EgressService): RawJwt {
        val authentication = SecurityContextHolder.getContext()?.authentication

        return authentication?.enriched()?.getEgressAccessToken(
            service,
            ingressToken = (authentication.credentials as? Jwt)?.tokenValue
        ) ?: throw AuthenticationCredentialsNotFoundException("failed to get egress access token")
    }
}
