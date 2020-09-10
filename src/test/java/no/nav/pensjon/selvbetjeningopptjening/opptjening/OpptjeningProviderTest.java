package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
    void When_Fnr_UserGroup5_then_calculateOpptjeningForFnr_returns() {
        String fnr = "06076323304";
        //List<Uttaksgrad> uttaksgradList = List.of(new Uttaksgrad());
        List<Uttaksgrad> uttaksgradList = new ArrayList<>();

       // uttaksgradList.get(0).setUttaksgrad(100);
        AfpHistorikk afphistorikk = new AfpHistorikk();
        UforeHistorikk uforehistorikk = new UforeHistorikk();
        List<Beholdning> beholdningList = List.of(new Beholdning());
        List<Inntekt> inntektList = new ArrayList<>();

        when(uttaksgradConsumer.getAlderSakUttaksgradhistorikkForPerson(fnr)).thenReturn(uttaksgradList);
        when(personConsumer.getAfpHistorikkForPerson(fnr)).thenReturn(afphistorikk);
        when(personConsumer.getUforeHistorikkForPerson(fnr)).thenReturn(uforehistorikk);
        when(pensjonsbeholdningConsumer.getPensjonsbeholdning(fnr)).thenReturn(beholdningList);
        when(opptjeningsgrunnlagConsumer.getInntektListeFromOpptjeningsgrunnlag(any(String.class), anyInt(), anyInt())).thenReturn(inntektList);
        OpptjeningResponse opptjeningResponse = opptjeningProvider.calculateOpptjeningForFnr(fnr);

       // assertThrows(NullPointerException.class,() -> opptjeningProvider.calculateOpptjeningForFnr(fnr));
    }
}