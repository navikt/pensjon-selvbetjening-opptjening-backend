package no.nav.pensjon.selvbetjeningopptjening.tech.web

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UriUtilTest {

    @Test
    fun `formatAsUri produces a URI given scheme, authority, path`() {
        val uri = UriUtil.formatAsUri("scheme1", "authority1", "path1")
        assertEquals("scheme1://authority1/path1", uri)
    }

    @Test
    fun `formatAsUri produces a URI given scheme, authority, but no path`() {
        val uri = UriUtil.formatAsUri("scheme1", "authority1", "")
        assertEquals("scheme1://authority1", uri)
    }
}
