package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.audit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.event.Level

class CefEntryTest {
    @Test
    fun `format returns info formatted according to CEF spec`() {
        val cefEntry = CefEntry(
            123456789L,
            Level.INFO,
            "audit:edit",
            "Hendelse",
            "Noe blir gjort",
            "X123456",
            "01023456789"
        )
        val formattedInfo = cefEntry.format()
        assertEquals(
            "CEF:0|pensjon|pensjonskalkulator-backend|1.0|audit:edit|Hendelse|INFO" +
                    "|end=123456789 suid=X123456 duid=01023456789 msg=Noe blir gjort" +
                    " flexString1Label=Decision flexString1=Permit", formattedInfo
        )
    }
}
