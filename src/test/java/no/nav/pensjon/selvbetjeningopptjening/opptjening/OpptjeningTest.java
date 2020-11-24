package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpptjeningTest {

    @Test
    void hasMethods_return_true_when_correspondingValue_is_nonNull() {
        Opptjening opptjening = filledOpptjening();
        assertTrue(opptjening.hasPensjonspoeng());
        assertTrue(opptjening.hasOmsorgspoeng());
        assertTrue(opptjening.hasPensjonsbeholdning());
        assertTrue(opptjening.hasPensjonsgivendeInntekt());
        assertTrue(opptjening.hasRestpensjon());
    }

    @Test
    void hasMethods_return_false_when_correspondingValue_is_null() {
        Opptjening opptjening = nullOpptjening();
        assertFalse(opptjening.hasPensjonspoeng());
        assertFalse(opptjening.hasOmsorgspoeng());
        assertFalse(opptjening.hasPensjonsbeholdning());
        assertFalse(opptjening.hasPensjonsgivendeInntekt());
        assertFalse(opptjening.hasRestpensjon());
    }

    @Test
    void hasMerknad_is_true_when_merknad_present() {
        var opptjening = new Opptjening(1L, 1D);
        opptjening.addMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
        assertTrue(opptjening.hasMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING));
    }

    @Test
    void hasMerknad_is_false_when_merknad_not_present() {
        var opptjening = new Opptjening(1L, 1D);
        assertFalse(opptjening.hasMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING));
    }

    @Test
    void isNotPositive_is_true_when_all_opptjeningValues_are_zeroOrLess() {
        assertTrue(opptjening(0L, 0D, 0L).isNotPositive());
        assertTrue(opptjening(-1L, -.1D, -1L).isNotPositive());
    }

    @Test
    void isNotPositive_is_false_when_atLeastOne_opptjeningValue_is_greaterThanZero() {
        assertFalse(opptjening(1L, 0D, 0L).isNotPositive());
        assertFalse(opptjening(0L, .1D, 0L).isNotPositive());
        assertFalse(opptjening(0L, 0D, 1L).isNotPositive());
        assertFalse(opptjening(-100L, 50D, 100L).isNotPositive());
    }

    @Test
    void test_isOmsorgspoengLessThanOrEqualToPensjonspoeng() {
        assertFalse(poengOpptjening(null, null).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
        assertFalse(poengOpptjening(1D, null).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
        assertFalse(poengOpptjening(null, 1D).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
        assertFalse(poengOpptjening(1D, 1.1D).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
        assertTrue(poengOpptjening(1D, 1D).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
        assertTrue(poengOpptjening(1.1D, 1D).isOmsorgspoengLessThanOrEqualToPensjonspoeng());
    }

    private static Opptjening filledOpptjening() {
        Opptjening opptjening = opptjening(1L, 2.1D, 2L);
        opptjening.setRestpensjon(3.1D);
        opptjening.setMaxUforegrad(3);
        opptjening.setOmsorgspoeng(4.1D);
        opptjening.setOmsorgspoengType("poengtype");
        return opptjening;
    }

    private static Opptjening poengOpptjening(Double pensjonspoeng, Double omsorgspoeng) {
        Opptjening opptjening = opptjening(0L, pensjonspoeng, 0L);
        opptjening.setOmsorgspoeng(omsorgspoeng);
        return opptjening;
    }

    private static Opptjening opptjening(long inntekt, Double pensjonspoeng, long beholdning) {
        var opptjening = new Opptjening(inntekt, pensjonspoeng);
        opptjening.setPensjonsbeholdning(beholdning);
        return opptjening;
    }

    private static Opptjening nullOpptjening() {
        return new Opptjening(null, null);
    }
}
