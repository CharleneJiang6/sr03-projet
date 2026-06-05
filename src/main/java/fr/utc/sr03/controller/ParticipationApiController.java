package fr.utc.sr03.controller;

import fr.utc.sr03.model.ApiResponse;
import fr.utc.sr03.model.Participation;
import fr.utc.sr03.model.ParticipationDTO;
import fr.utc.sr03.services.ParticipationService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(path = "/api/v1/participations")
public class ParticipationApiController {

    @Resource
    private ParticipationService participationService;

    /**
     * Create a participation for a given user and channel.
     * Expected body:
     * {
     * "userId": "1",
     * "channelId": "42"
     * }
     */
    @PostMapping
    public ResponseEntity<?> addParticipation(
            @RequestBody Map<String, String> body
    ) {
        try {
            if (!body.containsKey("userId") || !body.containsKey("channelId")) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "Les champs 'userId' et 'channelId' sont obligatoires.",
                                400
                        )
                );
            }

            int userId;
            int channelId;
            try {
                userId = Integer.parseInt(body.get("userId"));
                channelId = Integer.parseInt(body.get("channelId"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "Les identifiants 'userId' et 'channelId' doivent être des entiers.",
                                400
                        )
                );
            }

            Participation participation = participationService.addParticipation(userId, channelId);
            return ResponseEntity.ok(ParticipationDTO.fromEntity(participation));

        } catch (IllegalArgumentException e) {
            // user or channel inexistant
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (IllegalStateException e) {
            // participation existing
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse(
                            "Erreur inattendue lors de la création de la participation.",
                            500
                    )
            );
        }
    }

    /**
     * Create a participation for a given channel using a user email.
     * Expected body:
     * {
     * "userMail": "user@example.com",
     * "channelId": "42"
     * }
     */
    @PostMapping("/by-email")
    public ResponseEntity<?> addParticipationByEmail(
            @RequestBody Map<String, String> body
    ) {
        try {
            if (!body.containsKey("userMail") || !body.containsKey("channelId")) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "Les champs 'userMail' et 'channelId' sont obligatoires.",
                                400
                        )
                );
            }

            String userMail = body.get("userMail");
            int channelId;
            try {
                channelId = Integer.parseInt(body.get("channelId"));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "L'identifiant 'channelId' doit être un entier.",
                                400
                        )
                );
            }

            Participation participation = participationService.addParticipationByEmail(userMail, channelId);
            return ResponseEntity.ok(ParticipationDTO.fromEntity(participation));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse(
                            "Erreur inattendue lors de la création de la participation.",
                            500
                    )
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeParticipation(
            @RequestParam Integer userId,
            @RequestParam Integer channelId,
            @RequestParam Integer currentUserId
    ) {
        try {
            boolean deleted = participationService.removeParticipationWithRights(userId, channelId, currentUserId);

            if (!deleted) {
                // Nothing was deleted: either no participation exists for this user/channel
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "Aucune participation trouvée pour l'utilisateur " + userId +
                                        " dans le salon " + channelId + ".",
                                404
                        )
                );
            }

            // Successful deletion
            return ResponseEntity.ok(
                    new ApiResponse("Participation supprimée avec succès.", 200)
            );

        } catch (IllegalArgumentException e) {
            // user ou channel inexistant
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (SecurityException e) {
            // pas les droits
            return ResponseEntity.status(403).body(
                    new ApiResponse(e.getMessage(), 403)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse(
                            "Erreur inattendue lors de la suppression de la participation.",
                            500
                    )
            );
        }
    }

    /**
     * Get participations filtered either by userId or by channelId.
     * Examples:
     * GET /api/v1/participations?userId=1
     * GET /api/v1/participations?channelId=42
     * If both are null, returns 400 with message.
     */
    @GetMapping
    public ResponseEntity<?> getParticipations(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer channelId
    ) {
        try {
            if (userId == null && channelId == null) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse(
                                "Vous devez fournir au moins 'userId' ou 'channelId' en paramètre.",
                                400
                        )
                );
            }

            if (userId != null && channelId != null) {
                Participation participation =
                        participationService.findSpecificParticipation(userId, channelId);
                if (participation == null) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(
                        List.of(ParticipationDTO.fromEntity(participation))
                );
            }

            // when only userId provided
            if (userId != null) {
                List<ParticipationDTO> participations =
                        participationService.findUserParticipations(userId).stream()
                                .map(ParticipationDTO::fromEntity)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(participations);
            }

            // when only channelId provided
            List<ParticipationDTO> participations =
                    participationService.findChannelParticipations(channelId).stream()
                            .map(ParticipationDTO::fromEntity)
                            .collect(Collectors.toList());
            return ResponseEntity.ok(participations);

        } catch (IllegalArgumentException e) {
            // user or channel inexistant
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse(
                            "Erreur inattendue lors de la récupération des participations.",
                            500
                    )
            );
        }
    }

    /**
     * Check if a user is participant of a given channel.
     * Example:
     * GET /api/v1/participations/check?userId=1&channelId=42
     * Response: boolean (true / false) or error.
     */
    @GetMapping("/check")
    public ResponseEntity<?> isUserInChannel(
            @RequestParam Integer userId,
            @RequestParam Integer channelId
    ) {
        try {
            boolean inChannel = participationService.isUserInChannel(userId, channelId);
            return ResponseEntity.ok(inChannel);
        } catch (IllegalArgumentException e) {
            // user ou channel inexistant
            return ResponseEntity.badRequest().body(
                    new ApiResponse(e.getMessage(), 400)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse(
                            "Erreur inattendue lors de la vérification de la participation.",
                            500
                    )
            );
        }
    }
}
