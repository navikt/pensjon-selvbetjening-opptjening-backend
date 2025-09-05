package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group

import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Service
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.toString

@Service
class SecurityContextGroupService : GroupService {

    override fun groups(): List<String> = groupsFromSecurityContext()?.map { it.toString() }.orEmpty()

    private companion object {
        private const val CLAIM_KEY = "groups"

        private fun groupsFromSecurityContext() = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? List<*>
    }
}
