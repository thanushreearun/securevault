package com.securevault.backend.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class VaultCryptoService {

    private static final String AES_ALGORITHM = "AES";
    private static final String GCM_TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int AES_KEY_SIZE_BYTES = 32;
    private static final int IV_SIZE_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionResult encrypt(
            String plaintext,
            byte[] encryptionKey) {

        try {
            if (encryptionKey.length != AES_KEY_SIZE_BYTES) {
                throw new IllegalArgumentException(
                        "Encryption key must be 256 bits"
                );
            }

            byte[] iv = new byte[IV_SIZE_BYTES];
            secureRandom.nextBytes(iv);

            SecretKeySpec keySpec =
                    new SecretKeySpec(encryptionKey, AES_ALGORITHM);

            Cipher cipher =
                    Cipher.getInstance(GCM_TRANSFORMATION);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH_BITS,
                            iv
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] ciphertext =
                    cipher.doFinal(
                            plaintext.getBytes(StandardCharsets.UTF_8)
                    );

            return new EncryptionResult(
                    Base64.getEncoder().encodeToString(ciphertext),
                    Base64.getEncoder().encodeToString(iv)
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to encrypt vault data",
                    exception
            );
        }
    }

    public String decrypt(
            String ciphertext,
            String encodedIv,
            byte[] encryptionKey) {

        try {
            if (encryptionKey.length != AES_KEY_SIZE_BYTES) {
                throw new IllegalArgumentException(
                        "Encryption key must be 256 bits"
                );
            }

            byte[] encryptedBytes =
                    Base64.getDecoder().decode(ciphertext);

            byte[] iv =
                    Base64.getDecoder().decode(encodedIv);

            SecretKeySpec keySpec =
                    new SecretKeySpec(encryptionKey, AES_ALGORITHM);

            Cipher cipher =
                    Cipher.getInstance(GCM_TRANSFORMATION);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH_BITS,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] plaintext =
                    cipher.doFinal(encryptedBytes);

            return new String(
                    plaintext,
                    StandardCharsets.UTF_8
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to decrypt vault data",
                    exception
            );
        }
    }

    public record EncryptionResult(
            String ciphertext,
            String iv
    ) {}
}