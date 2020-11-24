package no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping;

import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.EndringPensjonsopptjening;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Opptjening;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.EndringPensjonsopptjeningDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpptjeningMapperTest {

    @Test
    void test_that_toDto_maps_relevant_values() {
        OpptjeningDto dto = OpptjeningMapper.toDto(opptjening());
        assertOpptjening(dto);
    }

    private static Opptjening opptjening() {
        var opptjening = new Opptjening(1L, 2.1D);
        opptjening.setPensjonsbeholdning(2L);
        opptjening.setRestpensjon(3.1D);
        opptjening.setMaxUforegrad(3);
        opptjening.setOmsorgspoeng(4.1D);
        opptjening.setOmsorgspoengType("poengtype");
        opptjening.setOpptjeningsendringer(List.of(endring()));
        opptjening.addMerknad(MerknadCode.HELT_UTTAK);
        opptjening.addMerknader(List.of(MerknadCode.FORSTEGANGSTJENESTE, MerknadCode.REFORM));
        return opptjening;
    }

    private static void assertOpptjening(OpptjeningDto dto) {
        assertEquals(1, dto.getPensjonsgivendeInntekt());
        assertEquals(2, dto.getPensjonsbeholdning());
        assertEquals(2.1D, dto.getPensjonspoeng());
        assertEquals(3.1D, dto.getRestpensjon());
        assertEquals(4.1D, dto.getOmsorgspoeng());
        assertEquals("poengtype", dto.getOmsorgspoengType());
        assertEquals(3, dto.getMaksUforegrad());
        assertEndringer(dto.getEndringOpptjening());
        assertMerknader(dto.getMerknader());
    }

    private static void assertEndringer(List<EndringPensjonsopptjeningDto> endringer) {
        assertEquals(1, endringer.size());
        assertEndring(endringer.get(0));
    }

    private static void assertMerknader(List<MerknadCode> merknader) {
        assertEquals(3, merknader.size());
        assertTrue(merknader.contains(MerknadCode.HELT_UTTAK));
        assertTrue(merknader.contains(MerknadCode.FORSTEGANGSTJENESTE));
        assertTrue(merknader.contains(MerknadCode.REFORM));
    }

    private static EndringPensjonsopptjening endring() {
        return EndringPensjonsopptjening.nyOpptjening(
                1991,
                1.2D,
                2.2D,
                1,
                3.2D,
                List.of(GrunnlagTypeCode.INNTEKT_GRUNNLAG),
                2);
    }

    private static void assertEndring(EndringPensjonsopptjeningDto endring) {
        assertEquals(1.2D, endring.getPensjonsbeholdningBelop());
        assertEquals(2.2D, endring.getEndringBelop()); // innskudd
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertEquals(GrunnlagTypeCode.INNTEKT_GRUNNLAG, endring.getGrunnlagTypes().get(0));
        assertEquals(3.2D, endring.getGrunnlag());
        assertEquals(TypeArsakCode.OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getArsakDetails().size());
        assertEquals(DetailsArsakCode.OPPTJENING_GRADERT, endring.getArsakDetails().get(0));
        assertEquals(LocalDate.of(1991, 1, 1), endring.getDato());
        assertEquals(2, endring.getUforegrad());
        assertEquals(1, endring.getUttaksgrad());
    }
}
