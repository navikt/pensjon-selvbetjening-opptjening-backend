package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedselsdato;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Navn;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadata;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadataEndring;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2;
import no.nav.pensjon.selvbetjeningopptjening.person.Person;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.emptyList;

public class PersonMapper {

    public static Person fromDto(PdlResponse response, Pid pid) {
        List<Foedselsdato2> birthDates = PdlFoedselsdatoMapper.INSTANCE.fromDtos(getBirths(response));
        Foedselsdato2 birthDate = birthDates.isEmpty() ? null : birthDates.getFirst();
        Navn navn = getLatestRegisteredNavn(getAllNavn(response));
        if (navn != null) {
            return new Person(pid, navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn(), birthDate);
        } else {
            return new Person(pid, birthDate);
        }
    }

    private static List<Navn> getAllNavn(PdlResponse response) {
        return response == null ? emptyList() : getAllNavn(response.getData());
    }

    private static List<Navn> getAllNavn(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getNavn();
    }

    private static Navn getLatestRegisteredNavn(List<Navn> navn) {
        Comparator<Navn> navnComparator = (navn1, navn2) -> {
            LocalDate navn1Endringstidspunkt = getEndringstidspunkt(navn1);
            LocalDate navn2Endringstidspunkt = getEndringstidspunkt(navn2);
            if (navn2Endringstidspunkt == null || navn1Endringstidspunkt != null && navn1Endringstidspunkt.isAfter(navn2Endringstidspunkt)) {
                return 1;
            } else if (navn1Endringstidspunkt == null || navn1Endringstidspunkt.isBefore(navn2Endringstidspunkt)) {
                return -1;
            }
            return 0;
        };

        return navn != null ? navn.stream().max(navnComparator).orElse(null) : null;
    }

    private static LocalDate getEndringstidspunkt(Navn navn) {
        PdlMetadata metadata = navn.getMetadata();
        if (metadata != null) {
            return metadata.getMaster().equalsIgnoreCase("FREG") ?
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

    private static List<Foedselsdato> getBirths(PdlResponse response) {
        return response == null
                ? emptyList()
                : getBirths(response.getData());
    }

    private static List<Foedselsdato> getBirths(PdlData data) {
        return data == null || data.getHentPerson() == null
                ? emptyList()
                : data.getHentPerson().getFoedselsdato();
    }
}
