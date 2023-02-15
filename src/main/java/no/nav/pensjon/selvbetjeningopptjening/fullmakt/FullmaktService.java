package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktsforholdDto;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class FullmaktService implements FullmaktApi {

    private final FullmaktClient client;
    private final static int WORKING_HOURS_START_HOUR = 7;
    private final static int WORKING_HOURS_END_HOUR = 21;

    public FullmaktService(FullmaktClient client) {
        this.client = client;
    }

    @Override
    public boolean harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid) {
        FullmaktsforholdDto response = client.harFullmaktsforhold(fullmaktsgiverPid, fullmektigPid);
        if (response == null || response.getHarFullmaktsforhold() == null || !response.getHarFullmaktsforhold()) {
            return false;
        }

        if (response.getErPersonligFullmakt()) {
            return response.getHarFullmaktsforhold();
        } else {
            return isValidIkkePersonligFullmaktWithinWorkingHours(response);
        }
    }

    private boolean isValidIkkePersonligFullmaktWithinWorkingHours(FullmaktsforholdDto fullmaktsforhold) {
        LocalDateTime now = today();
        return  fullmaktsforhold.getHarFullmaktsforhold()
                && !fullmaktsforhold.getErPersonligFullmakt()
                && !now.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                && now.getHour() > WORKING_HOURS_START_HOUR
                && now.getHour() < WORKING_HOURS_END_HOUR;
    }

    protected LocalDateTime today() {
        return LocalDateTime.now();
    }
}
