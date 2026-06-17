package fr.utc.sr03.model.dto;

public record UserUpdateRequest(
        String firstname,
        String lastname,
        String mail,
        Boolean activated,
        Boolean admin
) {}
