package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.OAuth2ParameterNames.REQUESTED_TOKEN_USE
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.TokenAccessParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class OAuth2ParameterBuilder {

    private lateinit var accessParameter: TokenAccessParameter
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var scope: String
    private lateinit var tokenAudience: String
    private lateinit var tokenRequestAudience: String
    private lateinit var jwk: RSAKey

    fun tokenAccessParameter(value: TokenAccessParameter) = this.also { accessParameter = value }

    fun clientId(value: String) = this.also { clientId = value }

    fun clientSecret(value: String) = this.also { clientSecret = value }

    fun scope(value: String) = this.also { scope = value }

    fun tokenAudience(value: String) = this.also { tokenAudience = value }

    fun tokenRequestAudience(value: String) = this.also { tokenRequestAudience = value }

    fun jwk(value: RSAKey) = this.also { jwk = value }

    fun buildClientCredentialsTokenRequestMap(): MultiValueMap<String, String> {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add(GRANT_TYPE, accessParameter.getGrantTypeName())
        map.add(accessParameter.getParameterName(), accessParameter.value)
        map.add(CLIENT_ID, clientId)
        map.add(CLIENT_SECRET, clientSecret)
        return map
    }

    /**
     * https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-on-behalf-of-flow
     */
    fun onBehalfOfTokenRequestMap(): MultiValueMap<String, String> {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add(GRANT_TYPE, accessParameter.getGrantTypeName())
        map.add(accessParameter.getParameterName(), accessParameter.value)
        map.add(SCOPE, scope)
        map.add(CLIENT_ID, clientId)
        map.add(CLIENT_SECRET, clientSecret)
        map.add(REQUESTED_TOKEN_USE, TOKEN_USE_ON_BEHALF_OF)
        return map
    }

    fun tokenExchangeRequestMap(): MultiValueMap<String, String> {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add(GRANT_TYPE, accessParameter.getGrantTypeName())
        map.add(accessParameter.getParameterName(), accessParameter.value)
        map.add(CLIENT_ASSERTION_TYPE, "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
        map.add(CLIENT_ASSERTION, createAssertion())
        map.add(AUDIENCE, tokenAudience)
        map.add(SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:jwt")
        map.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        map.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        return map
    }

    private fun createAssertion(): String =
        SignedJWT(header(), getClaims()).apply {
            sign(RSASSASigner(jwk))
        }.serialize()

    private fun header() =
        JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(jwk.getKeyID())
            .build()

    private fun getClaims() =
        JWTClaimsSet.Builder()
            .issuer(clientId)
            .audience(tokenRequestAudience)
            .expirationTime(expirationTime())
            .jwtID(UUID.randomUUID().toString())
            .issueTime(Date())
            .notBeforeTime(Date())
            .subject(clientId)
            .claim(SCOPE, OidcScopes.OPENID)
            .build()

    private companion object {
        private const val EXPIRATION_TIME_MINUTES_IN_THE_FUTURE = 2
        private const val TOKEN_USE_ON_BEHALF_OF = "on_behalf_of"

        private fun expirationTime(): Date? =
            fromLocalDateTime(
                LocalDateTime.now()
                    .plus(Duration.ofMinutes(EXPIRATION_TIME_MINUTES_IN_THE_FUTURE.toLong()))
            )

        private fun fromLocalDateTime(dateTime: LocalDateTime) =
            Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())
    }
}

