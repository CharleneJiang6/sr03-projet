package fr.utc.sr03.controller;


import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.User;
import fr.utc.sr03.services.ChannelService;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.JakartaEmail;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

    @Resource
    private UserService userService;

    @Resource
    private ChannelService channelService;

    // region [User Actions Endpoints]

    @GetMapping(value = "/Users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/admins")
    public List<User> getAdmins() {
        return userService.getAdminUsers();
    }

    @GetMapping("/users/active")
    public List<User> getActiveUsers() {
        return userService.getActiveUsers();
    }

    @GetMapping("/users/inactive")
    public List<User> getInactiveUsers() {
        return userService.getInactiveUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userService.getUserByMail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/search")
    public List<User> searchUsers(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {
        return userService.searchUsers(firstname, lastname, email);
    }

    // TODO: maybe check how to do with DTO/DAO
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable int id,
            @RequestBody Map<String, Object> updates
    ) {
        User updatedUser = userService.updateUser(id, updates);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable int id) {
        User user = userService.setActivated(id, true);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable int id) {
        User user = userService.setActivated(id, false);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    //optional
    @PatchMapping("/users/{id}/roles/admin")
    public ResponseEntity<User> grantAdmin(@PathVariable int id) {
        User user = userService.setAdmin(id, true);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    //optional
    @PatchMapping("/users/{id}/roles/regular")
    public ResponseEntity<User> removeAdmin(@PathVariable int id) {
        User user = userService.setAdmin(id, false);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable int id,
            @RequestBody Map<String, String> body
    ) {
        User user = userService.updatePassword(id, body.get("newPassword"));
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUserById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // endregion


    // region [Channel Actions Endpoints]
    @GetMapping("/channels")
    public List<Channel> getChannels(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        return channelService.getChannels(userId, type, status);
    }

    @GetMapping("/channels/search")
    public List<Channel> searchChannels(@RequestParam String title) {
        return channelService.searchByTitle(title);
    }

    @GetMapping("/channels/{channelId}")
    public ResponseEntity<Channel> getChannelById(@PathVariable int channelId) {
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channel);
    }

    @PostMapping("/channels")
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        Channel createdChannel = channelService.saveChannel(channel);
        return ResponseEntity.ok(createdChannel);
    }

    @PatchMapping("/channels/{channelId}")
    public ResponseEntity<Channel> updateChannel(
            @PathVariable int channelId,
            @RequestBody Map<String, Object> updates
    ) {
        Channel updatedChannel = channelService.updateChannel(channelId, updates);
        if (updatedChannel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedChannel);
    }

    @PatchMapping("/channels/{channelId}/expiration")
    public ResponseEntity<Channel> updateExpirationDate(
            @PathVariable int channelId,
            @RequestBody Map<String, String> body
    ) {
        LocalDateTime expirationDate = LocalDateTime.parse(body.get("expirationDate"));

        Channel channel = channelService.updateExpirationDate(channelId, expirationDate);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(channel);
    }

    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable int channelId) {
        boolean deleted = channelService.deleteChannelById(channelId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region [Test Only Endpoints]
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
    // endregion

}
