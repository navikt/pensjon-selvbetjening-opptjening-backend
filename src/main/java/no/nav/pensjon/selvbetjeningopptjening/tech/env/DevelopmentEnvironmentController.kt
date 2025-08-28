package no.nav.pensjon.selvbetjeningopptjening.tech.env

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class DevelopmentEnvironmentController {

    @GetMapping("devenv")
    @Hidden
    fun entraEnvironment(): String = environmentVariable("AZURE_APP_CLIENT_SECRET")

    private companion object {
        private fun environmentVariable(name: String) =
            if (System.getenv("NAIS_CLUSTER_NAME") == "dev-gcp")
                System.getenv(name)
            else "forbidden"
    }
}
