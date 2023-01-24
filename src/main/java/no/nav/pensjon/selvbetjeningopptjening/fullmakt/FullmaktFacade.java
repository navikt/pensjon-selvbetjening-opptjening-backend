package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.tjenestepensjon.TjenestepensjonClient;
import no.nav.pensjon.selvbetjeningopptjening.tjenestepensjon.Tjenestepensjonsforhold;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FullmaktFacade {

    private final FullmaktApi fullmaktService;
    private final TjenestepensjonClient tjenestepensjonClient;

    public FullmaktFacade(FullmaktApi fullmaktService, TjenestepensjonClient tjenestepensjonClient) {
        this.fullmaktService = fullmaktService;
        this.tjenestepensjonClient = tjenestepensjonClient;
    }

    public boolean mayActOnBehalfOf(String fullmaktsgiverPid, String fullmektigPid) {
        if (fullmaktsgiverPid.equals(fullmektigPid)) {
            return true; // may act "on behalf of" oneself
        }

        LocalDate today = today();
        return getValidPersonligFullmakter(fullmaktService, fullmaktsgiverPid, fullmektigPid, today).size() > 0
                || getValidSamhandlerFullmakter(fullmaktService, tjenestepensjonClient, fullmaktsgiverPid, fullmektigPid, today).size() > 0;
    }

    protected LocalDate today() {
        return LocalDate.now();
    }

    private static List<Fullmakt> getValidPersonligFullmakter(FullmaktApi service,
                                                              String fullmaktsgiverPid,
                                                              String fullmektigPid,
                                                              LocalDate today) {
        return service.getFullmakter(fullmaktsgiverPid)
                .stream()
                .filter(fullmakt -> fullmakt.isValidFor(fullmaktsgiverPid, fullmektigPid, today))
                .toList();
    }

    private static List<Fullmakt> getValidSamhandlerFullmakter(FullmaktApi service,
                                                               TjenestepensjonClient tjenestepensjonClient,
                                                               String fullmaktsgiverPid,
                                                               String fullmektigSamhandlerPid,
                                                               LocalDate today) {
        List<Tjenestepensjonsforhold> ordninger = tjenestepensjonClient.getAllTjenestepensjonsforhold(fullmaktsgiverPid);

        return getAllFullmakterForOrdninger(service, ordninger)
                .filter(fullmakt -> fullmakt.isValidForSamhandler(fullmektigSamhandlerPid, today))
                .toList();
    }

    private static Stream<Fullmakt> getAllFullmakterForOrdninger(FullmaktApi service, List<Tjenestepensjonsforhold> ordninger) {
        return ordninger.stream().map(ordning -> service.getFullmakter(ordning.getOrdning())).flatMap(List::stream);
    }
}
