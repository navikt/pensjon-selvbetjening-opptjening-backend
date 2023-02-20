package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktsforholdDto;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Component
public class FullmaktService implements FullmaktApi {

    private final FullmaktClient client;
    private final static int WORKING_HOURS_START_HOUR = 7;
    private final static int WORKING_HOURS_END_HOUR = 21;
    private final static int WORKING_HOURS_SUNDAY_START_HOUR = 10;
    private final static int WORKING_HOURS_SUNDAY_END_HOUR = 18;

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
        return fullmaktsforhold.getHarFullmaktsforhold()
                && !fullmaktsforhold.getErPersonligFullmakt()
                && (isRegularWorkingHours() || isWorkingHoursSunday());
    }

    private boolean isRegularWorkingHours() {
        LocalDateTime now = now();
        return !now.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                && now.getHour() >= WORKING_HOURS_START_HOUR
                && now.getHour() < WORKING_HOURS_END_HOUR;
    }

    private boolean isWorkingHoursSunday() {
        LocalDateTime now = now();
        return now.getDayOfWeek().equals(DayOfWeek.SUNDAY)
                && now.getHour() >= WORKING_HOURS_SUNDAY_START_HOUR
                && now.getHour() < WORKING_HOURS_SUNDAY_END_HOUR;
    }

    protected LocalDateTime now() {
        return LocalDateTime.now();
    }
}
