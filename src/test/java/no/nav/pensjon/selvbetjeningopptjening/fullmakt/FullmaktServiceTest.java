package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktsforholdDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FullmaktServiceTest {
    @Mock
    private FullmaktClient fullmaktClient;
    private static final LocalDateTime TIME_INSIDE_WORKING_HOURS = LocalDateTime.of(2023,2,6,12,0).with(DayOfWeek.MONDAY);
    private static final LocalDateTime TIME_OUTSIDE_WORKING_HOURS = LocalDateTime.of(2023,2,6,20,0).with(DayOfWeek.MONDAY);

    @Test
    void should_return_false_when_harFullmaktsforhold_false(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(false, null));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_false_when_harFullmaktsforhold_false_and_personlig_fullmakt(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(false, true));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_OUTSIDE_WORKING_HOURS).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_false_when_harFullmaktsforhold_false_and_not_personlig_fullmakt(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(false, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient,TIME_OUTSIDE_WORKING_HOURS).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_true_when_harFullmaktsforhold_true_and_personlig_fullmakt(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, true));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient,TIME_OUTSIDE_WORKING_HOURS).harFullmaktsforhold("","");
        assertTrue(result);
    }

    @Test
    void should_return_true_when_harFullmaktsforhold_true_and_upersonlig_fullmakt_within_working_hours(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS).harFullmaktsforhold("","");
        assertTrue(result);
    }

    @Test
    void should_return_false_when_upersonlig_fullmakt_and_after_21(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS.withHour(22)).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_false_when_upersonlig_fullmakt_and_before_7(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS.withHour(6)).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_true_when_upersonlig_fullmakt_and_sunday_within_working_hours(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient,TIME_INSIDE_WORKING_HOURS.with(DayOfWeek.SUNDAY)).harFullmaktsforhold("","");
        assertTrue(result);
    }

    @Test
    void should_return_false_when_upersonlig_fullmakt_and_after_18_on_sunday(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS.withHour(19).with(DayOfWeek.SUNDAY)).harFullmaktsforhold("","");
        assertFalse(result);
    }

    @Test
    void should_return_false_when_upersonlig_fullmakt_and_before_10_on_sunday(){
        when(fullmaktClient.harFullmaktsforhold(any(), any())).thenReturn(new FullmaktsforholdDto(true, false));
        boolean result = new FullmaktServiceWithSpecifiedToday(fullmaktClient, TIME_INSIDE_WORKING_HOURS.withHour(9).with(DayOfWeek.SUNDAY)).harFullmaktsforhold("","");
        assertFalse(result);
    }

    private static class FullmaktServiceWithSpecifiedToday extends FullmaktService{
        private final LocalDateTime now;
        public FullmaktServiceWithSpecifiedToday(FullmaktClient client, LocalDateTime now) {
            super(client);
            this.now = now;
        }

        @Override
        protected LocalDateTime now(){
            return now;
        }
    }

}