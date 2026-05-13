package fr.utc.sr03.controller;

import fr.utc.sr03.model.User;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.JakartaEmail;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserApiController {
    @Resource
    private UserService userService;

    // GET

    // GET /api/users?admin=true&activated=true&firstname=...&lastname=...&email=...
    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) Boolean admin,
            @RequestParam(required = false) Boolean activated,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {
        List<User> users = userService.searchUsers(admin, activated, firstname, lastname, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userService.getUserByMail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // PATCH

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates
    ) {
        User updatedUser = userService.updateUser(id, updates);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        User user = userService.updatePassword(id, body.get("newPassword"));
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        boolean deleted = userService.deleteUserById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // TEST

    @PostMapping(value = "/createTest")
    public void createTest() {
        User user = new User(
                "Cédric", "Martinet", "cedric.martinet@utc.fr", "1234"
        );
        userService.createUser(user);
    }

    @GetMapping(value = "/testmail")
    public void testmail() {
        //Test envoi Mail
        JakartaEmail jakartaEmail = new JakartaEmail();
        jakartaEmail.sendMail();
    }


}
