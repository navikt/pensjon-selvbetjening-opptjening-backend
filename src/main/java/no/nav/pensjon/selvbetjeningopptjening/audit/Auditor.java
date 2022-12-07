package no.nav.pensjon.selvbetjeningopptjening.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Sends info to the auditing system when an internal user (NAV employee) or a fullmektig
 * performs some task on behalf of a regular (external) user.
 * The info is formatted according to ArcSight CEF (Common Event Format).
 */
@Component
public class Auditor {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT_LOGGER");

    public void auditInternalUser(String userId, String onBehalfOfPid) {
        audit.info(cefEntry(userId, "NAV-ansatt", onBehalfOfPid).format());
    }

    public void auditFullmakt(String fullmektigPid, String onBehalfOfPid) {
        audit.info(cefEntry(fullmektigPid, "Fullmektig", onBehalfOfPid).format());
    }

    private static CefEntry cefEntry(String userId, String userType, String onBehalfOfPid) {
        return new CefEntry(
                ZonedDateTime.now().toInstant().toEpochMilli(),
                Level.INFO,
                "audit:read",
                "Datahenting paa vegne av",
                userType + " henter pensjonsopptjening for innbygger",
                userId,
                onBehalfOfPid);
    }
}
