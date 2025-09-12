package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object SecurityContextClaimExtractor {

    fun claim(key: String): Any? = jwt()?.claims?.get(key)

    private fun jwt(): Jwt? = SecurityContextHolder.getContext().authentication?.credentials as? Jwt
}
