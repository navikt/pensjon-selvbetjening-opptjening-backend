package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader

object ResultMapper {

    fun transferable(source: Merknader) =
        MerknaderV1(
            merknaderPerAar = source.perAar.map { (aar, code) ->
                aar to code.map(MerknadCodeV1::fromInternalValue)
            }.toMap()
        )
}