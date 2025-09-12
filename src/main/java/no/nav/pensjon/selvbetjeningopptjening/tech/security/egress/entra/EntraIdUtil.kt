package no.nav.pensjon.selvbetjeningopptjening.tech.security.egress.entra

import no.nav.pensjon.selvbetjeningopptjening.tech.web.UriUtil.formatAsUri
import kotlin.text.replace

object EntraIdUtil {

    private const val APPLICATION_URI_SCHEME = "api"

    /**
     * Ref. https://learn.microsoft.com/en-us/entra/identity-platform/scopes-oidc#the-default-scope
     */
    private const val ENTRA_ID_DEFAULT_SCOPE = ".default"

    /**
     * Gets the default application scope in the form "api://<cluster>.<namespace>.<app-name>/.default",
     * ref. https://doc.nais.io/security/auth/azure-ad/concepts/#scopes
     */
    fun getDefaultScope(fullyQualifiedApplicationName: String): String =
        "${formatAsApplicationIdUri(fullyQualifiedApplicationName)}/$ENTRA_ID_DEFAULT_SCOPE"

    /**
     * Gets the application ID URI for a fully qualified application name of the form "<cluster>:<namespace>:<app-name>".
     * The URI is of the form "api://<cluster>.<namespace>.<app-name>".
     * The fully qualified application name is referred to as 'application display name' in Azure AD.
     */
    private fun formatAsApplicationIdUri(fullyQualifiedApplicationName: String): String =
        formatAsUri(
            scheme = APPLICATION_URI_SCHEME,
            authority = fullyQualifiedApplicationName.replace(":", "."),
            path = ""
        )
}
