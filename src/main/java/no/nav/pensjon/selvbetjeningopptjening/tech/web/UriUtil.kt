package no.nav.pensjon.selvbetjeningopptjening.tech.web

import org.springframework.util.StringUtils.hasText
import java.lang.String.format

object UriUtil {

    /**
     * Ref. URI Generic Syntax, https://datatracker.ietf.org/doc/html/rfc3986#section-3
     */
    fun formatAsUri(scheme: String, authority: String, path: String): String =
        if (hasText(path)) format("%s://%s/%s", scheme, authority, path)
        else format("%s://%s", scheme, authority)
}
