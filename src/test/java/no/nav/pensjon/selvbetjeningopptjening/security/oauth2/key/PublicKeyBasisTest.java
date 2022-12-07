package no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublicKeyBasisTest {

    @Test
    void certificateBased_constructs_instance_containing_certificate() {
        PublicKeyBasis basis = PublicKeyBasis.certificateBased("id1", "use1", "cert1");

        assertEquals("id1", basis.getKeyId());
        assertEquals("use1", basis.getUse());
        assertEquals("cert1", basis.getCertificate());
        assertEquals("", basis.getExponent());
        assertEquals("", basis.getModulus());
        assertTrue(basis.hasCertificate());
    }

    @Test
    void modulusBased_constructs_instance_containing_exponentAndModulus() {
        PublicKeyBasis basis = PublicKeyBasis.modulusBased("id1", "use1", "exp1", "mod1");

        assertEquals("id1", basis.getKeyId());
        assertEquals("use1", basis.getUse());
        assertEquals("", basis.getCertificate());
        assertEquals("exp1", basis.getExponent());
        assertEquals("mod1", basis.getModulus());
        assertFalse(basis.hasCertificate());
    }
}
