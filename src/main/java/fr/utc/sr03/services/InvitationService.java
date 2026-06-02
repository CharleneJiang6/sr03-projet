package fr.utc.sr03.services;

import fr.utc.sr03.model.*;
import fr.utc.sr03.model.enums.InvitationStatus;
import fr.utc.sr03.repository.InvitationRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvitationService {

    @Resource
    private InvitationRepository invitationRepository;

    @Resource
    private UserService userService;

    @Resource
    private ChannelService channelService;

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
            return null;
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
        return invitationRepository.save(invitation);
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
}