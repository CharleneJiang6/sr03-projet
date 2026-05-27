package fr.utc.sr03.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordService {

    /**
     * Encrypt a password using BCrypt algorithm
     * @param password The plain text password
     * @return The encrypted password
     */
    public String encryptPassword(String password) {
        String passwordEncrypted = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println(passwordEncrypted); // TODO: debug to comment
        return passwordEncrypted;
    }

    /**
     * Validate password security requirements
     * @param password The password to validate
     * @return Error messages if validation fails, empty string if valid
     */
    public String validatePasswordSecurity(String password) {
        List<String> messages = new ArrayList<>();

        if (password.length() < 8) {
            messages.add("Le mot de passe doit contenir au moins 8 caractères.");
        }
        if (!password.matches(".*[A-Z].*")) {
            messages.add("Le mot de passe doit contenir au moins une lettre majuscule.");
        }
        if (!password.matches(".*[a-z].*")) {
            messages.add("Le mot de passe doit contenir au moins une lettre minuscule.");
        }
        if (!password.matches(".*\\d.*")) {
            messages.add("Le mot de passe doit contenir au moins un chiffre.");
        }
        if (!password.matches(".*[-_+|!@#$%^&*():;/~].*")) {
            messages.add("Le mot de passe doit contenir au moins un caractère spécial (ex: -_+|!@#$%^&*():;/~).");
        }

        return String.join(" ", messages);
    }

    /**
     * Verify if a plain password matches an encrypted password
     * @param plainPassword The plain text password
     * @param encryptedPassword The encrypted password
     * @return True if they match, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), encryptedPassword).verified;
    }
}
