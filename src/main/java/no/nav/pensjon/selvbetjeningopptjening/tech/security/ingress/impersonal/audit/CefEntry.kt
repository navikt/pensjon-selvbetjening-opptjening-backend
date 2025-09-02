package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit

import org.slf4j.event.Level
import kotlin.collections.joinToString

/**
 * CEF = ArcSight Common Event Format
 * Note that no character escaping is performed here, since the logged info does not require this.
 */
class CefEntry(
    private val timestamp: Long,
    private val level: Level,
    private val deviceEventClassId: String,
    private val name: String,
    private val message: String,
    private val sourceUserId: String,
    private val destinationUserId: String
) {
    fun format(): String =
        listOf(
            "$MESSAGE_TYPE:$CEF_VERSION",
            DEVICE_VENDOR,
            DEVICE_PRODUCT,
            DEVICE_VERSION,
            deviceEventClassId,
            name,
            severity(),
            extension()
        ).joinToString(SEPARATOR)

    private fun severity(): String =
        if (level == Level.INFO) "INFO" else "WARN"

    /**
     * Note that it is recommended by #tech-logg_analyse_og_datainnsikt
     * to use 'msg' rather than 'act', since the latter has max. length 63
     */
    private fun extension(): String =
        "end=$timestamp suid=$sourceUserId duid=$destinationUserId msg=$message flexString1Label=Decision flexString1=Permit"

    companion object {
        private const val CEF_VERSION = 0
        private const val MESSAGE_TYPE = "CEF"
        private const val SEPARATOR = "|"
        private const val DEVICE_VENDOR = "pensjon"
        private const val DEVICE_PRODUCT = "pensjonskalkulator-backend"
        private const val DEVICE_VERSION = "1.0"
    }
}
