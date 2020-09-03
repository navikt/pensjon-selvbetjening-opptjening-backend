package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.*;

import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_2012;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_GRADERT;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.INNGAENDE;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.INNGAENDE_2010;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.OPPTJENING;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.REGULERING;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.UTGAENDE;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.UTTAK;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Lonnsvekstregulering;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;

class EndringPensjonsbeholdningCalculatorTest {

    EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator;

    @BeforeEach
    public void setUp(){ endringPensjonsbeholdningCalculator = new EndringPensjonsbeholdningCalculator(); }

    @Test
    void when_empty_input_then_calculateEndringPensjonsbeholdning_returns_null_list() {
        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, new ArrayList<>(), new ArrayList<>());

        assertNull(dtos);
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_GivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 3, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(2, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_then_calculateEndringPensjonsbeholdning_returns_3_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(3, dtos.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculateEndringPensjonsbeholdning_returns_4_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020,12,31));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(4, dtos.size());
    }

    @Test
    void when_beholdning_with_FomDato_GivenYear_then_calculateEndringPensjonsbeholdning_returns_2_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 2, 1));
        beholdning.setBelop(10d);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), new ArrayList<>());

        assertEquals(2, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0,dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBelop(),dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(1).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_then_calculateEndringPensjonsbeholdning_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(10d);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), new ArrayList<>());

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0,dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(0,dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(0,dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_with_BeholdningInnskudd_then_calculateEndringPensjonsbeholdning_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(100d);
        beholdning.setBeholdningInnskudd(10d);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), new ArrayList<>());

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0,dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBeholdningInnskudd(),dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getBeholdningInnskudd(),dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop() - beholdning.getBeholdningInnskudd(),dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_with_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculateEndringPensjonsbeholdning_returns_4_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020, 12, 31));
        beholdning.setBelop(10d);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, Collections.singletonList(beholdning), new ArrayList<>());

        assertEquals(4, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0,dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(0,dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(0,dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getPensjonsbeholdningBelop());
        assertNull(dtos.get(3).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(3).getPensjonsbeholdningBelop());
    }

    @Test
    void when_beholdning_list_has_one_element_with_FomDato_1MayGivenYear_with_Lonnsvekstregulering_then_calculateEndringPensjonsbeholdning_returns_4_elements() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 5, 1));
        beholdning.setBelop(100d);
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(10d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(3, dtos.size());
        assertNull(dtos.get(0).getEndringBelop());
        assertEquals(0,dtos.get(0).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getLonnsvekstregulering().getReguleringsbelop(),dtos.get(1).getPensjonsbeholdningBelop());
        assertEquals(beholdning.getLonnsvekstregulering().getReguleringsbelop(),dtos.get(1).getEndringBelop());
        assertEquals(beholdning.getBelop() - beholdning.getLonnsvekstregulering().getReguleringsbelop(),dtos.get(2).getEndringBelop());
        assertEquals(beholdning.getBelop(),dtos.get(2).getPensjonsbeholdningBelop());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_100_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_HEL() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(100);
        uttaksgrad.setFomDato(LocalDate.of(2019,1,1));
        uttaksgrad.setTomDato(LocalDate.of(2020,12,31));

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list,Collections.singletonList(uttaksgrad));

        assertEquals(DetailsArsakCode.OPPTJENING_HEL, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING,dtos.get(1).getArsakType());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_lessthan100_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_GRADERT() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(50);
        uttaksgrad.setFomDato(LocalDate.of(2019,1,1));
        uttaksgrad.setTomDato(LocalDate.of(2020,12,31));

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list,Collections.singletonList(uttaksgrad));

        assertEquals(OPPTJENING_GRADERT, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING,dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1Jan2020_with_uttaksgrad_value_0_then_addNyOpptjening_returns_ArsakDetailCode_OPPTJENING_2012() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);
        beholdning.setVedtakId(1L);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setVedtakId(1L);
        uttaksgrad.setUttaksgrad(0);
        uttaksgrad.setFomDato(LocalDate.of(2019,1,1));
        uttaksgrad.setTomDato(LocalDate.of(2020,12,31));

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list,Collections.singletonList(uttaksgrad));

        assertEquals(OPPTJENING_2012, dtos.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING,dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1MayGivenYear_with_Lonnsvekstregulering_then_addRegulering_returns_ArsakDetailCode_REGULERING() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 5, 1));
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(2d);
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(DetailsArsakCode.REGULERING, dtos.get(1).getArsakDetails().get(0));
        assertEquals(REGULERING,dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_then_addInngaendeBeholdning_and_addNyOpptjening_returns_3_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(OPPTJENING,dtos.get(1).getArsakType());
        assertEquals(UTTAK,dtos.get(2).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_and_GivenYear_2010_then_addInngaendeBeholdning_and_addNyOpptjening_returns_3_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2010, 1, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2010, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(INNGAENDE_2010,dtos.get(1).getArsakType());
        assertEquals(UTTAK,dtos.get(2).getArsakType());
    }

    @Test
    void when_FomDato_1JanGivenYear_and_TomDato_31DecGivenYear_then_addInngaendeBeholdning_and_addNyOpptjening_and_addUtgaendeBeholdning_returns_4_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 1, 1));
        beholdning.setTomDato(LocalDate.of(2020,12,31));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(OPPTJENING,dtos.get(1).getArsakType());
        assertEquals(UTTAK,dtos.get(2).getArsakType());
        assertEquals(UTGAENDE,dtos.get(3).getArsakType());
    }

    @Test
    void when_FomDato_Before_1st_May_GivenYear_then_addInngaendeBeholdning_and_addChangesUttaksgradBeforeReguleringAtMay1th_returns_2_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 3, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(UTTAK,dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_After_1st_May_GivenYear_then_addInngaendeBeholdning_and_addChangesUttaksgradAfterReguleringAtMay1th_returns_2_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 6, 1));
        beholdning.setBelop(1d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(UTTAK,dtos.get(1).getArsakType());
    }

    @Test
    void when_FomDato_1MayGivenYear_with_Lonnsvekstregulering_then_addInngaendeBeholdning_and_addRegulering_returns_2_ArsakType_values() {
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(2020, 5, 1));
        beholdning.setLonnsvekstregulering(new Lonnsvekstregulering());
        beholdning.getLonnsvekstregulering().setReguleringsbelop(10d);
        beholdning.setBelop(1d);
        beholdning.setBeholdningInnskudd(10d);

        List<Beholdning> list = Collections.singletonList(beholdning);

        List<EndringPensjonsopptjeningDto> dtos =
                endringPensjonsbeholdningCalculator.calculateEndringPensjonsbeholdning(2020, list, new ArrayList<>());

        assertEquals(INNGAENDE, dtos.get(0).getArsakType());
        assertEquals(REGULERING,dtos.get(1).getArsakType());
    }
}
