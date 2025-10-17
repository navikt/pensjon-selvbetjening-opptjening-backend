package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config

/**
 * Specifies the services that are accessed by opptjening-backend, and their characteristics.
 */
enum class EgressService(
    val description: String,
    val shortName: String,
    val purpose: String,
    val supportsTokenExchange: Boolean
) {
    OAUTH2_TOKEN("OAuth2 token", "OA2", "OAuth2 access token", supportsTokenExchange = false),
    PENSJON_REPRESENTASJON(
        description = "Pensjon-representasjon",
        shortName = "Rep",
        purpose = "Representasjonsforhold (fullmakt m.m.)",
        supportsTokenExchange = true
    ),

    // As of 2025-09-01, PEN responds with 403 when TokenX token is used
    PENSJONSFAGLIG_KJERNE("Pensjonsfaglig kjerne", "PEN", "Pensjonsdata", supportsTokenExchange = false),

    PENSJONSOPPTJENING("Pensjonsopptjening", "POPP", "Pensjonsopptjeningsdata", supportsTokenExchange = true),
    PERSONDATA("Persondataløsningen", "PDL", "Persondata", supportsTokenExchange = true),
    SKJERMEDE_PERSONER("Skjermede personer", "SP", "Skjerming", supportsTokenExchange = false),
    PID_ENCRYPTION("PID Encryption Service", "PES", "PID encryption", supportsTokenExchange = false);
}
