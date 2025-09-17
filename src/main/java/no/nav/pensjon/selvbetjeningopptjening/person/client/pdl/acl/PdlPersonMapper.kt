package no.nav.pensjon.selvbetjeningopptjening.person.client.pdl.acl

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.common.exception.NotFoundException
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.person.NavnFormatter.formatNavn
import no.nav.pensjon.selvbetjeningopptjening.person.Person2
import no.nav.pensjon.selvbetjeningopptjening.person.Sivilstand
import java.time.LocalDate

object PdlPersonMapper {
    private val log = KotlinLogging.logger {}

    fun fromDto(dto: PdlPersonResult): Person2 =
        dto.data?.hentPerson?.let(::person)
            ?: throw NotFoundException("person").also { logError(dto) }

    private fun person(dto: PdlPerson) =
        Person2(
            navn = dto.navn.orEmpty().let(::fromDto) ?: "",
            foedselsdato = dto.foedselsdato.orEmpty().let(::fromDto) ?: LocalDate.MIN,
            sivilstand = dto.sivilstand.orEmpty().let(::fromDto),
            adressebeskyttelse = dto.adressebeskyttelse.orEmpty().let(::fromDto)
        )

    private fun fromDto(dto: List<PdlAdressebeskyttelse>): AdressebeskyttelseGradering =
        PdlAdressebeskyttelseGradering.fromExternalValue(dto.firstOrNull()?.gradering).internalValue

    private fun fromDto(dto: List<PdlFoedselsdato>): LocalDate? = dto.firstOrNull()?.foedselsdato?.value

    private fun fromDto(dto: List<PdlNavn>): String? =
        dto.firstOrNull()?.let { formatNavn(it.fornavn, it.mellomnavn, it.etternavn) }

    private fun fromDto(dto: List<PdlSivilstand>): Sivilstand =
        PdlSivilstandType.fromExternalValue(dto.firstOrNull()?.type).internalValue

    private fun logError(dto: PdlPersonResult) {
        dto.errors?.firstOrNull()?.message?.let {
            log.info { "PDL error message: $it" }
        }
    }
}
