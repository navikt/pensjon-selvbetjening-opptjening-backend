package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_2012;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode.OPPTJENING_GRADERT;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode.*;
import static no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode.*;
import static org.junit.jupiter.api.Assertions.*;

class EndringPensjonsbeholdningCalculatorTest {

    @Test
    void when_empty_input_then_calculator_returns_empty_list() {
        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, emptyList(), emptyList());

        assertTrue(endringer.isEmpty());
    }

    @Test
    void when_beholdning_list_has_one_element_with_fomDate_GivenYear_then_calculator_returns_2_elements() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 3, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(2, endringer.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_fomDate_1JanGivenYear_then_calculator_returns_2_elements() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 1, 1));

        List<EndringPensjonsopptjening> endringer =
                EndringPensjonsbeholdningCalculator
                        .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(2, endringer.size());
    }

    @Test
    void when_beholdning_list_has_one_element_with_fomDate_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculator_returns_3_elements() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(3, endringer.size());
    }

    @Test
    void when_beholdning_with_fomDate_GivenYear_then_calculator_returns_2_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        Beholdning beholdning = newBeholdning(10D, LocalDate.of(2020, 2, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(2, endringer.size());
        assertNull(endringer.get(0).getEndringsbelop());
        assertEquals(0, endringer.get(0).getBeholdningsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(1).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(1).getBeholdningsbelop());
    }

    @Test
    void when_beholdning_with_fomDate_1JanGivenYear_and_uttak_then_calculator_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        Beholdning beholdning = newBeholdning(10D, fomDato);
        Uttaksgrad uttaksgrad = uttaksgradFom(fomDato);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        assertEquals(3, endringer.size());
        assertNull(endringer.get(0).getEndringsbelop());
        assertEquals(0, endringer.get(0).getBeholdningsbelop());
        assertEquals(0, endringer.get(1).getBeholdningsbelop());
        assertEquals(0, endringer.get(1).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getBeholdningsbelop());
    }

    @Test
    void when_beholdning_with_fomDate_1JanGivenYear_with_BeholdningInnskudd_and_uttak_then_calculator_returns_3_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        Beholdning beholdning = beholdningWithInnskudd(fomDato);
        Uttaksgrad uttaksgrad = uttaksgradFom(fomDato);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        assertEquals(3, endringer.size());
        assertNull(endringer.get(0).getEndringsbelop());
        assertEquals(0, endringer.get(0).getBeholdningsbelop());
        assertEquals(beholdning.getInnskudd(), endringer.get(1).getBeholdningsbelop());
        assertEquals(beholdning.getInnskudd(), endringer.get(1).getEndringsbelop());
        assertEquals(beholdning.getBelop() - beholdning.getInnskudd(), endringer.get(2).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getBeholdningsbelop());
    }

    @Test
    void when_beholdning_with_fomDate_1JanGivenYear_and_TomDato_31DecGivenYear_and_uttak_then_calculator_returns_4_elements_med_endringBelop_og_pensjonsbeholdningBelop() {
        LocalDate fomDato = LocalDate.of(2020, 1, 1);
        LocalDate tomDato = LocalDate.of(2020, 12, 31);
        Beholdning beholdning = newBeholdning(10D, fomDato, tomDato);
        Uttaksgrad uttaksgrad = uttaksgrad(fomDato, tomDato);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        assertEquals(4, endringer.size());
        assertNull(endringer.get(0).getEndringsbelop());
        assertEquals(0, endringer.get(0).getBeholdningsbelop());
        assertEquals(0, endringer.get(1).getBeholdningsbelop());
        assertEquals(0, endringer.get(1).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getBeholdningsbelop());
        assertNull(endringer.get(3).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(3).getBeholdningsbelop());
    }

    @Test
    void when_beholdning_list_has_one_element_with_fomDateOnRegulationDate_with_lonnsvekstregulering_and_uttak_then_calculator_returns_3_elements() {
        LocalDate fomDato = LocalDate.of(2020, 5, 1);
        Beholdning beholdning = newBeholdning(100D, fomDato, new Lonnsvekstregulering(10D));
        Uttaksgrad uttaksgrad = uttaksgradFom(fomDato);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        double reguleringsbelop = beholdning.getLonnsvekstreguleringsbelop();
        assertEquals(3, endringer.size());
        assertNull(endringer.get(0).getEndringsbelop());
        assertEquals(0, endringer.get(0).getBeholdningsbelop());
        assertEquals(reguleringsbelop, endringer.get(1).getBeholdningsbelop());
        assertEquals(reguleringsbelop, endringer.get(1).getEndringsbelop());
        assertEquals(beholdning.getBelop() - reguleringsbelop, endringer.get(2).getEndringsbelop());
        assertEquals(beholdning.getBelop(), endringer.get(2).getBeholdningsbelop());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_100_then_calculator_returns_ArsakDetailCode_OPPTJENING_HEL() {
        Beholdning beholdning = beholdningWithVedtak(LocalDate.of(2020, 1, 1));
        Uttaksgrad uttaksgrad = uttaksgrad(beholdning.getVedtakId(), 100);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        assertEquals(DetailsArsakCode.OPPTJENING_HEL, endringer.get(1).getArsakDetails().get(0));
        assertEquals(OPPTJENING, endringer.get(1).getArsakType());
    }

    @Test
    void when_GivenYear_2020_with_uttaksgrad_value_lessthan100_then_calculator_returns_ArsakDetailCode_OPPTJENING_GRADERT() {
        Beholdning beholdning = beholdningWithVedtak(LocalDate.of(2020, 1, 1));
        Uttaksgrad uttaksgrad = uttaksgrad(beholdning.getVedtakId(), 50);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING_GRADERT, endring.getArsakDetails().get(0));
        assertEquals(OPPTJENING, endring.getArsakType());
    }

    @Test
    void when_fomDate_1Jan2020_with_uttaksgrad_value_0_then_calculator_returns_ArsakDetailCode_OPPTJENING_2012() {
        Beholdning beholdning = beholdningWithVedtak(LocalDate.of(2020, 1, 1));
        Uttaksgrad uttaksgrad = uttaksgrad(beholdning.getVedtakId(), 0);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), singletonList(uttaksgrad));

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING_2012, endring.getArsakDetails().get(0));
        assertEquals(OPPTJENING, endring.getArsakType());
    }

    @Test
    void when_fomDate_1MayGivenYear_with_Lonnsvekstregulering_then_calculator_returns_ArsakDetailCode_REGULERING() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 5, 1), new Lonnsvekstregulering(2D));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(DetailsArsakCode.REGULERING, endring.getArsakDetails().get(0));
        assertEquals(REGULERING, endring.getArsakType());
    }

    @Test
    void when_fomDate_1JanGivenYear_then_calculator_returns_2_ArsakType_values() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 1, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(OPPTJENING, endringer.get(1).getArsakType());
    }

    @Test
    void when_fomDate_1JanGivenYear_and_GivenYear_2010_then_calculator_returns_2_ArsakType_values() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2010, 1, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2010, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(INNGAENDE_2010, endringer.get(1).getArsakType());
    }

    @Test
    void when_fomDate_1JanGivenYear_and_TomDato_31DecGivenYear_then_calculator_returns_3_ArsakType_values() {
        Beholdning beholdning = newBeholdning(1D,
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 12, 31));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(OPPTJENING, endringer.get(1).getArsakType());
        assertEquals(UTGAENDE, endringer.get(2).getArsakType());
    }

    @Test
    void when_fomDate_before_1st_May_GivenYear_then_calculator_returns_2_ArsakType_values() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 3, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(UTTAK, endringer.get(1).getArsakType());
    }

    @Test
    void when_fomDate_After_1st_May_GivenYear_then_calculator_returns_2_ArsakType_values() {
        Beholdning beholdning = newBeholdning(1D, LocalDate.of(2020, 6, 1));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(UTTAK, endringer.get(1).getArsakType());
    }

    @Test
    void when_fomDate_1MayGivenYear_with_Lonnsvekstregulering_then_calculator_returns_2_ArsakType_values() {
        Beholdning beholdning = beholdningWithInnskudd(
                LocalDate.of(2020, 5, 1), new Lonnsvekstregulering(10D));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        assertEquals(INNGAENDE, endringer.get(0).getArsakType());
        assertEquals(REGULERING, endringer.get(1).getArsakType());
    }

    @Test
    void when_beholdningGrunnlag_is_inntekt_then_calculator_returns_grunnlagTypeCode_INNTEKT_GRUNNLAG() {
        double inntekt = 1D;
        Beholdning beholdning = beholdningFom1Jan2020(1D, inntekt, 2D, 0D, 0D, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().contains(INNTEKT_GRUNNLAG));
        assertEquals(inntekt, endring.getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_omsorg_then_calculator_returns_grunnlagTypeCode_OMSORGSOPPTJENING_GRUNNLAG() {
        double omsorg = 1d;
        Beholdning beholdning = beholdningFom1Jan2020(omsorg, 2d, omsorg, 0D, 0D, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().contains(OMSORGSOPPTJENING_GRUNNLAG));
        assertEquals(omsorg, endring.getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_forstegangstjeneste_then_calculator_returns_grunnlagTypeCode_FORSTEGANGSTJENESTE_GRUNNLAG() {
        double forstegangstjeneste = 1D;
        Beholdning beholdning = beholdningFom1Jan2020(forstegangstjeneste, 0D, 0D, forstegangstjeneste, 0D, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().contains(FORSTEGANGSTJENESTE_GRUNNLAG));
        assertEquals(forstegangstjeneste, endring.getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_dagpenger_then_calculator_returns_grunnlagTypeCode_DAGPENGER_GRUNNLAG() {
        double dagpenger = 1d;
        Beholdning beholdning = beholdningFom1Jan2020(dagpenger, 0D, 0D, 0D, dagpenger, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().contains(DAGPENGER_GRUNNLAG));
        assertEquals(dagpenger, endring.getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_ufore_then_calculator_returns_grunnlagTypeCode_UFORE_GRUNNLAG() {
        double uforebelop = 1d;
        Beholdning beholdning = beholdningFom1Jan2020(uforebelop, 0D, 0D, 0D, 0D, new Uforeopptjening(100, uforebelop));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(1, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().contains(UFORE_GRUNNLAG));
        assertEquals(uforebelop, endring.getGrunnlag());
    }

    @Test
    void when_forstegangstjeneste_ufore_or_dagpenger_is_among_more_than_one_possible_grunnlag_then_calculator_returns_all_grunnlagTypes_present_except_OMSORGSOPPTJENING() {
        double grunnlag = 7d;
        Beholdning beholdning = beholdningFom1Jan2020(grunnlag, 2d, 3d, 1d, 4d, 5d);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertEquals(4, endring.getGrunnlagTypes().size());
        assertTrue(endring.getGrunnlagTypes().containsAll(List.of(FORSTEGANGSTJENESTE_GRUNNLAG, UFORE_GRUNNLAG, DAGPENGER_GRUNNLAG, INNTEKT_GRUNNLAG)));
        assertEquals(grunnlag, endring.getGrunnlag());
    }

    @Test
    void when_beholdningGrunnlag_is_null_then_calculator_returns_grunnlagTypeCode_NO_GRUNNLAG() {
        Beholdning beholdning = beholdningFom1Jan2020(null, 0D, 0D, 0D, 0D, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertTrue(endring.getGrunnlagTypes().contains(NO_GRUNNLAG));
    }

    @Test
    void when_beholdningGrunnlag_is_0_then_calculator_returns_grunnlagTypeCode_NO_GRUNNLAG() {
        Beholdning beholdning = beholdningFom1Jan2020(0D, 0D, 0D, 0D, 0D, 0D);

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, singletonList(beholdning), emptyList());

        EndringPensjonsopptjening endring = endringer.get(1);
        assertEquals(OPPTJENING, endring.getArsakType());
        assertTrue(endring.getGrunnlagTypes().contains(NO_GRUNNLAG));
    }

    @Test
    void when_inngaende_and_uttakAfterRegulation_then_calculator_returns_difference_as_endringsbelop() {
        Beholdning inngaende = newBeholdning(10D,
                LocalDate.of(2019, 12, 1),
                LocalDate.of(2019, 12, 31));
        Beholdning uttakAfterRegulation = newBeholdning(11D,
                LocalDate.of(2020, 6, 1),
                LocalDate.of(2020, 6, 30));

        List<EndringPensjonsopptjening> endringer = EndringPensjonsbeholdningCalculator
                .calculatePensjonsbeholdningsendringer(2020, List.of(inngaende, uttakAfterRegulation), emptyList());

        Double actualEndring = endringer
                .stream()
                .filter(endring -> UTTAK.equals(endring.getArsakType()))
                .findFirst()
                .map(EndringPensjonsopptjening::getEndringsbelop)
                .orElse(0D);
        assertEquals(1D, actualEndring);
    }

    private static Beholdning newBeholdning(double belop, LocalDate fomDate) {
        return new Beholdning(
                null, "", "", "",
                belop,
                null,
                fomDate,
                null,
                null, null, null, null, "",
                null, null, null, null,
                null, null);
    }

    private static Beholdning newBeholdning(double belop, LocalDate fomDate, LocalDate tomDate) {
        return new Beholdning(
                null, "", "", "",
                belop,
                null,
                fomDate,
                tomDate,
                null, null, null, null, "",
                null, null, null, null,
                null, null);
    }

    private static Beholdning newBeholdning(double belop, LocalDate fomDate, Lonnsvekstregulering regulering) {
        return new Beholdning(
                null, "", "", "",
                belop,
                null,
                fomDate,
                null, null, null,
                null, null, "",
                regulering, null, null, null,
                null, null);
    }

    private static Beholdning beholdningFom1Jan2020(Double grunnlag, double inntekt, double omsorgsbelop,
                                                    double forstegangstjenestebelop, double dagpenger,
                                                    double uforebelop) {
        return beholdningFom1Jan2020(grunnlag, inntekt, omsorgsbelop,
                forstegangstjenestebelop, dagpenger,
                new Uforeopptjening(0, uforebelop));
    }

    private static Beholdning beholdningFom1Jan2020(Double grunnlag, double inntekt, double omsorgsbelop,
                                                    double forstegangstjenestebelop, double dagpenger,
                                                    Uforeopptjening uforeopptjening) {
        return new Beholdning(
                null, "", "", "",
                100D,
                null,
                LocalDate.of(2020, 1, 1),
                null,
                grunnlag,
                null, null, null, "",
                new Lonnsvekstregulering(null),
                new Inntektsopptjening(1990, inntekt, null),
                new Omsorgsopptjening(1990, omsorgsbelop, null),
                new Dagpengeopptjening(1990, dagpenger, null),
                new Forstegangstjenesteopptjening(1990, forstegangstjenestebelop),
                uforeopptjening);
    }

    private static Beholdning beholdningWithInnskudd(LocalDate fomDate) {
        return new Beholdning(
                null, "", "", "",
                100.0,
                null,
                fomDate,
                null, null, null,
                10.0,
                null, "",
                null, null, null, null,
                null, null);
    }

    private static Beholdning beholdningWithInnskudd(LocalDate fomDate, Lonnsvekstregulering regulering) {
        return new Beholdning(
                null, "", "", "",
                1.0,
                null,
                fomDate,
                null, null, null,
                10.0, null, "",
                regulering, null, null, null,
                null, null);
    }

    private static Beholdning beholdningWithVedtak(LocalDate fomDate) {
        return new Beholdning(
                null, "", "", "",
                1.0,
                1L,
                fomDate,
                null,
                null, null, null, null, "",
                null, null, null, null,
                null, null);
    }

    private static Uttaksgrad uttaksgrad(long vedtakId, int uttaksgrad) {
        return new Uttaksgrad(
                vedtakId,
                uttaksgrad,
                LocalDate.of(2019, 1, 1),
                LocalDate.of(2020, 12, 31));
    }

    private static Uttaksgrad uttaksgrad(LocalDate fomDate, LocalDate tomDate) {
        return new Uttaksgrad(null, null, fomDate, tomDate);
    }

    private static Uttaksgrad uttaksgradFom(LocalDate date) {
        return new Uttaksgrad(null, null, date, null);
    }
}
