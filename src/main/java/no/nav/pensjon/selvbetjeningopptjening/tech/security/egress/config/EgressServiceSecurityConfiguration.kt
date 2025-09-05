package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.config

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.EgressAccessTokenFacade
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token.RawJwt
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.function.Function

@Configuration
open class EgressServiceSecurityConfiguration {

    @Bean
    open fun egressServiceListsByAudience(
        @Value("\${pen.app-id}") pensjonsfagligKjerneServiceId: String,
        @Value("\${pdl.app-id}") persondataServiceId: String,
        @Value("\${pensjon.representasjon.app-id}") representasjonServiceId: String,
        @Value("\${skjermede-personer-pip-app-id}") skjermingServiceId: String,
        @Value("\${pensjonsopptjening-register-app-id}") opptjeningServiceId: String,
    ) =
        EgressServicesByAudience(
            mapOf(
                pensjonsfagligKjerneServiceId to EgressService.PENSJONSFAGLIG_KJERNE,
                persondataServiceId to EgressService.PERSONDATA,
                representasjonServiceId to EgressService.PENSJON_REPRESENTASJON,
                skjermingServiceId to EgressService.SKJERMEDE_PERSONER,
                opptjeningServiceId to EgressService.PENSJONSOPPTJENING,
            )
        )

    @Bean
    open fun egressTokenSuppliersByService(
        serviceListsByAudience: EgressServicesByAudience,
        egressTokenGetter: EgressAccessTokenFacade
    ): EgressTokenSuppliersByService {
        val suppliersByService: MutableMap<EgressService, Function<String?, RawJwt>> =
            EnumMap(EgressService::class.java)

        serviceListsByAudience.entries.forEach { (audience, service) ->
            obtainTokenSupplier(
                audience,
                service,
                egressTokenGetter,
                tokenSuppliersByService = suppliersByService
            )
        }

        return EgressTokenSuppliersByService(suppliersByService)
    }

    companion object {

        private fun obtainTokenSupplier(
            audience: String,
            service: EgressService,
            egressTokenGetter: EgressAccessTokenFacade,
            tokenSuppliersByService: MutableMap<EgressService, Function<String?, RawJwt>>
        ) {
            val tokenSupplier = Function<String?, RawJwt> {
                egressTokenGetter.getAccessToken(
                    audience,
                    ingressToken = it,
                    tokenExchangeIsSupported = service.supportsTokenExchange
                )
            }

            tokenSuppliersByService[service] = tokenSupplier
        }
    }
}
