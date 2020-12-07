package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BirthDateMapperTest {

    @Test
    void fromDtos_shall_map_to_domain_objects() {
        Foedsel birth1 = birth(1981, 2, 3);
        Foedsel birth2 = birth(1982, 12, 31);
        birth2.setFoedselsaar(1983);
        var birth3 = new Foedsel();
        birth3.setFoedselsaar(1984);

        List<BirthDate> birthDates = BirthDateMapper.fromDtos(List.of(birth1, birth2, birth3));

        assertEquals(3, birthDates.size());
        assertBirthDate(birthDates.get(0), LocalDate.of(1981, 2, 3), false);
        assertBirthDate(birthDates.get(1), LocalDate.of(1982, 12, 31), false);
        assertBirthDate(birthDates.get(2), LocalDate.of(1984, 1, 1), true);
    }

    @Test
    void fromDtos_shall_ignore_null_values() {
        List<Foedsel> births = new ArrayList<>();
        births.add(null);
        births.add(birth(1981, 2, 3));

        List<BirthDate> birthDates = BirthDateMapper.fromDtos(births);

        assertEquals(1, birthDates.size());
        assertBirthDate(birthDates.get(0), LocalDate.of(1981, 2, 3), false);
    }

    @Test
    void fromDtos_shall_map_nullList_to_emptyList() {
        List<BirthDate> birthDates = BirthDateMapper.fromDtos(null);
        assertEquals(0, birthDates.size());
    }

    private static Foedsel birth(int year, int month, int day) {
        var birth = new Foedsel();
        birth.setFoedselsdato(LocalDate.of(year, month, day));
        return birth;
    }

    private static void assertBirthDate(BirthDate actual, LocalDate expectedDate, boolean expectedBasedOnYearOnly) {
        assertEquals(expectedDate, actual.getValue());
        assertEquals(expectedBasedOnYearOnly, actual.isBasedOnYearOnly());
    }
}
