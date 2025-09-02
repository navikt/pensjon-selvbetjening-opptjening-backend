package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config

/**
 * An egress service is an external service used by backend (backend makes an outwards-directed call).
 * In order to call the service, an access token with the appropriate audience needs to be obtained.
 * The audience identifies the immediate server that needs to be called (which may be a proxy server instead of the
 * actual external service).
 */
data class EgressServicesByAudience(val entries: Map<String, EgressService>)
