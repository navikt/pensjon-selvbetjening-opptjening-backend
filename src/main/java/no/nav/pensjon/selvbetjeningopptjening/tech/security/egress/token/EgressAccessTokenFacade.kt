package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.token

import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.AuthType
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.enriched
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.obo.OnBehalfOfTokenService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.oauth2.tokenexchange.TokenExchangeService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class EgressAccessTokenFacade(
    private val clientCredentialsTokenService: ClientCredentialsEgressTokenService,
    private val tokenExchangeService: TokenExchangeService,
    private val onBehalfOfTokenService: OnBehalfOfTokenService
) {
    fun getAccessToken(audience: String, ingressToken: String?, tokenExchangeIsSupported: Boolean): RawJwt =
        tokenGetter(tokenExchangeIsSupported).getEgressToken(ingressToken, audience, user = "")

    private fun tokenGetter(tokenExchangeIsSupported: Boolean): EgressTokenGetter =
        when (authType()) {
            AuthType.PERSON_SELF -> if (tokenExchangeIsSupported) tokenExchangeService else clientCredentialsTokenService
            AuthType.REPRESENTANT -> clientCredentialsTokenService
            //AuthType.NAV_ANSATT -> onBehalfOfTokenService
            AuthType.NAV_ANSATT -> clientCredentialsTokenService //TODO use onBehalfOfTokenService when frontend sends OBO (exchanged Entra) token
            AuthType.NAV_MACHINE -> clientCredentialsTokenService
        }

    companion object {
        private fun authType(): AuthType =
            SecurityContextHolder.getContext().authentication.enriched().authType
    }
}
