package no.nav.pensjon.selvbetjeningopptjening.logging

data class LogMessage(
    val type: String,
    val jsonContent: Object
)
