package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2

import com.fasterxml.jackson.annotation.JsonProperty

class OAuth2TokenDto {

    @JsonProperty("token_type")
    private var tokenType: String? = null

    @JsonProperty("scope")
    private var scope: String? = null

    @JsonProperty("expires_in")
    private var expiresIn: Int? = null

    // ext_expires_in must be declared to avoid "unknown property" deserialization error
    @JsonProperty("ext_expires_in")
    private var extExpiresIn: Int? = null

    @JsonProperty("access_token")
    private var accessToken: String? = null

    @JsonProperty("id_token")
    private var idToken: String? = null

    @JsonProperty("refresh_token")
    private var refreshToken: String? = null

    @JsonProperty("token_type")
    fun getTokenType(): String? {
        return tokenType
    }

    @JsonProperty("token_type")
    fun setTokenType(tokenType: String?) {
        this.tokenType = tokenType
    }

    @JsonProperty("scope")
    fun getScope(): String? {
        return scope
    }

    @JsonProperty("scope")
    fun setScope(scope: String?) {
        this.scope = scope
    }

    @JsonProperty("expires_in")
    fun getExpiresIn(): Int? {
        return expiresIn
    }

    @JsonProperty("expires_in")
    fun setExpiresIn(expiresIn: Int?) {
        this.expiresIn = expiresIn
    }

    @JsonProperty("access_token")
    fun getAccessToken(): String? {
        return accessToken
    }

    @JsonProperty("access_token")
    fun setAccessToken(accessToken: String?) {
        this.accessToken = accessToken
    }

    @JsonProperty("id_token")
    fun getIdToken(): String? {
        return idToken
    }

    @JsonProperty("id_token")
    fun setIdToken(idToken: String?) {
        this.idToken = idToken
    }

    @JsonProperty("refresh_token")
    fun getRefreshToken(): String? {
        return refreshToken
    }

    @JsonProperty("refresh_token")
    fun setRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }
}
