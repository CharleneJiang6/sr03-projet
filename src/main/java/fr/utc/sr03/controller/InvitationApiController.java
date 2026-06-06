package fr.utc.sr03.controller;

import fr.utc.sr03.model.Invitation;
import fr.utc.sr03.services.InvitationService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/invitations")
public class InvitationApiController {

    @Resource
    private InvitationService invitationService;

    @GetMapping
    public List<Invitation> getAllInvitations() {
        return invitationService.getAllInvitations();
    }

    @GetMapping("/received/{userId}")
    public List<Invitation> getReceivedInvitations(@PathVariable Integer userId) {
        return invitationService.getReceivedInvitations(userId);
    }

    @GetMapping("/sent/{userId}")
    public List<Invitation> getSentInvitations(@PathVariable Integer userId) {
        return invitationService.getSentInvitations(userId);
    }

    @PostMapping
    public ResponseEntity<?> createInvitation(@RequestBody Map<String, Integer> body) {
        Integer senderId = body.get("senderId");
        Integer receiverId = body.get("receiverId");
        Integer channelId = body.get("channelId");

        Invitation invitation = invitationService.createInvitation(senderId, receiverId, channelId);

        if (invitation == null) {
            return ResponseEntity.badRequest().body("Utilisateur ou channel introuvable.");
        }

        return ResponseEntity.ok(invitation);
    }

    // Invite a user to a channel by its email
    @PostMapping("by-email")
    public ResponseEntity<?> createInvitationByEmail(@RequestBody Map<String, String> body) {
        String senderId = body.get("senderId");
        String receiverEmail = body.get("receiverMail");
        String channelId = body.get("channelId");

        Invitation invitation = invitationService.inviteUserByEmail(senderId, receiverEmail, channelId);

        if (invitation == null) {
            return ResponseEntity.badRequest().body("Utilisateur ou channel introuvable.");
        }

        return ResponseEntity.ok(invitation);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Integer id) {
        Invitation invitation = invitationService.acceptInvitation(id);

        if (invitation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(invitation);
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<?> declineInvitation(@PathVariable Integer id) {
        Invitation invitation = invitationService.declineInvitation(id);

        if (invitation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(invitation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable Integer id) {
        boolean deleted = invitationService.deleteInvitation(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
