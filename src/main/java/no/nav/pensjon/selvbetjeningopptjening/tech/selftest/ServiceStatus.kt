package no.nav.pensjon.selvbetjeningopptjening.tech.selftest

enum class ServiceStatus(val code: Int, val color: String) {

    DOWN(1, "red"),
    UP(0, "green")
}
