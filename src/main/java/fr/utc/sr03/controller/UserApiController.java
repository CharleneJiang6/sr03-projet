package fr.utc.sr03.controller;

import fr.utc.sr03.model.*;
import fr.utc.sr03.model.dto.*;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.PasswordService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserApiController {
    @Resource
    private UserService userService;

    @Resource
    private PasswordService passwordService;

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

    // Authenticate an existing user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        var result = userService.loginUser(body.email(), body.password());
        if (result.isPresent()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).
                body(new ApiResponse("Identifiants invalides")
                );
    }


    @PostMapping
    public ResponseEntity<?> createRegularUser(@RequestBody CreateUserRequest body) {
        try {
            UserDTO createdUser = userService.createRegularUser(
                    body.firstname(),
                    body.lastname(),
                    body.email(),
                    body.password()
            ).orElseThrow(() -> new RuntimeException("Impossible de créer l'utilisateur."));

            return ResponseEntity.ok(createdUser);
        } catch (UserService.EmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse(e.getMessage(), HttpStatus.CONFLICT.value())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAdminUser(@RequestBody CreateUserRequest body) {
        Optional<UserDTO> createdAdmin = userService.createAdminUser(
                body.firstname(),
                body.lastname(),
                body.email(),
                body.password()
        );
        if (createdAdmin.isPresent()) {
            return ResponseEntity.ok(createdAdmin.get());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse("Impossible de créer l'utilisateur admin.", HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    // Update user info: all field is taken into account, except the password
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody UserUpdateRequest body
    ) {
        User updatedUser = userService.updateUser(id, body);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }

    // Specific endpoint to update the password only
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Integer id,
            @RequestBody PasswordUpdateRequest request
    ) {
        try {
            User updatedUser = userService.updatePassword(id, request.password());
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(
                    new ApiResponse("Mot de passe mis à jour avec succès.", 200)
            );
        } catch (UserService.InvalidPasswordException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        }
    }


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
}
