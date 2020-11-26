package no.nav.pensjon.selvbetjeningopptjening.security.crypto;

public interface Crypto {

    String encrypt(String value) throws CryptoException;

    String decrypt(String value) throws CryptoException;
}
