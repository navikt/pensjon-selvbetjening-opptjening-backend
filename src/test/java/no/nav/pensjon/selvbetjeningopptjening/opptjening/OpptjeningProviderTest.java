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

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
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
import no.nav.pensjon.selvbetjeningopptjening.model.BeholdningDto;
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
    private MerknadHandler merknadHandler;
    @Captor
    private ArgumentCaptor<Integer> yearCaptor;

    private OpptjeningProvider opptjeningProvider;

    @BeforeEach
    void setUp() {
        opptjeningProvider = new OpptjeningProvider(
                pensjonsbeholdningConsumer,
                opptjeningsgrunnlagConsumer,
                pensjonspoengConsumer,
                restpensjonConsumer,
                personConsumer,
                pdlConsumer,
                uttaksgradConsumer,
                merknadHandler);
    }

    @Test
    void when_UserGroup5_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1968;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_UserGroup4_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1956;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_UserGroup123_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1950;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_Uttaksgrad_is_not_set_then_calculateOpptjeningForFnr_throws_NullPointerException() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);
        List<Uttaksgrad> uttaksgradList = List.of(new Uttaksgrad());

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        assertThrows(NullPointerException.class, () -> opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato)));
    }

    @Test
    void when_Fnr_UserGroup5_with_beholdning_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
        beholdning.setBelop(100d);
        List<BeholdningDto> beholdningList = Collections.singletonList(beholdning);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn( new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn( new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(1, opptjeningDtoMap.size());
        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1980).getPensjonsbeholdning());
    }

    @Test
    void when_UserGroup5_with_and_FomDato_1983_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning_and_3_OpptjeningDto_WithNoOpptjening() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1983, 1, 1));
        beholdning.setBelop(100d);
        List<BeholdningDto> beholdningList = Collections.singletonList(beholdning);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(4, opptjeningDtoMap.size());
        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1983).getPensjonsbeholdning());
    }

    @Test
    void when_UserGroup4_with_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Pensjonsbeholdning() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);
        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1983, 1, 1));
        beholdning.setBelop(100d);
        List<BeholdningDto> beholdningList = Collections.singletonList(beholdning);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(beholdningList);
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(new ArrayList<>());
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getBelop().longValue(), opptjeningDtoMap.get(1983).getPensjonsbeholdning());
    }

    /*  Tests with pensjonspoeng for user group 123 and 4 */
    @Test
    void when_UserGroup4_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(Collections.singletonList(pensjonspoeng));
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getPensjonspoengType(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoeng());
    }

    @Test
    void when_UserGroup4_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("PPI");
        pensjonspoeng.setPoeng(10d);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        pensjonspoeng.setInntekt(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(Collections.singletonList(pensjonspoeng));
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getInntekt().getBelop().intValue(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonsgivendeInntekt());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonspoeng());
    }

    @Test
    void when_UserGroup4_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1980);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        Pensjonspoeng pensjonspoeng1 = new Pensjonspoeng();
        pensjonspoeng1.setAr(1981);
        pensjonspoeng1.setPensjonspoengType("OSFE");
        pensjonspoeng1.setPoeng(20d);

        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng, pensjonspoeng1);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), anyList(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertEquals(pensjonspoengList.size(), opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    @Test
    void when_UserGroup123_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        LocalDate fodselsdato = LocalDate.of(1950, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1970);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(Collections.singletonList(pensjonspoeng));
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getPensjonspoengType(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getOmsorgspoeng());
    }

    @Test
    void when_UserGroup123_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1944, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1960);
        pensjonspoeng.setPensjonspoengType("PPI");
        pensjonspoeng.setPoeng(10d);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        pensjonspoeng.setInntekt(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(Collections.singletonList(pensjonspoeng));
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(pensjonspoeng.getInntekt().getBelop().intValue(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonsgivendeInntekt());
        assertEquals(pensjonspoeng.getPoeng(), opptjeningDtoMap.get(pensjonspoeng.getAr()).getPensjonspoeng());
    }

    @Test
    void when_UserGroup123_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        LocalDate fodselsdato = LocalDate.of(1940, 7, 6);

        Pensjonspoeng pensjonspoeng = new Pensjonspoeng();
        pensjonspoeng.setAr(1960);
        pensjonspoeng.setPensjonspoengType("OSFE");
        pensjonspoeng.setPoeng(10d);

        Pensjonspoeng pensjonspoeng1 = new Pensjonspoeng();
        pensjonspoeng1.setAr(1963);
        pensjonspoeng1.setPensjonspoengType("OSFE");
        pensjonspoeng1.setPoeng(20d);

        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng, pensjonspoeng1);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(pensjonspoengList);
        doNothing().when(merknadHandler).addMerknaderOnOpptjening(anyInt(), any(OpptjeningDto.class), any(), anyList(), any(AfpHistorikk.class), any(UforeHistorikk.class));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertEquals(pensjonspoengList.size(), opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    /*  Restpensjon tests */
    @Test
    void when_Fnr_UserGroup5_with_Restpensjon_and_no_Uttaksgrad_then_calculateOpptjeningForFnr_returns_no_OpptjeningDto() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertNull(opptjeningDtoMap);
    }

    @Test
    void when_Fnr_UserGroup5_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestPensjonstillegg_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestPensjonstillegg(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestPensjonstillegg(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestTilleggspensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestTilleggspensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestTilleggspensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestPensjonstillegg_and_RestGrunnpensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestPensjonstillegg(100d);
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestPensjonstillegg() + restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup4_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1980, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup123_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1950, 7, 6);

        Uttaksgrad uttaksgrad = new Uttaksgrad();
        uttaksgrad.setUttaksgrad(50);

        Restpensjon restpensjon = new Restpensjon();
        restpensjon.setRestGrunnpensjon(100d);
        restpensjon.setFomDato(LocalDate.of(1970, 1, 1));

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(Collections.singletonList(uttaksgrad));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(Collections.singletonList(restpensjon));
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningDtoMap.get(1970).getRestpensjon());
    }

    /* Tests for inntekt for user group 5 */
    @Test
    void when_Fnr_UserGroup5_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
        beholdning.setBelop(100d);
        InntektOpptjeningBelop inntektOpptjeningBelop = new InntektOpptjeningBelop();
        inntektOpptjeningBelop.setAr(1980);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntektOpptjeningBelop.setSumPensjonsgivendeInntekt(inntekt);
        beholdning.setInntektOpptjeningBelop(inntektOpptjeningBelop);

        List<BeholdningDto> beholdningList = Collections.singletonList(beholdning);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt().getBelop().intValue(), opptjeningDtoMap.get(1980).getPensjonsgivendeInntekt());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_without_InntektType_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("");
        List<Inntekt> inntektList = Collections.singletonList(inntekt);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_and_InntektType_without_Beholdning_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("SUM_PI");

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(Collections.singletonList(inntekt));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_and_InntektType_SUM_PI_and_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningData_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1963, 7, 6);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntekt.setInntektAr(1980);
        inntekt.setInntektType("SUM_PI");

        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1981, 1, 1));
        beholdning.setBelop(100d);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(Collections.singletonList(beholdning));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(Collections.singletonList(inntekt));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));

        assertEquals(inntekt.getBelop().intValue(), opptjeningResponse.getOpptjeningData().get(1980).getPensjonsgivendeInntekt());
    }

    /* Tests for beholdning with inntekt  for user group 4 */
    @Test
    void when_Fnr_UserGroup4_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1960, 7, 6);

        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

        BeholdningDto beholdning = new BeholdningDto();
        beholdning.setFomDato(LocalDate.of(1980, 1, 1));
        beholdning.setBelop(100d);
        InntektOpptjeningBelop inntektOpptjeningBelop = new InntektOpptjeningBelop();
        inntektOpptjeningBelop.setAr(1980);
        Inntekt inntekt = new Inntekt();
        inntekt.setBelop(200L);
        inntektOpptjeningBelop.setSumPensjonsgivendeInntekt(inntekt);
        beholdning.setInntektOpptjeningBelop(inntektOpptjeningBelop);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(Collections.singletonList(beholdning));
        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(fodselsdato, null));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(fodselsdato));
        Map<Integer, OpptjeningDto> opptjeningDtoMap = opptjeningResponse.getOpptjeningData();

        assertEquals(beholdning.getInntektOpptjeningBelop().getSumPensjonsgivendeInntekt().getBelop().intValue(), opptjeningDtoMap.get(1980).getPensjonsgivendeInntekt());
    }

    /* Tests for PDL Response */
    @Test
    void when_PdlResponse_not_contains_foedselsdato_then_use_foedselsaar_from_pdl_instead() {
        Integer expectedFoedselsaar = 1970;

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(null, expectedFoedselsaar));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());

        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(new ArrayList<>());

        opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(LocalDate.now()));

        assertThat(yearCaptor.getValue() - 13, is(expectedFoedselsaar));
    }

    @Test
    void when_PdlResponse_contains_foedselsdato_then_use_foedselsaar_from_pdl_foedselsdato() {
        int expectedFoedselsaar = 1970;

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenReturn(createPdlResponseForFoedselsdato(LocalDate.of(expectedFoedselsaar, 8, 9), 1990));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(any(String.class))).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(new ArrayList<>());

        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(new ArrayList<>());

        opptjeningProvider.calculateOpptjeningForFnr(PidGenerator.generatePid(LocalDate.now()));

        assertThat(yearCaptor.getValue() - 13, is(expectedFoedselsaar));
    }

    @Test
    void when_Call_to_PDL_fails_then_use_foedselsaar_from_fnr_instead() {
        Pid pid = PidGenerator.generatePid(LocalDate.of(1964, 7, 6));

        when(pdlConsumer.getPdlResponse(any(PdlRequest.class))).thenThrow(new FailedCallingExternalServiceException("", ""));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(pid.getPid())).thenReturn(new ArrayList<>());
        when(personConsumer.getAfpHistorikkForPerson(pid.getPid())).thenReturn(new AfpHistorikk());
        when(personConsumer.getUforeHistorikkForPerson(pid.getPid())).thenReturn(new UforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(pid.getPid())).thenReturn(new ArrayList<>());

        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(new ArrayList<>());

        opptjeningProvider.calculateOpptjeningForFnr(pid);

        assertThat(yearCaptor.getValue() - 13, is(pid.getFodselsdato().getYear()));
    }

    private static PdlResponse createPdlResponseForFoedselsdato(LocalDate foedselsdato, Integer foedselsaar) {
        var pdlResponse = new PdlResponse();
        var pdlData = new PdlData();
        var hentPersonResponse = new HentPersonResponse();
        var foedsel = new Foedsel();
        foedsel.setFoedselsdato(foedselsdato);
        foedsel.setFoedselsaar(foedselsaar);
        hentPersonResponse.setFoedsel(List.of(foedsel));
        pdlData.setHentPerson(hentPersonResponse);
        pdlResponse.setData(pdlData);
        return pdlResponse;
    }
}