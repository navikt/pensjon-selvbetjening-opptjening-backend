package no.nav.pensjon.selvbetjeningopptjening.audit;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CefEntryTest {

    @Test
    void format_returns_info_formattedAccordingTo_cefSpec() {
        var cefEntry = new CefEntry(
                123456789L,
                Level.INFO,
                "audit:access",
                "Hendelse",
                "Noe blir gjort",
                "X123456",
                "01020345678");

        String formattedInfo = cefEntry.format();

        assertEquals("CEF:0|pensjon|pensjon-selvbetjening-opptjening-backend|1.0|audit:access|Hendelse|INFO" +
                "|end=123456789 suid=X123456 duid=01020345678 msg=Noe blir gjort" +
                " flexString1Label=Decision flexString1=Permit", formattedInfo);
    }
}
