package no.nav.pensjon.selvbetjeningopptjening.person

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid
import java.time.LocalDate

class Person(
    val pid: Pid,
    val fornavn: String? = null,
    val mellomnavn: String? = null,
    val etternavn: String? = null,
    val foedselsdato: Foedselsdato2? = null
) {
    constructor(pid: Pid, foedselsdato: Foedselsdato2?) : this(
        pid,
        fornavn = null,
        mellomnavn = null,
        etternavn = null,
        foedselsdato
    )

    constructor(pid: Pid) : this(pid, fornavn = null, mellomnavn = null, etternavn = null, foedselsdato = null)

    fun getFodselsdato(): LocalDate =
        foedselsdato(foedselsdato, pid)

    private fun foedselsdato(foedselsdato: Foedselsdato2?, pid: Pid): LocalDate =
        foedselsdato?.value
            ?: throw RuntimeException("Fødselsdato ikke funnet for PID ${FoedselsnummerUtil.redact(pid.pid)}")
}