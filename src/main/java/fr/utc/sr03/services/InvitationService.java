package fr.utc.sr03.services;

import fr.utc.sr03.model.*;
import fr.utc.sr03.model.enums.ChannelType;
import fr.utc.sr03.model.enums.InvitationStatus;
import fr.utc.sr03.repository.InvitationRepository;
import fr.utc.sr03.model.dto.ParticipationDTO;
import jakarta.annotation.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvitationService {

    @Resource
    private InvitationRepository invitationRepository;

    @Resource
    private UserService userService;

    @Resource
    private ChannelService channelService;

    @Resource
    private ParticipationService participationService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    public List<Invitation> getAllInvitations() {
        return invitationRepository.findAll();
    }

    public Invitation getInvitationById(Integer id) {
        return invitationRepository.findById(id).orElse(null);
    }

    public List<Invitation> getReceivedInvitations(Integer userId) {
        return invitationRepository.findByReceiverId(userId);
    }

    public List<Invitation> getSentInvitations(Integer userId) {
        return invitationRepository.findBySenderId(userId);
    }

    public Invitation createInvitation(Integer senderId, Integer receiverId, Integer channelId) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        Channel channel = channelService.getChannelById(channelId);

        if (sender == null || receiver == null || channel == null) {
            throw new IllegalArgumentException("Les paramètres de l'invitation ne doivent pas être nuls.");
        }

        List<Invitation> existingInvitation = invitationRepository.findBySenderIdAndReceiverIdAndChannelId(
                senderId, receiverId, channelId);
        if (!existingInvitation.isEmpty() && existingInvitation.getFirst().getStatus() == InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Une invitation en attente existe déjà entre ces utilisateurs pour ce salon.");
        }

        // Specific rules for Private chats (2 members only)
        if (ChannelType.PRIVATE.equals(channel.getType())) {

            // Verify the number of existing participations in the channel
            List<Participation> participations = participationService.findChannelParticipations(channelId);
            if (participations.size() >= 2) {
                throw new IllegalArgumentException("Les salons privés ne peuvent pas avoir plus de 2 membres. " +
                        "Veuillez créer un salon de type 'Groupe' pour inviter plus de personnes.");
            }

            // Verify the invitations already sent by this sender for this channel
            List<Invitation> sentInvitations = getSentInvitations(senderId);
            boolean hasPendingForThisChannel = sentInvitations.stream()
                    .anyMatch(inv -> inv.getChannel().getId().equals(channelId)
                            && inv.getStatus() == InvitationStatus.PENDING);

            if (hasPendingForThisChannel) {
                throw new IllegalArgumentException("Vous avez déjà une invitation envoyé en attente pour ce salon. " +
                        "Veuillez l'annuler avant d'envoyer une autre.");
            }
        }

        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setReceiver(receiver);
        invitation.setChannel(channel);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setCreationDate(LocalDateTime.now());

        return invitationRepository.save(invitation);
    }

    public Invitation acceptInvitation(Integer id) {
        Invitation invitation = getInvitationById(id);

        if (invitation == null) {
            return null;
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
        Participation participation = participationService.addParticipation(
                invitation.getReceiver().getId(),
                invitation.getChannel().getId()
        );

        // As a new member is being successfully added to a channel,
        // all clients subscribed to the channel's members topic are notified with the new member's information.
        if (participation != null) {
            Integer channelId = invitation.getChannel().getId();
            messagingTemplate.convertAndSend(
                    "/topic/channel/" + channelId + "/members",
                    ParticipationDTO.fromEntity(participation)
            );
        }

        return invitation;
    }

    public Invitation declineInvitation(Integer id) {
        Invitation invitation = getInvitationById(id);

        if (invitation == null) {
            return null;
        }

        invitation.setStatus(InvitationStatus.REFUSED);
        return invitationRepository.save(invitation);
    }

    public boolean deleteInvitation(Integer id) {
        if (!invitationRepository.existsById(id)) {
            return false;
        }

        invitationRepository.deleteById(id);
        return true;
    }

    public Invitation inviteUserByEmail(String senderId, String receiverEmail, String channelId) {
        Integer sId;
        Integer cId;
        try {
            sId = Integer.parseInt(senderId);
            cId = Integer.parseInt(channelId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Les identifiants envoyés sont invalides.");
        }

        User sender = userService.getUserById(sId);
        if (sender == null) {
            throw new IllegalArgumentException("L'utilisateur émetteur est introuvable.");
        }

        receiverEmail = receiverEmail.toLowerCase();
        User receiver = userService.getUserByMail(receiverEmail).orElse(null);
        if (receiver == null) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé avec l'adresse e-mail : " + receiverEmail);
        }

        Channel channel = channelService.getChannelById(cId);
        if (channel == null) {
            throw new IllegalArgumentException("Le salon cible est introuvable.");
        }

        Participation participationFound = participationService.findSpecificParticipation(
                receiver.getId(), cId
        );
        if (participationFound != null) {
            throw new IllegalArgumentException(receiverEmail + " est déjà membre du channel.");
        }

        return createInvitation(sender.getId(), receiver.getId(), channel.getId());
    }
}
