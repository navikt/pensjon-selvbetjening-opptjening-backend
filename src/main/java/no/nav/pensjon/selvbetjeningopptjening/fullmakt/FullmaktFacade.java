package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class FullmaktFacade {

    private final FullmaktApi fullmaktService;

    public FullmaktFacade(FullmaktApi fullmaktService) {
        this.fullmaktService = fullmaktService;
    }

    public boolean mayActOnBehalfOf(String fullmaktsgiverPid, String fullmektigPid) {
        if (fullmaktsgiverPid.equals(fullmektigPid)) {
            return true; // may act "on behalf of" oneself
        }

        LocalDate today = today();
        return getValidFullmakter(fullmaktService, fullmaktsgiverPid, fullmektigPid, today).size() > 0;
    }

    protected LocalDate today() {
        return LocalDate.now();
    }

    private static List<Fullmakt> getValidFullmakter(FullmaktApi service,
                                                     String fullmaktsgiverPid,
                                                     String fullmektigPid,
                                                     LocalDate today) {
        return service.getFullmakter(fullmaktsgiverPid)
                .stream()
                .filter(fullmakt -> fullmakt.isValidFor(fullmaktsgiverPid, fullmektigPid, today, Fullmaktnivaa.FULLSTENDIG))
                .toList();
    }
}
