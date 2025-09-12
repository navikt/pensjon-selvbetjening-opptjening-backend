package no.nav.pensjon.selvbetjeningopptjening.person.client.pdl.acl

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.person.Sivilstand
import org.springframework.util.StringUtils.hasLength
import kotlin.also
import kotlin.collections.singleOrNull
import kotlin.collections.toTypedArray
import kotlin.text.equals

/**
 * For externalValue definitions see: https://pdldocs-navno.msappproxy.net/ekstern/index.html#_sivilstand
 */
enum class PdlSivilstandType(val externalValue: String, val internalValue: Sivilstand) {

    UNKNOWN("?", Sivilstand.UNKNOWN),
    UOPPGITT("UOPPGITT", Sivilstand.UOPPGITT),
    UGIFT("UGIFT", Sivilstand.UGIFT),
    GIFT("GIFT", Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN("ENKE_ELLER_ENKEMANN", Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT("SKILT", Sivilstand.SKILT),
    SEPARERT("SEPARERT", Sivilstand.SEPARERT),
    REGISTRERT_PARTNER("REGISTRERT_PARTNER", Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER("SEPARERT_PARTNER", Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER("SKILT_PARTNER", Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER("GJENLEVENDE_PARTNER", Sivilstand.GJENLEVENDE_PARTNER);

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PDL sivilstand '$externalValue'" } }
            else
                UOPPGITT
    }
}
