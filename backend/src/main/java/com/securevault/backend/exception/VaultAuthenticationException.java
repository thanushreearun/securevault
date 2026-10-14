package com.securevault.backend.exception;

public class VaultAuthenticationException extends RuntimeException {

    public VaultAuthenticationException(String message) {
        super(message);
    }
}