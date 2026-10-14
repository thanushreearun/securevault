package com.securevault.backend.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VaultKeyDerivationServiceTest {

    private final VaultKeyDerivationService keyService =
            new VaultKeyDerivationService();

    @Test
    void samePasswordAndSaltShouldProduceSameKey() {

        char[] password1 = "fake-master-password".toCharArray();
        char[] password2 = "fake-master-password".toCharArray();

        byte[] salt = keyService.generateSalt();

        byte[] key1 =
                keyService.deriveKey(password1, salt);

        byte[] key2 =
                keyService.deriveKey(password2, salt);

        assertArrayEquals(key1, key2);

        Arrays.fill(key1, (byte) 0);
        Arrays.fill(key2, (byte) 0);
    }

    @Test
    void differentSaltShouldProduceDifferentKey() {

        char[] password1 = "fake-master-password".toCharArray();
        char[] password2 = "fake-master-password".toCharArray();

        byte[] salt1 = keyService.generateSalt();
        byte[] salt2 = keyService.generateSalt();

        byte[] key1 =
                keyService.deriveKey(password1, salt1);

        byte[] key2 =
                keyService.deriveKey(password2, salt2);

        assertFalse(Arrays.equals(key1, key2));

        Arrays.fill(key1, (byte) 0);
        Arrays.fill(key2, (byte) 0);
    }

    @Test
    void differentPasswordShouldProduceDifferentKey() {

        char[] password1 = "fake-master-password-one".toCharArray();
        char[] password2 = "fake-master-password-two".toCharArray();

        byte[] salt = keyService.generateSalt();

        byte[] key1 =
                keyService.deriveKey(password1, salt);

        byte[] key2 =
                keyService.deriveKey(password2, salt);

        assertFalse(Arrays.equals(key1, key2));

        Arrays.fill(key1, (byte) 0);
        Arrays.fill(key2, (byte) 0);
    }

    @Test
    void derivedKeyShouldBe256Bits() {

        char[] password =
                "fake-master-password".toCharArray();

        byte[] salt =
                keyService.generateSalt();

        byte[] key =
                keyService.deriveKey(
                        password,
                        salt
                );

        assertEquals(32, key.length);

        Arrays.fill(key, (byte) 0);
    }

    @Test
    void saltShouldBeRandom() {

        byte[] salt1 =
                keyService.generateSalt();

        byte[] salt2 =
                keyService.generateSalt();

        assertEquals(16, salt1.length);
        assertEquals(16, salt2.length);

        assertFalse(Arrays.equals(salt1, salt2));
    }
}