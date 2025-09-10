package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress

import mu.KotlinLogging
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.collections.joinToString
import kotlin.collections.orEmpty
import kotlin.let

class TokenAudienceValidator(val audience: String) : OAuth2TokenValidator<Jwt> {

    private val log = KotlinLogging.logger {}

    override fun validate(token: Jwt): OAuth2TokenValidatorResult =
        validate(token.audience.orEmpty())

    private fun validate(audiences: List<String>): OAuth2TokenValidatorResult =
        if (audiences.contains(audience))
            success()
        else
            "Invalid audience claim: ${audiences.joinToString()}".let {
                log.warn { it }
                failure(OAuth2Error(it))
            }
}
