package fr.utc.sr03.repository;

import fr.utc.sr03.model.Invitation;
import fr.utc.sr03.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    List<Invitation> findByReceiverId(Integer receiverId);

    List<Invitation> findBySenderId(Integer senderId);

    List<Invitation> findByReceiverIdAndStatus(Integer receiverId, InvitationStatus status);

    int countByChannelIdAndSenderIdAndStatus(Integer channelId, Integer senderId, InvitationStatus invitationStatus);

    List<Invitation> findBySenderIdAndReceiverIdAndChannelId(Integer senderId, Integer receiverId, Integer channelId);

    List<Invitation> findByReceiverIdOrderByCreationDateDesc(Integer userId);

    List<Invitation> findBySenderIdOrderByCreationDateDesc(Integer userId);

    void deleteByChannelId(int channelId);
}
