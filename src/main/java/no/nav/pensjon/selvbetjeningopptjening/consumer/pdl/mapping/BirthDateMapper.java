package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedselsdato;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class BirthDateMapper {

    public static List<BirthDate> fromDtos(List<Foedselsdato> list) {
        return list == null ? emptyList()
                :
                list.stream()
                        .map(BirthDateMapper::fromDto)
                        .filter(Objects::nonNull)
                        .collect(toList());
    }

    private static BirthDate fromDto(Foedselsdato birth) {
        return birth == null ? null : map(birth);
    }

    private static BirthDate map(Foedselsdato birth) {
        return birth.getFoedselsdato() == null
                ? fromYear(birth.getFoedselsaar())
                : new BirthDate(birth.getFoedselsdato());
    }

    private static BirthDate fromYear(Integer year) {
        return year == null ? null : new BirthDate(year);
    }
}
