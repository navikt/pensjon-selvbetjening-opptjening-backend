package no.nav.pensjon.selvbetjeningopptjening.tech.crypto

import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.text.isEmpty
import kotlin.text.split
import kotlin.text.toRegex

class KeyPair private constructor(
    val keyId: String,
    val exportablePublicKey: String,
    val publicKey: PublicKey,
    val privateKey: PrivateKey
) {
    companion object {
        private const val ALGORITHM = "RSA"

        fun fromString(key: String): KeyPair {
            val keyParts = key.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val keyId = keyParts[0]
            val publicKey = keyParts[1]
            val privateKey = keyParts[2]
            val exportablePublicKey = "$keyId.$publicKey"

            try {
                val urlDecoder = Base64.getUrlDecoder()
                val publicKeyBytes = urlDecoder.decode(publicKey)
                val privateKeyBytes = urlDecoder.decode(privateKey)
                val keyFactory = KeyFactory.getInstance(ALGORITHM)

                return KeyPair(
                    keyId,
                    exportablePublicKey,
                    keyFactory.generatePublic(X509EncodedKeySpec(publicKeyBytes)),
                    keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))
                )
            } catch (e: InvalidKeySpecException) {
                throw kotlin.RuntimeException("Failed to load encryption keys for encryption", e)
            } catch (e: NoSuchAlgorithmException) {
                throw kotlin.RuntimeException("Failed to load encryption keys for encryption", e)
            }
        }
    }
}
