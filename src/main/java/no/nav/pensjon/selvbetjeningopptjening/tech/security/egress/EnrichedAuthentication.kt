package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.selvbetjeningopptjening.tech.representasjon.RepresentertRolle
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * Authentication data is initially obtained by Spring Security.
 * This class augments that data by adding a mechanism for getting egress tokens
 * (used by backend for accessing other services).
 * It also keeps the person ID (if available).
 */
class EnrichedAuthentication(
    private val initialAuth: Authentication?,
    val authType: AuthType,
    private val egressTokenSuppliersByService: EgressTokenSuppliersByService,
    private val target: RepresentasjonTarget
) : Authentication {

    fun getEgressAccessToken(service: EgressService, ingressToken: String?): RawJwt =
        egressTokenSuppliersByService.value[service]?.apply(ingressToken) ?: RawJwt("")

    fun targetPid(): Pid? = target.pid

    fun fullmektigPid(): Pid? =
        if (target.rolle == RepresentertRolle.FULLMAKT_GIVER)
            SecurityContextPidExtractor.pidFromSecurityContext()?.let(::Pid)
        else
            null

    override fun getName(): String = initialAuth?.name ?: ""

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = initialAuth?.authorities ?: mutableSetOf()

    override fun getCredentials(): Any = initialAuth?.credentials ?: ""

    override fun getDetails(): Any = initialAuth?.details ?: ""

    override fun getPrincipal(): Any = initialAuth?.principal ?: ""

    override fun isAuthenticated(): Boolean = initialAuth?.isAuthenticated ?: false

    override fun setAuthenticated(isAuthenticated: Boolean) {
        initialAuth?.let { it.isAuthenticated = isAuthenticated }
    }
}

fun Authentication.enriched(): EnrichedAuthentication = this as EnrichedAuthentication
