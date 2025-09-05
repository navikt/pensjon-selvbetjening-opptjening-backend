package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2

enum class AuthorizationGrantType(val value: String, val parameterName: String) {

    AUTHORIZATION_CODE("authorization_code", OAuth2ParameterNames.CODE),
    CLIENT_CREDENTIALS("client_credentials", OAuth2ParameterNames.SCOPE),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer", OAuth2ParameterNames.ASSERTION),
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange", OAuth2ParameterNames.SUBJECT_TOKEN)
}
