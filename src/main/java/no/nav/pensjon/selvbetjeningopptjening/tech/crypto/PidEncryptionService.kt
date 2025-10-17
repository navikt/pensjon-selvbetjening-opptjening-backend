package no.nav.pensjon.selvbetjeningopptjening.tech.crypto

import mu.KotlinLogging
import no.nav.pensjon.selvbetjeningopptjening.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import kotlin.collections.toTypedArray
import kotlin.text.contains
import kotlin.text.isEmpty
import kotlin.text.isNullOrEmpty
import kotlin.text.split
import kotlin.text.substring
import kotlin.text.toByteArray
import kotlin.text.toRegex

/**
 * Based on https://github.com/navikt/pid-encryption/
 */
@Service
class PidEncryptionService(
    private val client: PidEncryptionClient,
    @Value("\${pid.encryption.key.new}") newKey: String,
    @Value("\${pid.encryption.key.old}") oldKey: String
) {

    private val log = KotlinLogging.logger {}

    private val newKeyPair = KeyPair.fromString(newKey)
    private val oldKeyPair = KeyPair.fromString(oldKey)

    val publicKey: String
        get() = newKeyPair.exportablePublicKey

    fun encrypt(value: String?): String {
        try {
            if (value.isNullOrEmpty()) {
                throw kotlin.RuntimeException("String is null or empty")
            }

            val encryptedBytes = encryptionCipher().doFinal(value.toByteArray(StandardCharsets.UTF_8))

            // Prepend the key ID, so when we later try to decrypt the string, we know which decryption key to use:
            return newKeyPair.keyId + "." + base64(encryptedBytes)
        } catch (e: NoSuchPaddingException) {
            throw kotlin.RuntimeException("Unsupported encryption padding", e)
        } catch (e: IllegalBlockSizeException) {
            throw kotlin.RuntimeException("Illegal block size for decryption - ${displayableValue(value!!)}", e)
        } catch (e: NoSuchAlgorithmException) {
            throw kotlin.RuntimeException("Unsupported encryption encoding", e)
        } catch (e: BadPaddingException) {
            throw kotlin.RuntimeException("Bad encryption padding", e)
        } catch (e: InvalidKeyException) {
            throw kotlin.RuntimeException("Invalid public key for encryption", e)
        }
    }

    fun decrypt(encrypted: String?): String {
        try {
            if (encrypted == null) {
                throw kotlin.RuntimeException("Encrypted string is null; expected <key id>.<encrypted string>")
            }

            if (!encrypted.contains(".")) {
                throw kotlin.RuntimeException("Encrypted string is not on the expected format <key id>.<encrypted string>")
            }

            val parts = encrypted.split("\\.".toRegex(), limit = 2).toTypedArray()
            val keyId = parts[0]
            val encryptedBase64 = parts[1]

            if (keyId.isEmpty()) {
                throw kotlin.RuntimeException("Key ID in encrypted string is null or empty. Expected a string on the format <key id>.<encrypted string>")
            }

            if (encryptedBase64.isEmpty()) {
                throw kotlin.RuntimeException("Encrypted string is null or empty. Expected a string on the format <key id>.<encrypted string>")
            }

            val decryptedBytes = decryptionCipher(keyId).doFinal(Base64.getUrlDecoder().decode(encryptedBase64))
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: NoSuchPaddingException) {
            throw kotlin.RuntimeException("Unsupported encryption padding", e)
        } catch (e: IllegalBlockSizeException) {
            throw kotlin.RuntimeException("Illegal block size for decryption (too long encrypted string?)", e)
        } catch (e: NoSuchAlgorithmException) {
            throw kotlin.RuntimeException("Unsupported encryption encoding", e)
        } catch (e: BadPaddingException) {
            throw kotlin.RuntimeException("Bad encryption padding", e)
        } catch (e: InvalidKeyException) {
            throw kotlin.RuntimeException("The private key for decryption is invalid", e)
        }
    }

    fun decryptPid(encryptedPid: String?): String? {
        if (isEncrypted(encryptedPid)) {
            try {
                val decryptedPid = client.decrypt(encryptedPid)
                return decryptedPid
            } catch (e: EgressException) {
                log.error("Pid decryption failed for " + encryptedPid + " : " + e.message)
                throw kotlin.RuntimeException(e)
            }
        }
        else return encryptedPid
    }

    private fun isEncrypted(value: String?): Boolean =
        !value.isNullOrEmpty() && value.contains(".")

    private fun encryptionCipher(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, newKeyPair.publicKey)
        return cipher
    }

    private fun decryptionCipher(keyId: String): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val keyPair = if (keyId == newKeyPair.keyId) newKeyPair else oldKeyPair
        cipher.init(Cipher.DECRYPT_MODE, keyPair.privateKey)
        return cipher
    }

    private companion object {
        private const val TRANSFORMATION = "RSA"

        private fun base64(bytes: ByteArray?): String =
            Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

        private fun displayableValue(value: String): String {
            val length = value.length

            return if (length > 12)
                "${value.substring(0, 3)}...${value.substring(length - 3, length)} - length $length"
            else
                "length $length"
        }
    }
}
