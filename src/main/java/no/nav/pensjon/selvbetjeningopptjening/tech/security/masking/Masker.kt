package no.nav.pensjon.selvbetjeningopptjening.tech.security.masking

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid

object Masker {

    private const val NORMAL_FNR_LENGTH: Int = 11
    private const val END_INDEX_OF_BIRTH_DATE_PART_OF_FNR: Int = 6
    private val controlCharacterRegex = "\\p{Cntrl}".toRegex()

    fun maskFnr(pid: Pid?): String =
        pid?.let { maskFnr(it.pid) } ?: "null"

    fun maskFnr(rawFnr: String?): String {
        if (rawFnr == null) return "null"

        val fnr: String = rawFnr.replace(controlCharacterRegex, "")

        return if (fnr.length == NORMAL_FNR_LENGTH)
            fnr.substring(0, END_INDEX_OF_BIRTH_DATE_PART_OF_FNR) + "*****"
        else
            String.format("****** (length %d)", fnr.length)
    }
}