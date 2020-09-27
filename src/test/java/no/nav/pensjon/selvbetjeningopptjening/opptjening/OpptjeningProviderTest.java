package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlRequest;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.PdlResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedsel;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.HentPersonResponse;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.PdlData;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Inntekt;
import no.nav.pensjon.selvbetjeningopptjening.model.InntektOpptjeningBelop;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.Restpensjon;
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
    private PdlConsumer pdlConsumer;

    @Mock
    private EndringPensjonsbeholdningCalculator endringPensjonsbeholdningCalculator;

    @Mock
    private MerknadHandler merknadHandler;

    @Captor
    private ArgumentCaptor<Integer> yearCaptor;

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
        opptjeningProvider.setPdlConsumer(pdlConsumer);
    }

    @Test
    void When_Fnr_is_not_in_proper_number_format_and_no_pdl_response_then_calculateOpptjeningForFnr_throws_NumberFormatException() {
        String fnr = "fnr";
        PdlResponse pdlResponse = new PdlResponse();
        PdlData pdlData = new PdlData();
        HentPersonResponse hentPersonResponse = new HentPersonResponse();
        pdlData.setHentPerson(hentPersonResponse);
        pdlResponse.setData(pdlData);

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(pdlResponse);

        assertThrows(NumberFormatException.class, () -> opptjeningProvider.calculateOpptjeningForFnr(fnr));
    }

    @Test
    void When_Uttaksgrad_is_not_set_then_calculateOpptjeningForFnr_throws_NullPointerException() {
        String fnr = "06076323304";
        List<Uttaksgrad> uttaksgradList = List.of(new Uttaksgrad());
        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        assertThrows(NullPointerException.class, () -> opptjeningProvider.calculateOpptjeningForFnr(fnr));
    }

    @Test
    void When_Fnr_UserGroup5_with_beholdning_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
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
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

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
        beholdning.setFomDato(LocalDate.of(1983, 1, 1));
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
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

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
        beholdning.setFomDato(LocalDate.of(1983, 1, 1));
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
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1983).getPensjonsbeholdning());
    }

    /*  Tests with pensjonspoeng for user group 123 and 4 */
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
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

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
        inntekt.setBelop(200L);
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
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

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
        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng, pensjonspoeng1);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertEquals(2, opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    @Test
    void When_UserGroup123_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        String fnr = "06075023304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1970);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        List<Pensjonspoeng> pensjonspoengList = Collections.singletonList(pensjonspoeng);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1950, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getPensjonspoengType(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoeng());
    }

    @Test
    void When_UserGroup123_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        String fnr = "06074423304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1960);
        pensjonspoeng.setPensjonspoengType("PPI");
        pensjonspoeng.setPoeng(10d);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        pensjonspoeng.setInntekt(inntekt);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        List<Pensjonspoeng> pensjonspoengList = Collections.singletonList(pensjonspoeng);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1944, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getInntekt().getBelop().intValue(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonsgivendeInntekt());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonspoeng());
    }

    @Test
    void When_UserGroup123_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        String fnr = "06074023304";

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1960);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        Pensjonspoeng pensjonspoeng1 = new Pensjonspoeng();
        pensjonspoeng1.setAr(1963);
        pensjonspoeng1.setPensjonspoengType("OSFE");
        pensjonspoeng1.setPoeng(20d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();
        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng, pensjonspoeng1);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonspoengConsumer.getPensjonspoengListe(fnr)).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1940, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertEquals(2, opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    /*  Restpensjon tests */
    @Test
    void When_Fnr_UserGroup5_with_Restpensjon_and_no_Uttaksgrad_then_calculateOpptjeningForFnr_returns_no_OpptjeningDto() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertNull(opptjeningDtoMap);
    }

    @Test
    void When_Fnr_UserGroup5_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06076323304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void When_Fnr_UserGroup5_with_RestPensjonstillegg_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06076323304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestPensjonstillegg(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestPensjonstillegg(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void When_Fnr_UserGroup5_with_RestTilleggspensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06076323304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestTilleggspensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestTilleggspensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void When_Fnr_UserGroup5_with_RestPensjonstillegg_and_RestGrunnpensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06076323304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestPensjonstillegg(100d);
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestPensjonstillegg() + restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void When_Fnr_UserGroup4_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06076023304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void When_Fnr_UserGroup123_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        String fnr = "06075023304";

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1970, 1, 1));

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Restpensjon> restpensjonList = Collections.singletonList(restpensjon);
        List<Uttaksgrad> uttaksgradList = Collections.singletonList(uttaksgrad);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(restpensjonConsumer.getRestpensjonListe(fnr)).thenReturn(restpensjonList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1950, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1970).getRestpensjon());
    }

    /* Tests for inntekt for user group 5 */
    @Test
    void When_Fnr_UserGroup5_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
        beholdning.setBelop(100d);
        InntektOpptjeningBelop inntektOpptjeningBelop = new InntektOpptjeningBelop();
        inntektOpptjeningBelop.setAr(1980);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntektOpptjeningBelop.setSumPensjonsgivendeInntekt(inntekt);
        beholdning.setInntektOpptjeningBelop(inntektOpptjeningBelop);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);
        List<Inntekt> inntektList = new ArrayList<>();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt().getBelop().intValue(), opptjeningDtoMap.get(1980).getPensjonsgivendeInntekt());
    }

    @Test
    void When_Fnr_UserGroup5_with_Inntekt_without_InntektType_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("");

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = Collections.singletonList(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void When_Fnr_UserGroup5_with_Inntekt_and_InntektType_without_Beholdning_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("SUM_PI");

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = new ArrayList<>();
        List<Inntekt> inntektList = Collections.singletonList(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void When_Fnr_UserGroup5_with_Inntekt_and_InntektType_SUM_PI_and_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningData_with_PensjonsgivendeInntekt() {
        String fnr = "06076323304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("SUM_PI");

        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1981, 1, 1));
        beholdning.setBelop(100d);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);
        List<Inntekt> inntektList = Collections.singletonList(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1963, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertEquals(inntekt.getBelop().intValue(), opptjeningResponse.getOpptjeningData().get(1980).getPensjonsgivendeInntekt());

    }

    /* Tests for beholdning with inntekt  for user group 4 */
    @Test
    void When_Fnr_UserGroup4_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        String fnr = "06076023304";

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Beholdning beholdning = new Beholdning();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
        beholdning.setBelop(100d);
        InntektOpptjeningBelop inntektOpptjeningBelop = new InntektOpptjeningBelop();
        inntektOpptjeningBelop.setAr(1980);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntektOpptjeningBelop.setSumPensjonsgivendeInntekt(inntekt);
        beholdning.setInntektOpptjeningBelop(inntektOpptjeningBelop);

        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = Collections.singletonList(beholdning);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(1960, 7, 6), null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt().getBelop().intValue(), opptjeningDtoMap.get(1980).getPensjonsgivendeInntekt());
    }

    /* Tests for PDL Respose */
    @Test
    public void When_PdlResponse_not_contains_foedselsdato_then_use_foedselsaar_from_pdl_instead() {
        String fnr = "06076023304";
        Integer expectedFoedselsaar = 1970;

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(null, expectedFoedselsaar));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(new ArrayList<>());

        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(new ArrayList<>());

        opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertThat(yearCaptor.getValue() - 13, is(expectedFoedselsaar));
    }

    @Test
    public void When_PdlResponse_contains_foedselsdato_then_use_foedselsaar_from_pdl_foedselsdato() {
        String fnr = "06076023304";
        int expectedFoedselsaar = 1970;

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(expectedFoedselsaar, 8, 9), 1990));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(new ArrayList<>());

        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(new ArrayList<>());

        opptjeningProvider.calculateOpptjeningForFnr(fnr);

        assertThat(yearCaptor.getValue() - 13, is(expectedFoedselsaar));
    }

    private PdlResponse createPdlResponseForFoedselsdato(LocalDate foedselsdato, Integer foedselsaar) {
        PdlResponse pdlResponse = new PdlResponse();
        PdlData pdlData = new PdlData();
        HentPersonResponse hentPersonResponse = new HentPersonResponse();
        Foedsel foedsel = new Foedsel();
        foedsel.setFoedselsdato(foedselsdato);
        foedsel.setFoedselsaar(foedselsaar);

        hentPersonResponse.setFoedsel(List.of(foedsel));
        pdlData.setHentPerson(hentPersonResponse);
        pdlResponse.setData(pdlData);

        return pdlResponse;
    }
}