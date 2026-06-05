package fr.utc.sr03.controller;

import fr.utc.sr03.model.ApiResponse;
import fr.utc.sr03.model.User;
import fr.utc.sr03.model.UserDTO;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.PasswordService;
import fr.utc.sr03.services.JakartaEmail;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserApiController {
    @Resource
    private UserService userService;

    @Resource
    private PasswordService passwordService;

    // GET

    // GET /api/users?admin=true&activated=true&firstname=...&lastname=...&email=...
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) Boolean admin,
            @RequestParam(required = false) Boolean activated,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {
        List<User> users = userService.searchUsers(admin, activated, firstname, lastname, email);
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).toList();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(user));
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.getUserByMail(email)
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Preserve the unicity of email addresses added to the database
        User existingUser = userService.searchUsers(
                null,
                null,
                null,
                null,
                user.getMail()
        ).stream().findFirst().orElse(null);
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(
                            "Le courriel '" + user.getMail() + "' est déjà utilisé. Veuillez en saisir un autre.",
                            400
                    )
            );
        }

        // Test the password security
        String passwordValidation = passwordService.validatePasswordSecurity(user.getPassword());
        if (!passwordValidation.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(passwordValidation, 400)
            );
        }

        // Encrypt the password before saving
        user.setPassword(passwordService.encryptPassword(user.getPassword()));
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(new UserDTO(createdUser));
    }

    // PATCH

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates
    ) {
        User updatedUser = userService.updateUser(id, updates);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        // First, test the password security
        String password = body.get("password");
        String passwordValidation = passwordService.validatePasswordSecurity(password);
        if (!passwordValidation.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(
                            "Le mot de passe ne respecte pas les règles de sécurité.",
                            400
                    )
            );
        }

        // Then, encrypt the password and store it in the database
        User user = userService.updatePassword(id, passwordService.encryptPassword(password));
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                new ApiResponse("Mot de passe mis à jour avec succès.", 200)
        );
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.deleteUserById(id);
        if (!deleted) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(
                            "Aucun utilisateur n'existe pour pour l'ID " + id,
                            400
                    )
            );
        }
        return ResponseEntity.ok(
                new ApiResponse("Utilisateur " + id + " supprimé avec succès.", 200)
        );
    }

    // TEST

    @GetMapping(value = "/testmail")
    public void testmail() {
        //Test envoi Mail
        JakartaEmail jakartaEmail = new JakartaEmail();
        jakartaEmail.sendMail();
    }


}
