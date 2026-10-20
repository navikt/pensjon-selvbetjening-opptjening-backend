package no.nav.pensjon.selvbetjeningopptjening.api.merknad.v1.acl

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknad
import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknader

object MerknadMapper {

    fun transferable(source: Merknader) =
        MerknaderV1(
            merknaderPerAar = source.perAar.map { (aar, merknader) ->
                aar to merknader.mapNotNull(::transferableKode)
            }.toMap()
        )

    private fun transferableKode(merknad: Merknad): MerknadCodeV1? =
        MerknadCodeV1.fromInternalValue(merknad.kode).let {
            if (it == MerknadCodeV1.UNKNOWN) null else it
        }
}