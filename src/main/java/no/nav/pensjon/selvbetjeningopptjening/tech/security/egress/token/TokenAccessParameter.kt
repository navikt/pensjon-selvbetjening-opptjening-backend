package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.AuthorizationGrantType

class TokenAccessParameter(private val type: AuthorizationGrantType, val value: String) {

    fun getGrantTypeName(): String = type.value

    fun getParameterName(): String = type.parameterName

    companion object {
        fun authorizationCode(code: String) = TokenAccessParameter(AuthorizationGrantType.AUTHORIZATION_CODE, code)

        fun clientCredentials(scope: String) = TokenAccessParameter(AuthorizationGrantType.CLIENT_CREDENTIALS, scope)

        fun jwtBearer(assertion: String) = TokenAccessParameter(AuthorizationGrantType.JWT_BEARER, assertion)

        fun tokenExchange(subjectToken: String) =
            TokenAccessParameter(AuthorizationGrantType.TOKEN_EXCHANGE, subjectToken)
    }
}
