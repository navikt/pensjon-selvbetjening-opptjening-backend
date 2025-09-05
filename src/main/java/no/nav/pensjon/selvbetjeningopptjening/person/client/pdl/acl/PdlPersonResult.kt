package no.nav.pensjon.selvbetjeningopptjening.person.client.pdl.acl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDate

/**
 * Anti-corruption layer.
 */
data class PdlPersonResult(
    val data: PdlPersonEnvelope?,
    val extensions: PdlExtensions?,
    val errors: List<PdlError>?
)

data class PdlPersonEnvelope(val hentPerson: PdlPerson?) // null if person not found

data class PdlPerson(
    val navn: List<PdlNavn>?,
    val foedselsdato: List<PdlFoedselsdato>?,
    val sivilstand: List<PdlSivilstand>?,
    val adressebeskyttelse: List<PdlAdressebeskyttelse>?
)

data class PdlNavn(
    val fornavn: String? = null,
    val mellomnavn: String? = null,
    val etternavn: String? = null
)

data class PdlFoedselsdato(val foedselsdato: PdlDate?)

data class PdlSivilstand(val type: String?)

data class PdlAdressebeskyttelse(val gradering: String?)

data class PdlError(val message: String?)

data class PdlExtensions(val warnings: List<PdlWarning>?)

data class PdlWarning(
    val query: String?,
    val id: String?,
    val code: String?,
    val message: String?,
    val details: Any?
)

data class PdlDate(val value: LocalDate) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
        val converter = DateScalarConverter()

        @JsonCreator
        @JvmStatic
        fun create(rawValue: String) = PdlDate(converter.toScalar(rawValue))
    }
}
