package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Navn;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadata;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadataEndring;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping.BirthDateMapper.fromDtos;

public class PersonMapper {

    public static Person fromDto(PdlResponse response, Pid pid) {
        List<BirthDate> birthDates = fromDtos(getBirths(response));
        BirthDate birthDate = birthDates.isEmpty() ? null : birthDates.get(0);
        Navn navn = getLatestRegisteredNavn(getNames(response));
        if (navn != null) {
            return new Person(pid, navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn(), birthDate);
        } else {
            return new Person(pid, null, null, null, birthDate);
        }
    }

    private static List<Navn> getNames(PdlResponse response) {
        return response == null ? emptyList() : getNames(response.getData());
    }

    private static List<Navn> getNames(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getNavn();
    }

    private static Navn getLatestRegisteredNavn(List<Navn> navn) {
        Comparator<Navn> navnComparator = (navn1, navn2) -> {
            LocalDate navn1Endringstidpunkt = getEndringstidspunkt(navn1);
            LocalDate navn2Endringstidpunkt = getEndringstidspunkt(navn2);
            if (navn2Endringstidpunkt == null || navn1Endringstidpunkt != null && navn1Endringstidpunkt.isAfter(navn2Endringstidpunkt)) {
                return 1;
            } else if (navn1Endringstidpunkt == null || navn1Endringstidpunkt.isBefore(navn2Endringstidpunkt)) {
                return -1;
            }
            return 0;
        };

        return navn.stream().max(navnComparator).orElse(null);
    }

    private static LocalDate getEndringstidspunkt(Navn navn) {
        PdlMetadata metadata = navn.getMetadata();
        if (metadata != null) {
            return metadata.getMaster().toUpperCase().equals("FREG") ?
                    navn.getFolkeregistermetadata().getAjourholdstidspunkt()
                    :
                    getLatestEndring(metadata.getEndringer()).getRegistrert();
        }
        return null;
    }

    private static PdlMetadataEndring getLatestEndring(List<PdlMetadataEndring> endringer) {
        Comparator<PdlMetadataEndring> endringComparator = (endring1, endring2) -> {
            if (endring1.getRegistrert().isAfter(endring2.getRegistrert())) {
                return 1;
            } else if (endring1.getRegistrert().isBefore(endring2.getRegistrert())) {
                return -1;
            }
            return 0;
        };
        return endringer == null ? null : endringer.stream().max(endringComparator).orElse(null);
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
