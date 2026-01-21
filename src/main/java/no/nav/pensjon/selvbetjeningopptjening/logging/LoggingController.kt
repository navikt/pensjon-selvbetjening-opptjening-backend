package no.nav.pensjon.selvbetjeningopptjening.logging

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class LoggingController {
    private val log = KotlinLogging.logger {}

    @PutMapping(path = ["logg"])
    fun log(@RequestBody message: LogMessage): ResponseEntity<Any> {
        val type: String = message.type

        if ("info".equals(type, ignoreCase = true))
            log.info { "info ${message.jsonContent}" }
        else if ("error".equals(type, ignoreCase = true))
            log.error { "error ${message.jsonContent}" }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
