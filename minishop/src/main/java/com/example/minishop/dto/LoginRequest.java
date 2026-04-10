package com.example.minishop.dto;

public record LoginRequest(
        String username,
        String password
) {}