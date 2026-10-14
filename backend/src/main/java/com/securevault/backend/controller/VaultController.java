package com.securevault.backend.controller;

import com.securevault.backend.dto.VaultData;
import com.securevault.backend.entity.Vault;
import com.securevault.backend.service.VaultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createVault(
            Authentication authentication,
            @RequestHeader("X-Vault-Master-Password")
            String masterPassword) {

        Vault vault =
                vaultService.createVaultForUser(
                        authentication.getName(),
                        masterPassword.toCharArray()
                );

        return ResponseEntity.ok(
                Map.of(
                        "id", vault.getId(),
                        "version", vault.getVersion(),
                        "createdAt", vault.getCreatedAt()
                )
        );
    }

    @PostMapping("/unlock")
    public ResponseEntity<VaultData> unlockVault(
            Authentication authentication,
            @RequestHeader("X-Vault-Master-Password")
            String masterPassword) {

        VaultData vaultData =
                vaultService.decryptVaultForUser(
                        authentication.getName(),
                        masterPassword.toCharArray()
                );

        return ResponseEntity.ok(vaultData);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getVault(
            Authentication authentication) {

        Vault vault =
                vaultService.getVaultForUser(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                Map.of(
                        "id", vault.getId(),
                        "version", vault.getVersion(),
                        "createdAt", vault.getCreatedAt(),
                        "updatedAt", vault.getUpdatedAt()
                )
        );
    }
}