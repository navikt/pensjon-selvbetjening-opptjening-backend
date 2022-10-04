package no.nav.pensjon.selvbetjeningopptjening.security.aad;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2Scopes;

import static no.nav.pensjon.selvbetjeningopptjening.util.net.UriUtil.formatAsUri;

/**
 * Utilities related to Azure Active Directory.
 */
public final class AzureAdUtil {

    private static final String APPLICATION_URI_SCHEME = "api";

    /**
     * Gets the default application scope in the form "api://<cluster>.<namespace>.<app-name>/.default",
     * ref. https://doc.nais.io/security/auth/azure-ad/concepts/#scopes
     */
    public static String getDefaultScope(String fullyQualifiedApplicationName) {
        return formatAsApplicationIdUri(fullyQualifiedApplicationName) + "/" + Oauth2Scopes.AAD_DEFAULT;
    }

    /**
     * Gets the application ID URI for a fully qualified application name of the form "<cluster>:<namespace>:<app-name>".
     * The URI is of the form "api://<cluster>.<namespace>.<app-name>".
     * The fully qualified application name is referred to as 'application display name' in Azure AD.
     */
    private static String formatAsApplicationIdUri(String fullyQualifiedApplicationName) {
        return formatAsUri(
                APPLICATION_URI_SCHEME,
                fullyQualifiedApplicationName.replace(":", "."),
                "");
    }

    private AzureAdUtil() {
    }
}
