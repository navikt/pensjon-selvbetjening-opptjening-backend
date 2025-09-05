package no.nav.pensjon.selvbetjeningopptjening.tech.trace

fun interface CallIdGenerator {
    fun newId(): String
}
