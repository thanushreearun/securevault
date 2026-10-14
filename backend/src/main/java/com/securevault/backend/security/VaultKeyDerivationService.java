package com.securevault.backend.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class VaultKeyDerivationService {

    private static final int SALT_SIZE_BYTES = 16;
    private static final int KEY_SIZE_BYTES = 32;

    private static final int MEMORY_KB = 64 * 1024;
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 2;

    private final SecureRandom secureRandom = new SecureRandom();

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE_BYTES];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public byte[] deriveKey(
            char[] masterPassword,
            byte[] salt) {

        if (masterPassword == null || masterPassword.length == 0) {
            throw new IllegalArgumentException(
                    "Master password must not be empty"
            );
        }

        if (salt == null || salt.length < SALT_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "Salt must contain at least 16 bytes"
            );
        }

        byte[] passwordBytes = null;

        try {
            passwordBytes = new String(masterPassword)
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);

            Argon2Parameters.Builder builder =
                    new Argon2Parameters.Builder(
                            Argon2Parameters.ARGON2_id
                    )
                    .withSalt(salt)
                    .withMemoryAsKB(MEMORY_KB)
                    .withIterations(ITERATIONS)
                    .withParallelism(PARALLELISM);

            Argon2BytesGenerator generator =
                    new Argon2BytesGenerator();

            generator.init(builder.build());

            byte[] derivedKey = new byte[KEY_SIZE_BYTES];

            generator.generateBytes(
                    passwordBytes,
                    derivedKey
            );

            return derivedKey;

        } finally {
            if (passwordBytes != null) {
                java.util.Arrays.fill(passwordBytes, (byte) 0);
            }

            java.util.Arrays.fill(masterPassword, '\0');
        }
    }
}