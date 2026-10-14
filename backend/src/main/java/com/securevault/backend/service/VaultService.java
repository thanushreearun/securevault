package com.securevault.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securevault.backend.dto.VaultData;
import com.securevault.backend.dto.VaultEntry;
import com.securevault.backend.entity.User;
import com.securevault.backend.entity.Vault;
import com.securevault.backend.repository.UserRepository;
import com.securevault.backend.repository.VaultRepository;
import com.securevault.backend.security.VaultCryptoService;
import com.securevault.backend.security.VaultKeyDerivationService;
import com.securevault.backend.exception.VaultAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VaultService {

    private final VaultRepository vaultRepository;
    private final UserRepository userRepository;
    private final VaultCryptoService cryptoService;
    private final VaultKeyDerivationService keyDerivationService;
    private final ObjectMapper objectMapper;

    public VaultService(
            VaultRepository vaultRepository,
            UserRepository userRepository,
            VaultCryptoService cryptoService,
            VaultKeyDerivationService keyDerivationService,
            ObjectMapper objectMapper) {

        this.vaultRepository = vaultRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.keyDerivationService = keyDerivationService;
        this.objectMapper = objectMapper;
    }

    public Vault createVaultForUser(
            String email,
            char[] masterPassword) {

        User user = findUser(email);

        if (vaultRepository.existsByUser(user)) {
            throw new IllegalStateException(
                    "Vault already exists"
            );
        }

        byte[] salt =
                keyDerivationService.generateSalt();

        byte[] encryptionKey = null;

        try {
            encryptionKey =
                    keyDerivationService.deriveKey(
                            masterPassword,
                            salt
                    );

            VaultData emptyVault =
                    new VaultData();

            String plaintext =
                    objectMapper.writeValueAsString(
                            emptyVault
                    );

            VaultCryptoService.EncryptionResult encrypted =
                    cryptoService.encrypt(
                            plaintext,
                            encryptionKey
                    );

            Vault vault = new Vault();

            vault.setUser(user);
            vault.setEncryptedData(
                    encrypted.ciphertext()
            );
            vault.setSalt(salt);

            vault.setIv(
                    java.util.Base64.getDecoder()
                            .decode(encrypted.iv())
            );

            vault.setVersion(1);

            LocalDateTime now =
                    LocalDateTime.now();

            vault.setCreatedAt(now);
            vault.setUpdatedAt(now);

            return vaultRepository.save(vault);

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Unable to serialize vault",
                    exception
            );

        } finally {

            if (encryptionKey != null) {
                java.util.Arrays.fill(
                        encryptionKey,
                        (byte) 0
                );
            }

            java.util.Arrays.fill(
                    masterPassword,
                    '\0'
            );
        }
    }

    public VaultData decryptVaultForUser(String email, char[] masterPassword) {
    User user = findUser(email);

    Vault vault = vaultRepository.findByUser(user)
            .orElseThrow(() -> new IllegalArgumentException("Vault not found"));

    byte[] encryptionKey = null;

    try {
        encryptionKey = keyDerivationService.deriveKey(
                masterPassword,
                vault.getSalt()
        );

        final String plaintext;

        try {
            plaintext = cryptoService.decrypt(
                    vault.getEncryptedData(),
                    java.util.Base64.getEncoder().encodeToString(vault.getIv()),
                    encryptionKey
            );
        } catch (IllegalStateException exception) {
            throw new VaultAuthenticationException(
                    "Invalid vault master password"
            );
        }

        try {
            return objectMapper.readValue(
                    plaintext,
                    VaultData.class
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to deserialize vault",
                    exception
            );
        }

    } finally {
        if (encryptionKey != null) {
            java.util.Arrays.fill(encryptionKey, (byte) 0);
        }

        java.util.Arrays.fill(masterPassword, '\0');
    }
}

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));
    }

    public Vault getVaultForUser(String email) {

        User user = findUser(email);

        return vaultRepository.findByUser(user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vault not found"
                        ));
}
}