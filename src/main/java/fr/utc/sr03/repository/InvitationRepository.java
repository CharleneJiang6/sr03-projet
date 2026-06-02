package fr.utc.sr03.repository;

import fr.utc.sr03.model.Invitation;
import fr.utc.sr03.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    List<Invitation> findByReceiverId(Integer receiverId);

    List<Invitation> findBySenderId(Integer senderId);

    List<Invitation> findByReceiverIdAndStatus(Integer receiverId, InvitationStatus status);
}