package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.PidGenerator;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.BirthDate;
import no.nav.pensjon.selvbetjeningopptjening.common.domain.Person;
import no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.OpptjeningsgrunnlagConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonsbeholdning.PensjonsbeholdningConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.pensjonspoeng.PensjonspoengConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.person.PersonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.restpensjon.RestpensjonConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.uttaksgrad.UttaksgradConsumer;
import no.nav.pensjon.selvbetjeningopptjening.model.code.OpptjeningTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.dto.OpptjeningResponse;
import no.nav.pensjon.selvbetjeningopptjening.person.PersonService;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.pensjon.selvbetjeningopptjening.PidGenerator.generatePid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpptjeningProviderTest {

    private static final LocalDate DATE_IN_1960 = LocalDate.of(1960, 7, 6);
    private static final LocalDate DATE_IN_1963 = LocalDate.of(1963, 7, 6);
    private OpptjeningProvider opptjeningProvider;

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
    private PersonService personService;
    @Captor
    private ArgumentCaptor<Integer> yearCaptor;

    @BeforeEach
    void setUp() {
        opptjeningProvider = new OpptjeningProvider(
                pensjonsbeholdningConsumer,
                opptjeningsgrunnlagConsumer,
                pensjonspoengConsumer,
                restpensjonConsumer,
                personConsumer,
                personService,
                uttaksgradConsumer);
    }

    @Test
    void when_UserGroup5_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1968;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        when(personService.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4))).thenReturn(new Person(
                PidGenerator.generatePid(fodselsdato),
                null,
                null,
                null,
                new BirthDate(fodselsdato)));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_UserGroup4_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1956;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(emptyList());
        when(personService.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4))).thenReturn(new Person(
                PidGenerator.generatePid(fodselsdato),
                null,
                null,
                null,
                new BirthDate(fodselsdato)));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_UserGroup123_then_set_fodselsaar_on_response() {
        int expectedFodselsaar = 1950;
        LocalDate fodselsdato = LocalDate.of(expectedFodselsaar, 7, 6);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(emptyList());
        mockPerson(fodselsdato);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getFodselsaar(), is(expectedFodselsaar));
    }

    @Test
    void when_Fnr_UserGroup5_with_beholdning_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning() {
        Beholdning beholdning = beholdningFomFirstDayInYear(1980);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(1, opptjeningerByYear.size());
        assertEquals(beholdning.getBelop(), opptjeningerByYear.get(1980).getPensjonsbeholdning().doubleValue());
    }

    @Test
    void when_UserGroup5_with_and_FomDato_1983_then_calculateOpptjeningForFnr_returns_1_OpptjeningDto_with_Pensjonsbeholdning_and_3_OpptjeningDto_WithNoOpptjening() {
        Beholdning beholdning = beholdningFomFirstDayInYear(1983);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(4, opptjeningerByYear.size());
        assertEquals(beholdning.getBelop(), opptjeningerByYear.get(1983).getPensjonsbeholdning().doubleValue());
    }

    @Test
    void when_UserGroup4_with_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Pensjonsbeholdning() {
        Beholdning beholdning = beholdningFomFirstDayInYear(1983);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(emptyList());
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(beholdning.getBelop(), opptjeningerByYear.get(1983).getPensjonsbeholdning().doubleValue());
    }

    /*  Tests with pensjonspoeng for user group 1, 2, 3 and 4 */
    @Test
    void when_UserGroup4_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        var pensjonspoeng = new Pensjonspoeng(1980, "OSFE", 10D, null, null);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(singletonList(pensjonspoeng));
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        OpptjeningDto opptjening = opptjeningerByYear.get(pensjonspoeng.getYear());
        assertEquals(pensjonspoeng.getType(), opptjening.getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjening.getOmsorgspoeng());
    }

    @Test
    void when_UserGroup4_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        var pensjonspoeng = new Pensjonspoeng(1980, "PPI", 10D, inntekt(), null);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(singletonList(pensjonspoeng));
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        OpptjeningDto opptjening = opptjeningerByYear.get(pensjonspoeng.getYear());
        assertEquals(pensjonspoeng.getInntekt().getBelop(), opptjening.getPensjonsgivendeInntekt().longValue());
        assertEquals(pensjonspoeng.getPoeng(), opptjening.getPensjonspoeng());
    }

    @Test
    void when_UserGroup4_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        var pensjonspoeng1 = new Pensjonspoeng(1980, "OSFE", 10D, null, null);
        var pensjonspoeng2 = new Pensjonspoeng(1981, "OSFE", 20D, null, null);
        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng1, pensjonspoeng2);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(pensjonspoengList);
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        assertEquals(pensjonspoengList.size(), opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    @Test
    void when_UserGroup123_with_PensjonspoengType_OSFE_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Omsorgspoeng() {
        LocalDate fodselsdato = LocalDate.of(1950, 7, 6);
        var pensjonspoeng = new Pensjonspoeng(1970, "OSFE", 10D, null, null);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(singletonList(pensjonspoeng));
        mockPerson(fodselsdato);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        OpptjeningDto opptjening = opptjeningerByYear.get(pensjonspoeng.getYear());
        assertEquals(pensjonspoeng.getType(), opptjening.getOmsorgspoengType());
        assertEquals(pensjonspoeng.getPoeng(), opptjening.getOmsorgspoeng());
    }

    @Test
    void when_UserGroup123_with_PensjonspoengType_PPI_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        LocalDate fodselsdato = LocalDate.of(1944, 7, 6);
        var pensjonspoeng = new Pensjonspoeng(1960, "PPI", 10D, inntekt(), null);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(singletonList(pensjonspoeng));
        mockPerson(fodselsdato);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        OpptjeningDto opptjening = opptjeningerByYear.get(pensjonspoeng.getYear());
        assertEquals(pensjonspoeng.getInntekt().getBelop(), opptjening.getPensjonsgivendeInntekt().longValue());
        assertEquals(pensjonspoeng.getPoeng(), opptjening.getPensjonspoeng());
    }

    @Test
    void when_UserGroup123_with_2_Pensjonspoeng_then_calculateOpptjeningForFnr_returns_NumberOfYearsWithPensjonpoeng_2() {
        LocalDate fodselsdato = LocalDate.of(1940, 7, 6);
        var pensjonspoeng1 = new Pensjonspoeng(1960, "OSFE", 10D, null, null);
        var pensjonspoeng2 = new Pensjonspoeng(1963, "OSFE", 20D, inntekt(), null);
        List<Pensjonspoeng> pensjonspoengList = Arrays.asList(pensjonspoeng1, pensjonspoeng2);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(pensjonspoengList);
        mockPerson(fodselsdato);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        assertEquals(pensjonspoengList.size(), opptjeningResponse.getNumberOfYearsWithPensjonspoeng());
    }

    /*  Restpensjon tests */
    @Test
    void when_Fnr_UserGroup5_with_Restpensjon_and_no_Uttaksgrad_then_calculateOpptjeningForFnr_returns_no_OpptjeningDto() {
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        Restpensjon restpensjon = restpensjonWithGrunnpensjon(1980);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningerByYear.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestPensjonstillegg_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        Restpensjon restpensjon = restpensjonWithPensjonstillegg();
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestPensjonstillegg(), opptjeningerByYear.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestTilleggspensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        Restpensjon restpensjon = restpensjonWithTilleggspensjon();
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestTilleggspensjon(), opptjeningerByYear.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup5_with_RestPensjonstillegg_and_RestGrunnpensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        Restpensjon restpensjon = restpensjonWithGrunnpensjonAndPensjonstillegg();
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestPensjonstillegg() + restpensjon.getRestGrunnpensjon(), opptjeningerByYear.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup4_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        Restpensjon restpensjon = restpensjonWithGrunnpensjon(1980);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningerByYear.get(1980).getRestpensjon());
    }

    @Test
    void when_Fnr_UserGroup123_with_RestGrunnPensjon_and_Uttaksgrad_less_than_100_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_Restpensjon() {
        LocalDate fodselsdato = LocalDate.of(1950, 7, 6);
        Restpensjon restpensjon = restpensjonWithGrunnpensjon(1970);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(singletonList(uttaksgrad()));
        when(restpensjonConsumer.getRestpensjonListe(any(String.class))).thenReturn(singletonList(restpensjon));
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        mockPerson(fodselsdato);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(fodselsdato), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(restpensjon.getRestGrunnpensjon(), opptjeningerByYear.get(1970).getRestpensjon());
    }

    /* Tests for inntekt for user group 5 */
    @Test
    void when_Fnr_UserGroup5_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        var inntektsopptjening = new Inntektsopptjening(1980, null, inntekt());
        Beholdning beholdning = beholdningFomFirstDayInYear(1980, inntektsopptjening);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(emptyList());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(beholdning.getInntektsopptjening().getSumPensjonsgivendeInntekt().getBelop(), opptjeningerByYear.get(1980).getPensjonsgivendeInntekt().longValue());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_without_InntektType_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        Inntekt inntekt = inntekt("");
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(singletonList(inntekt));
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_and_InntektType_without_Beholdning_then_calculateOpptjeningForFnr_returns_no_OpptjeningData() {
        Inntekt inntekt = inntekt("SUM_PI");
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(singletonList(inntekt));
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertNull(opptjeningResponse.getOpptjeningData());
    }

    @Test
    void when_Fnr_UserGroup5_with_Inntekt_and_InntektType_SUM_PI_and_Beholdning_then_calculateOpptjeningForFnr_returns_OpptjeningData_with_PensjonsgivendeInntekt() {
        Inntekt inntekt = inntekt("SUM_PI");
        Beholdning beholdning = beholdningFomFirstDayInYear(1981);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(singletonList(inntekt));
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertEquals(inntekt.getBelop(), opptjeningResponse.getOpptjeningData().get(1980).getPensjonsgivendeInntekt().longValue());
    }

    @Test
    void when_pid_UserGroup5_with_beholdning_years_without_inntekt_then_inntekt_should_be_default_0() {
        long expectedInntekt2010 = 16987L;
        long expectedInntekt2012 = 1200L;
        Inntekt additionalInntekt2010 = new Inntekt(2010, "SUM_PI", expectedInntekt2010);
        Beholdning beholdning2011 = beholdningFomFirstDayInYear(2011);
        Inntekt inntekt2012 = new Inntekt(2012, "SUM_PI", expectedInntekt2012);
        var inntektsopptjening = new Inntektsopptjening(2012, (double) expectedInntekt2012, inntekt2012);
        Beholdning beholdning2012 = beholdningFomFirstDayInYear(2012, inntektsopptjening);
        Beholdning beholdning2013 = beholdningFomFirstDayInYear(2013);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(List.of(beholdning2011, beholdning2012, beholdning2013));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(singletonList(additionalInntekt2010));
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getOpptjeningData().get(2010).getPensjonsgivendeInntekt().longValue(), is(expectedInntekt2010));
        assertThat(opptjeningResponse.getOpptjeningData().get(2011).getPensjonsgivendeInntekt(), is(0));
        assertThat(opptjeningResponse.getOpptjeningData().get(2012).getPensjonsgivendeInntekt().longValue(), is(expectedInntekt2012));
        assertThat(opptjeningResponse.getOpptjeningData().get(2013).getPensjonsgivendeInntekt(), is(0));
    }

    @Test
    void when_pid_UserGroup4_default_inntekt_should_not_overwrite_already_set_inntekt() {
        long expectedInntekt2012 = 1200L;
        long expectedInntekt2013 = 1600L;
        Beholdning beholdning2011 = beholdningFomFirstDayInYear(2011);
        Inntekt inntekt2012 = new Inntekt(2012, "SUM_PI", expectedInntekt2012);
        var inntektsopptjening = new Inntektsopptjening(2012, (double) expectedInntekt2012, inntekt2012);
        Beholdning beholdning2012 = beholdningFomFirstDayInYear(2012, inntektsopptjening);
        Beholdning beholdning2013 = beholdningFomFirstDayInYear(2013);
        Pensjonspoeng pensjonspoeng2013 = pensjonspoengForYearWithInntekt(2013, expectedInntekt2013);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(List.of(beholdning2011, beholdning2012, beholdning2013));
        mockPerson(DATE_IN_1960);
        when(pensjonspoengConsumer.getPensjonspoengListe(any(String.class))).thenReturn(singletonList(pensjonspoeng2013));

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getOpptjeningData().get(2011).getPensjonsgivendeInntekt(), is(0));
        assertThat(opptjeningResponse.getOpptjeningData().get(2012).getPensjonsgivendeInntekt().longValue(), is(expectedInntekt2012));
        assertThat(opptjeningResponse.getOpptjeningData().get(2013).getPensjonsgivendeInntekt().longValue(), is(expectedInntekt2013));
    }

    @Test
    void when_pid_UserGroup5_with_with_no_inntekt_on_last_two_years_then_pensjonsgivendeInntekt_should_be_null() {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;
        int yearWithInntekt = currentYear - 2;
        int yearWithoutInntekt = currentYear - 3;
        long expectedInntekt = 1200L;

        Beholdning beholdningYearWithoutInntekt = beholdningFomFirstDayInYear(yearWithoutInntekt);

        var inntekt = new Inntekt(yearWithInntekt, "SUM_PI", expectedInntekt);
        var inntektsopptjening = new Inntektsopptjening(yearWithInntekt, (double) expectedInntekt, inntekt);
        Beholdning beholdningYearWithInntekt = beholdningFomFirstDayInYear(yearWithInntekt, inntektsopptjening);
        Beholdning beholdningLastYear = beholdningFomFirstDayInYear(lastYear);
        Beholdning beholdningThisYear = beholdningFomFirstDayInYear(currentYear);

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class)))
                .thenReturn(List.of(beholdningYearWithoutInntekt, beholdningYearWithInntekt, beholdningLastYear, beholdningThisYear));
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        mockPerson(DATE_IN_1963);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1963), LoginSecurityLevel.LEVEL4);

        assertThat(opptjeningResponse.getOpptjeningData().get(yearWithoutInntekt).getPensjonsgivendeInntekt(), is(0));
        assertThat(opptjeningResponse.getOpptjeningData().get(yearWithInntekt).getPensjonsgivendeInntekt().longValue(), is(expectedInntekt));
        assertNull(opptjeningResponse.getOpptjeningData().get(lastYear).getPensjonsgivendeInntekt());
        assertNull(opptjeningResponse.getOpptjeningData().get(currentYear).getPensjonsgivendeInntekt());
    }

    /* Tests for beholdning with inntekt for user group 4 */
    @Test
    void when_Fnr_UserGroup4_with_InntektOpptjeningBelop_and_FomDato_1980_then_calculateOpptjeningForFnr_returns_OpptjeningDto_with_PensjonsgivendeInntekt() {
        var inntektsopptjening = new Inntektsopptjening(1980, null, inntekt());
        Beholdning beholdning = beholdningFomFirstDayInYear(1980, inntektsopptjening);
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(singletonList(beholdning));
        mockPerson(DATE_IN_1960);

        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(generatePid(DATE_IN_1960), LoginSecurityLevel.LEVEL4);

        Map<Integer, OpptjeningDto> opptjeningerByYear = opptjeningResponse.getOpptjeningData();
        assertEquals(beholdning.getInntektsopptjening().getSumPensjonsgivendeInntekt().getBelop(), opptjeningerByYear.get(1980).getPensjonsgivendeInntekt().longValue());
    }

    /* Tests for PDL response */

    @Test
    void when_PdlResponse_contains_foedselsdato_then_use_foedselsaar_from_pdl_foedselsdato() {
        int expectedFoedselsaar = 1970;
        when(personService.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4))).thenReturn(new Person(
                PidGenerator.generatePid(LocalDate.of(1990, 1, 1)),
                null,
                null,
                null,
                new BirthDate(LocalDate.of(expectedFoedselsaar, 8, 9))));
        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(any(String.class))).thenReturn(emptyList());
        when(personConsumer.getUforeHistorikkForPerson(any(String.class))).thenReturn(uforeHistorikk());
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(any(String.class))).thenReturn(emptyList());
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), yearCaptor.capture(), anyInt())).thenReturn(emptyList());

        opptjeningProvider.calculateOpptjeningForFnr(generatePid(LocalDate.now()), LoginSecurityLevel.LEVEL4);

        assertThat(yearCaptor.getValue() - 13, is(expectedFoedselsaar));
    }

    private void mockPerson(LocalDate fodselsdato) {
        when(personService.getPerson(any(Pid.class), eq(LoginSecurityLevel.LEVEL4))).thenReturn(
                new Person(PidGenerator.generatePid(fodselsdato),
                        null,
                        null,
                        null,
                        new BirthDate(fodselsdato)));
    }

    private static Beholdning beholdningFomFirstDayInYear(int year) {
        return beholdningFomFirstDayInYear(year, null);
    }

    private static Beholdning beholdningFomFirstDayInYear(int year, Inntektsopptjening inntektsopptjening) {
        return new Beholdning(
                null, "", "", "", 100D, null,
                LocalDate.of(year, 1, 1),
                null, null, null, null, null,
                "", null,
                inntektsopptjening,
                null, null, null, null);
    }

    private static Inntekt inntekt(String type) {
        return new Inntekt(1980, type, 200L);
    }

    private static Inntekt inntekt() {
        return inntekt("");
    }

    private static Restpensjon restpensjonWithGrunnpensjon(int year) {
        return new Restpensjon(LocalDate.of(year, 1, 1), 100D, null, null);
    }

    private static Restpensjon restpensjonWithPensjonstillegg() {
        return new Restpensjon(LocalDate.of(1980, 1, 1), null, null, 100D);
    }

    private static Restpensjon restpensjonWithGrunnpensjonAndPensjonstillegg() {
        return new Restpensjon(LocalDate.of(1980, 1, 1), 100D, null, 100D);
    }

    private static Restpensjon restpensjonWithTilleggspensjon() {
        return new Restpensjon(LocalDate.of(1980, 1, 1), null, 100D, null);
    }

    private static Uttaksgrad uttaksgrad() {
        return new Uttaksgrad(
                null,
                50,
                LocalDate.MAX,
                null);
    }

    private static AfpHistorikk afpHistorikk() {
        return new AfpHistorikk(LocalDate.MIN, LocalDate.MAX);
    }

    private static UforeHistorikk uforeHistorikk() {
        return new UforeHistorikk(emptyList());
    }

    private static Pensjonspoeng pensjonspoengForYearWithInntekt(int year, long inntekt) {
        return new Pensjonspoeng(year, OpptjeningTypeCode.PPI.toString(), 1d, new Inntekt(year, "SUM_PI", inntekt), new Omsorg(""));
    }
}
