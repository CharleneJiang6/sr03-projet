package fr.utc.sr03.model.dto;

public record CreateUserRequest(
        String firstname,
        String lastname,
        String email,
        String password
) {}
