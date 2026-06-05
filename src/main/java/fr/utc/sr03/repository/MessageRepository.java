package fr.utc.sr03.repository;

import fr.utc.sr03.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByChannel_IdOrderByCreationDateAsc(Integer channelId);

}