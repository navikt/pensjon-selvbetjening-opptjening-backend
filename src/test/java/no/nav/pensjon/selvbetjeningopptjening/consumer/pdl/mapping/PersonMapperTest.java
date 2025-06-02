package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedselsdato;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.HentPersonResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Navn;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlFolkeregisterMetadata;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadata;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlMetadataEndring;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class PersonMapperTest {
    @Test
    void should_return_person_with_navn_and_fodselsdato_when_all_data_present() {
        Navn navn = new Navn();
        navn.setFornavn("fornavn");
        navn.setEtternavn("etternavn");
        navn.setMellomnavn("mellomnavn");
        PdlMetadata metadata = new PdlMetadata();
        metadata.setMaster("FREG");

        Foedselsdato foedselsdato = new Foedselsdato();
        foedselsdato.setFoedselsdato(LocalDate.of(1960, 3, 4));

        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(List.of(foedselsdato), List.of(navn));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertEquals(navn.getFornavn(), person.getFornavn());
        assertEquals(navn.getEtternavn(), person.getEtternavn());
        assertEquals(navn.getMellomnavn(), person.getMellomnavn());
        assertEquals(foedselsdato.getFoedselsdato(), person.getFodselsdato());
    }

    @Test
    void should_return_fodselsdato_from_pid_when_no_fodselsdato_returned_from_PDL() {
        LocalDate expectedFodselsdato = LocalDate.of(1980, 4, 5);
        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(Collections.emptyList(), List.of(new Navn()));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePid(expectedFodselsdato));

        assertEquals(expectedFodselsdato, person.getFodselsdato());
    }

    @Test
    void should_handle_that_navn_is_null() {
        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(List.of(new Foedselsdato()), Collections.emptyList());

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertNull(person.getFornavn());
        assertNull(person.getEtternavn());
        assertNull(person.getMellomnavn());
        assertNotNull(person.getPid());
        assertNotNull(person.getFodselsdato());
    }

    @Test
    void should_pick_FREG_navn_when_only_this_navn_occurs() {
        String expectedNavnToBePicked = "navn";
        Navn navn = new Navn();
        navn.setFornavn(expectedNavnToBePicked);
        PdlMetadata metadata = new PdlMetadata();
        metadata.setMaster("FREG");

        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(null, List.of(navn));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertEquals(expectedNavnToBePicked, person.getFornavn());
    }

    @Test
    void should_pick_non_FREG_navn_when_only_this_navn_occurs() {
        String expectedNavnToBePicked = "navn";
        Navn navn = new Navn();
        navn.setFornavn(expectedNavnToBePicked);
        PdlMetadata metadata = new PdlMetadata();
        metadata.setMaster("NAV");

        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(null, List.of(navn));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertEquals(expectedNavnToBePicked, person.getFornavn());

    }

    @Test
    void should_pick_non_FREG_navn_when_other_navn_is_latest_navn_when_more_than_one_navn() {
        String expectedNavnToBePicked = "navn2";
        Navn navn1 = new Navn();
        navn1.setFornavn("navn1");
        PdlMetadata metadata = new PdlMetadata();
        metadata.setMaster("FREG");
        navn1.setMetadata(metadata);
        PdlFolkeregisterMetadata folkeregisterMetadata = new PdlFolkeregisterMetadata();
        folkeregisterMetadata.setAjourholdstidspunkt(LocalDate.of(1960, 5, 3));
        navn1.setFolkeregistermetadata(folkeregisterMetadata);

        Navn navn2 = new Navn();
        navn2.setFornavn(expectedNavnToBePicked);
        PdlMetadata metadata2 = new PdlMetadata();
        metadata2.setMaster("PDL");
        PdlMetadataEndring endring1 = new PdlMetadataEndring();
        endring1.setRegistrert(LocalDate.of(1990, 4, 2));
        PdlMetadataEndring endring2 = new PdlMetadataEndring();
        endring2.setRegistrert(LocalDate.of(1955, 4, 2));
        metadata2.setEndringer(List.of(endring1, endring2));
        navn2.setMetadata(metadata2);
        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(null, List.of(navn1, navn2));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertEquals(expectedNavnToBePicked, person.getFornavn());

    }

    @Test
    void should_pick_FREG_navn_when_FREG_navn_is_latest_navn_when_more_than_one_navn() {
        String expectedNavnToBePicked = "navn1";
        Navn navn1 = new Navn();
        navn1.setFornavn(expectedNavnToBePicked);
        PdlMetadata metadata = new PdlMetadata();
        metadata.setMaster("FREG");
        navn1.setMetadata(metadata);
        PdlFolkeregisterMetadata folkeregisterMetadata = new PdlFolkeregisterMetadata();
        folkeregisterMetadata.setAjourholdstidspunkt(LocalDate.of(1950, 5, 3));
        navn1.setFolkeregistermetadata(folkeregisterMetadata);

        Navn navn2 = new Navn();
        navn2.setFornavn("navn2");
        PdlMetadata metadata2 = new PdlMetadata();
        metadata2.setMaster("PDL");
        PdlMetadataEndring endring = new PdlMetadataEndring();
        endring.setRegistrert(LocalDate.of(1950, 4, 2));
        metadata2.setEndringer(List.of(endring));
        navn2.setMetadata(metadata2);
        PdlResponse pdlResponse = getPdlResponseWithFoedselsdatoAndNavn(null, List.of(navn1, navn2));

        Person person = PersonMapper.fromDto(pdlResponse, PidGenerator.generatePidAtAge(66));

        assertEquals(expectedNavnToBePicked, person.getFornavn());

    }

    @Test
    void should_pick_first_fodselsdato_when_multiple_fodselsdato_from_PDL() {
        LocalDate expectedFodselsdato = LocalDate.of(1982, 3, 4);

        Person person = PersonMapper.fromDto(getPdlResponseWithFoedselsdatoAndNavn(List.of(
                createFoedselsdato(expectedFodselsdato),
                createFoedselsdato(LocalDate.of(1982, 3, 3)),
                createFoedselsdato(LocalDate.of(1982, 3, 5))), null), PidGenerator.generatePidAtAge(50));

        assertEquals(expectedFodselsdato, person.getFodselsdato());
    }

    private PdlResponse getPdlResponseWithFoedselsdatoAndNavn(List<Foedselsdato> foedselsdato, List<Navn> navn) {
        HentPersonResponse hentPersonResponse = new HentPersonResponse();
        hentPersonResponse.setFoedselsdato(foedselsdato);
        hentPersonResponse.setNavn(navn);
        PdlData pdlData = new PdlData();
        pdlData.setHentPerson(hentPersonResponse);
        PdlResponse pdlResponse = new PdlResponse();
        pdlResponse.setData(pdlData);

        return pdlResponse;
    }

    private Foedselsdato createFoedselsdato(LocalDate date) {
        Foedselsdato foedselsdato = new Foedselsdato();
        foedselsdato.setFoedselsdato(date);
        return foedselsdato;
    }

}