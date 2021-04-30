package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Navn;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;

import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping.BirthDateMapper.fromDtos;

public class PersonMapper {

    public static Person fromDto(PdlResponse response, Pid pid) {
        List<BirthDate> birthDates = fromDtos(getBirths(response));
        BirthDate birthDate = birthDates.isEmpty() ? null : birthDates.get(0);
        List<Navn> names = getNavn(response);
        if (names != null && !names.isEmpty()) {
            return new Person(pid, names.get(0).getFornavn(), names.get(0).getMellomnavn(), names.get(0).getEtternavn(), birthDate);
        } else {
            return new Person(pid, null, null, null, birthDate);
        }
    }

    private static List<Navn> getNavn(PdlResponse response) {
        return response == null ? emptyList() : getNavn(response.getData());
    }

    private static List<Navn> getNavn(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getNavn();
    }

    private static List<Foedsel> getBirths(PdlResponse response) {
        return response == null
                ? emptyList()
                : getBirths(response.getData());
    }

    private static List<Foedsel> getBirths(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getFoedsel();
    }
}
