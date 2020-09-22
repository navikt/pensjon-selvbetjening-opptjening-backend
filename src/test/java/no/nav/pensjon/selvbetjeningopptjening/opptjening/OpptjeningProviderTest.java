package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;

@ExtendWith(MockitoExtension.class)
class OpptjeningProviderTest {

    @Mock
    private PensjonsbeholdningConsumer pensjonsbeholdningConsumer;

    @Mock
    private OpptjeningsgrunnlagConsumer opptjeningsgrunnlagConsumer;

    @Mock
    private PensjonspoengConsumer pensjonspoengConsumer;

    @Mock
    private RestpensjonConsumer restpensjonConsumer;

    @Mock
    private PersonConsumer personConsumer;

    @Mock
    private UttaksgradConsumer uttaksgradConsumer;

    @Mock
    private EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator;

    @Mock
    private MerknadHandler merknadHandler;

    private OpptjeningProvider opptjeningProvider;

    @BeforeEach
    public void setUp() {
        opptjeningProvider = new OpptjeningProvider();
        opptjeningProvider.setEndringPensjonsbeholdningCalculator(endringPensjonsbeholdningCalculator);
        opptjeningProvider.setMerknadHandler(merknadHandler);
        opptjeningProvider.setOpptjeningsgrunnlagConsumer(opptjeningsgrunnlagConsumer);
        opptjeningProvider.setPensjonsbeholdningConsumer(pensjonsbeholdningConsumer);
        opptjeningProvider.setPensjonspoengConsumer(pensjonspoengConsumer);
        opptjeningProvider.setPersonConsumer(personConsumer);
        opptjeningProvider.setRestpensjonConsumer(restpensjonConsumer);
        opptjeningProvider.setUttaksgradConsumer(uttaksgradConsumer);
    }

    @Test
    void When_Fnr_is_not_in_proper_number_format_then_calculateOpptjeningForFnr_throws_NumberFormatException() {
        String fnr = "fnr";

        assertThrows(NumberFormatException.class,() -> opptjeningProvider.calculateOpptjeningForFnr(fnr));
    }

    @Test
    void When_Uttaksgrad_is_not_set_then_calculateOpptjeningForFnr_throws_NullPointerException() {
        String fnr = "06076323304";
        List<Uttaksgrad> uttaksgradList = List.of(new Uttaksgrad());
       // uttaksgradList.get(0).setUttaksgrad(50);
        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);

        assertThrows(NullPointerException.class,() -> opptjeningProvider.calculateOpptjeningForFnr(fnr));
    }

    @Test
    void When_Fnr_UserGroup5_with_beholdning_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1980,01,01));
        beholdning.setBelop(100d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);
        List<Inntekt> inntektList = new ArrayList<>();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(1, opptjeningDtoMap.size());
        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1980).getPensjonsbeholdning());
    }

    @Test
    void When_UserGroup5_with_and_FomDato_1983_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning_and_3_OpptjeningDto_WithNoOpptjening() {
        String fnr = "06076323304";
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1983,01,01));
        beholdning.setBelop(100d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);
        List<Inntekt> inntektList = new ArrayList<>();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(4, opptjeningDtoMap.size());
        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1983).getPensjonsbeholdning());
    }

    @Test
    void When_UserGroup4_with_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Pensjonsbeholdning() {
        String fnr = "06076023304";
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1983,01,01));
        beholdning.setBelop(100d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);
        List<Pensjonspoeng> pensjonspoengList = new ArrayList<>();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(),any(OpptjeningDto.class),anyList(),anyList(),any(AfpHistorikk.class),any(UforeHistorikk.class));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1983).getPensjonsbeholdning());
    }

    @Test
    void When_UserGroup4_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        String fnr = "06076023304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Pensjonspoeng> pensjonspoengList = Collections.singletonList(pensjonspoeng);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(),any(OpptjeningDto.class),anyList(),anyList(),any(AfpHistorikk.class),any(UforeHistorikk.class));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getPensjonspoengType(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoeng());
    }

    @Test
    void When_UserGroup4_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        String fnr = "06076023304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("PPI");
        pensjonspoeng.setPoeng(10d);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200l);
        pensjonspoeng.setInntekt(inntekt);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        List<Beholdning> beholdningList = new ArrayList<>();

        List<Pensjonspoeng> pensjonspoengList = Collections.singletonList(pensjonspoeng);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(),any(OpptjeningDto.class),anyList(),anyList(),any(AfpHistorikk.class),any(UforeHistorikk.class));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getInntekt().getBelop().intValue(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonsgivendeInntekt());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonspoeng());
    }

    @Test
    void When_UserGroup4_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        String fnr = "06076023304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        Pensjonspoeng pensjonspoeng1 = new Pensjonspoeng();
        pensjonspoeng1.setAr(1981);
        pensjonspoeng1.setPensjonspoengType("OSFE");
        pensjonspoeng1.setPoeng(20d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng,pensjonspoeng1);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(),any(OpptjeningDto.class),anyList(),anyList(),any(AfpHistorikk.class),any(UforeHistorikk.class));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(2, opptjeningResponse.getNumberOfYearsWithPensjonspoeng());

    }
}