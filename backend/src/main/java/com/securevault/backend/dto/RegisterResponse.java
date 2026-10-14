package com.securevault.backend.dto;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RegisterResponse() {
    }

    public RegisterResponse(
            Long id,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}