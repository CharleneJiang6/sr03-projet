package fr.utc.sr03.services;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Participation;
import fr.utc.sr03.model.User;
import fr.utc.sr03.repository.ParticipationRepository;
import jakarta.annotation.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParticipationService {

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private ChannelService channelService;

    @Resource
    private UserService userService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Create a participation for a given user and channel.
     * Validates that the user and channel exist and that the participation does not already exist.
     */
    public Participation addParticipation(Integer userId, Integer channelId) {
        User user = getExistingUser(userId);
        Channel channel = getExistingChannel(channelId);

        boolean alreadyExists = participationRepository.existsByUserIdAndChannelId(userId, channelId);
        if (alreadyExists) {
            throw new IllegalStateException(
                    "L'utilisateur " + userId + " est déjà participant du salon " + channelId + "."
            );
        }

        Participation participation = new Participation(user, channel);
        return participationRepository.save(participation);
    }

    /**
     * Remove a participation for a given user and channel.
     * Checks that the user and channel exist, then removes the participation if found.
     * If the participation does not exist, it does nothing.
     */
    public void removeParticipation(Integer userId, Integer channelId) {
        getExistingUser(userId);
        getExistingChannel(channelId);

        Participation participation =
                participationRepository.findByUserIdAndChannelId(userId, channelId).orElse(null);

        if (participation == null) {
            return;
        }

        participationRepository.delete(participation);
    }

    /**
     * Remove a participation with basic access control:
     * - currentUserId can always remove their own participation.
     * - the owner of the channel can remove any participant from the channel.
     *
     * @return true if a participation has been removed, false if no participation existed.
     */
    public boolean removeParticipationWithRights(Integer userId, Integer channelId, Integer currentUserId) {
        User targetUser = getExistingUser(userId);
        Channel channel = getExistingChannel(channelId);
        User currentUser = getExistingUser(currentUserId);

        boolean isSelf = targetUser.getId().equals(currentUser.getId());
        boolean isChannelOwner = channel.getOwner() != null
                && channel.getOwner().getId().equals(currentUser.getId());

        if (!isSelf && !isChannelOwner) {
            throw new SecurityException(
                    "Vous n'êtes pas autorisé à supprimer cette participation."
            );
        }

        Participation participation =
                participationRepository.findByUserIdAndChannelId(userId, channelId).orElse(null);

        if (participation == null) {
            // No participation to delete
            return false;
        }

        participationRepository.delete(participation);

        messagingTemplate.convertAndSend(
                "/topic/channel/" + channelId + "/members",
                Optional.of(Map.of(
                                "type", "MEMBER_REMOVED",
                                "userId", userId,
                                "channelId", channelId
                        )
                )
        );
        return true;
    }

    /**
     * Get all participations (channels) of a given user.
     */
    public List<Participation> findUserParticipations(Integer userId) {
        getExistingUser(userId);
        return participationRepository.findByUserId(userId);
    }

    /**
     * Get all participations (users) of a given channel.
     */
    public List<Participation> findChannelParticipations(Integer channelId) {
        getExistingChannel(channelId);
        return participationRepository.findByChannelId(channelId);
    }

    /**
     * Get the participation of a user in a channel, or null if the user is not a participant.
     */
    public Participation findSpecificParticipation(Integer userId, Integer channelId) {
        getExistingUser(userId);
        getExistingChannel(channelId);

        return participationRepository.findByUserIdAndChannelId(userId, channelId).orElse(null);
    }

    /**
     * Check if a user is participant of a given channel.
     * Still validates that both user and channel exist.
     */
    public boolean isUserInChannel(Integer userId, Integer channelId) {
        getExistingUser(userId);
        getExistingChannel(channelId);
        return participationRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    public Participation addParticipationByEmail(String userMail, int channelId) {
        Optional<User> user = userService.getUserByMail(userMail);
        if (user.isEmpty()) {
            throw new IllegalArgumentException(
                    "Aucun utilisateur trouvé avec l'adresse e-mail : " + userMail
            );
        }

        getExistingUser(user.get().getId());

        return addParticipation(user.get().getId(), channelId);
    }

    /**
     * Returns an existing user or throws an exception if the user does not exist.
     */
    private User getExistingUser(Integer userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException(
                    "L'utilisateur avec l'identifiant " + userId + " n'existe pas."
            );
        }
        return user;
    }

    /**
     * Returns an existing channel or throws an exception if the channel does not exist.
     */
    private Channel getExistingChannel(Integer channelId) {
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException(
                    "Le salon avec l'identifiant " + channelId + " n'existe pas."
            );
        }
        return channel;
    }

    @Transactional
    public void deleteAllParticipationsForUser(int userId) {
        participationRepository.deleteByUserId(userId);
    }

}
