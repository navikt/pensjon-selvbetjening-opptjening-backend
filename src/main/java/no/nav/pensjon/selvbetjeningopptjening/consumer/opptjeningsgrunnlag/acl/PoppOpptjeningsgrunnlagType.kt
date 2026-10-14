package no.nav.pensjon.selvbetjeningopptjening.consumer.opptjeningsgrunnlag.acl

import no.nav.pensjon.selvbetjeningopptjening.opptjening.grunnlag.OpptjeningsgrunnlagType

enum class PoppOpptjeningsgrunnlagType(val internalValue: OpptjeningsgrunnlagType) {

    DAGPENGER(internalValue = OpptjeningsgrunnlagType.DAGPENGER),
    FORST_TJENESTE(internalValue = OpptjeningsgrunnlagType.FOERSTEGANGSTJENESTE),
    INNTEKT(internalValue = OpptjeningsgrunnlagType.INNTEKT),
    OMSORG(internalValue = OpptjeningsgrunnlagType.OMSORG);

    companion object {
        private val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: OpptjeningsgrunnlagType): PoppOpptjeningsgrunnlagType =
            valuesByInternal[value]
                ?: throw IllegalArgumentException("internal value $value not supported")
    }
}