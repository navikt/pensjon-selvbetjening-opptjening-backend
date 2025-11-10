package no.nav.pensjon.selvbetjeningopptjening.person

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import no.nav.pensjon.selvbetjeningopptjening.tech.security.masking.Masker.maskFnr
import java.time.LocalDate

class Person(
    val pid: Pid,
    val fornavn: String? = null,
    val mellomnavn: String? = null,
    val etternavn: String? = null,
    val foedselsdato: Foedselsdato2? = null
) {
    private val log = KotlinLogging.logger {}

    constructor(pid: Pid, foedselsdato: Foedselsdato2?) : this(
        pid,
        fornavn = null,
        mellomnavn = null,
        etternavn = null,
        foedselsdato
    )

    constructor(pid: Pid) : this(pid, fornavn = null, mellomnavn = null, etternavn = null, foedselsdato = null)

    fun getFodselsdato(): LocalDate =
        getFodselsdato(foedselsdato, pid)

    private fun getFodselsdato(foedselsdato: Foedselsdato2?, pid: Pid): LocalDate =
        foedselsdato?.value ?: defaultFoedselsdato(pid).also {
            log.warn { "No birthdates found for PID ${maskFnr(pid.pid)}" }
        }

    /**
     * Note: In rare cases this method returns the wrong date, since
     * the first 6 digits of the fødselsnummer is not always the birthdate
     */
    private fun defaultFoedselsdato(pid: Pid): LocalDate =
        pid.getFodselsdato().also { log.info { "Deriving birthdate directly from PID" } }
}