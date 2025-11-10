package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.mapping

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.model.Foedselsdato
import no.nav.pensjon.selvbetjeningopptjening.person.Foedselsdato2

object PdlFoedselsdatoMapper {

    fun fromDtos(list: List<Foedselsdato?>?): List<Foedselsdato2> =
        list.orEmpty().mapNotNull(::foedselsdato)

    private fun foedselsdato(source: Foedselsdato?): Foedselsdato2? =
        source?.foedselsdato?.let(::Foedselsdato2) ?: source?.foedselsaar?.let(::Foedselsdato2)
}