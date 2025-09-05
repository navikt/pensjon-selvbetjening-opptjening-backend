package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import java.util.function.Function

/**
 * Egress access tokens are used for authentication when calling external services.
 * This class holds a collection of access token suppliers.
 * Each supplier is mapped to a service (the service for which the token is to be used).
 * By holding suppliers instead of actual tokens, the tokens are obtained "lazily" (when needed).
 */
data class EgressTokenSuppliersByService(val value: Map<EgressService, Function<String?, RawJwt>>)
