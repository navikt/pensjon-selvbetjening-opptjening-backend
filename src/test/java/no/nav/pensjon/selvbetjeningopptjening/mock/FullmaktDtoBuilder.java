package no.nav.pensjon.selvbetjeningopptjening.mock;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Aktoertype;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Fagomraade;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Fullmaktnivaa;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.Fullmakttype;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.AktoerDto;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktDto;

import java.util.Calendar;

import static java.util.Collections.emptyList;

public class FullmaktDtoBuilder {

    public static final String FULLMAKTSGIVER_PID = "04925398980";
    public static final String FULLMEKTIG_PID = "30915399246";
    private static final boolean gyldig = true;
    private static final int fomMonth = 1;
    private static final int tomMonth = 12;
    private final int id;
    private final Fagomraade fagomraade = Fagomraade.PEN;
    private final Fullmaktnivaa nivaa = Fullmaktnivaa.FULLSTENDIG;
    private final Aktoertype fullmaktsgiverType = Aktoertype.PERSON;
    private final Aktoertype fullmektigType = Aktoertype.PERSON;
    private int versjon;

    private FullmaktDtoBuilder(int id) {
        this.id = id;
    }

    public static FullmaktDtoBuilder instance(int id) {
        return new FullmaktDtoBuilder(id);
    }

    public FullmaktDtoBuilder withVersjon(int value) {
        versjon = value;
        return this;
    }

    public FullmaktDto build() {
        return new FullmaktDto(
                aktoer(FULLMAKTSGIVER_PID, fullmaktsgiverType),
                aktoer(FULLMEKTIG_PID, fullmektigType),
                fagomraade.name(),
                calendar(fomMonth),
                nivaa.name(),
                Fullmakttype.SELVBET.name(),
                versjon,
                "endrer",
                calendar(Calendar.JANUARY),
                id,
                gyldig,
                "oppretter",
                calendar(Calendar.FEBRUARY),
                calendar(Calendar.MARCH),
                calendar(tomMonth));
    }

    private static AktoerDto aktoer(String pid, Aktoertype type) {
        return new AktoerDto(pid, type.name(), emptyList(), emptyList());
    }

    private static Calendar calendar(int fomMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, fomMonth, 4);
        return calendar;
    }
}
