package no.nav.pensjon.selvbetjeningopptjening.person

object FoedselsnummerUtil {
    private val FOEDSELSNUMMER_REGEX = """[0-9]{2}([0-9]{4})[0-9]{5}""".toRegex()

    fun redact(value: String) =
        FOEDSELSNUMMER_REGEX.replace(value, "**$1*****")
}