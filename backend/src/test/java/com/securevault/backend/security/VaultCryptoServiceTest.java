package com.securevault.backend.security;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class VaultCryptoServiceTest {

    private final VaultCryptoService cryptoService =
            new VaultCryptoService();

    @Test
    void encryptAndDecryptShouldReturnOriginalText() {

        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        String original =
                "fake-development-password-123";

        VaultCryptoService.EncryptionResult encrypted =
                cryptoService.encrypt(
                        original,
                        key
                );

        String decrypted =
                cryptoService.decrypt(
                        encrypted.ciphertext(),
                        encrypted.iv(),
                        key
                );

        assertEquals(original, decrypted);
    }

    @Test
    void encryptionShouldProduceDifferentCiphertextEachTime() {

        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        String plaintext =
                "fake-development-password-123";

        VaultCryptoService.EncryptionResult first =
                cryptoService.encrypt(
                        plaintext,
                        key
                );

        VaultCryptoService.EncryptionResult second =
                cryptoService.encrypt(
                        plaintext,
                        key
                );

        assertNotEquals(
                first.ciphertext(),
                second.ciphertext()
        );

        assertNotEquals(
                first.iv(),
                second.iv()
        );
    }

    @Test
    void wrongKeyShouldNotDecrypt() {

        byte[] correctKey = new byte[32];
        byte[] wrongKey = new byte[32];

        SecureRandom random = new SecureRandom();

        random.nextBytes(correctKey);
        random.nextBytes(wrongKey);

        String plaintext =
                "fake-development-password-123";

        VaultCryptoService.EncryptionResult encrypted =
                cryptoService.encrypt(
                        plaintext,
                        correctKey
                );

        assertThrows(
                IllegalStateException.class,
                () -> cryptoService.decrypt(
                        encrypted.ciphertext(),
                        encrypted.iv(),
                        wrongKey
                )
        );
    }
}