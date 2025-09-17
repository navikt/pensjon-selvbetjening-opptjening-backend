package no.nav.pensjon.selvbetjeningopptjening.mock

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

/**
 * Mocks JWT-based authentication
 */
class MockAuthentication(private val claimKey: String, private val claimValue: Any) : Authentication {

    override fun getCredentials(): Any =
        Jwt(
            "token1",
            Instant.MIN,
            Instant.MAX,
            mapOf("k" to "v"),
            mapOf(claimKey to claimValue)
        )

    override fun getName(): String = ""
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
    override fun getDetails(): Any = ""
    override fun getPrincipal(): Any = ""
    override fun isAuthenticated(): Boolean = false
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}
