package fr.utc.sr03.repository;

import fr.utc.sr03.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    List<Channel> findByTitleContainingIgnoreCase(String title);

    List<Channel> findByCreationDateAfter(LocalDateTime dateTime);

    List<Channel> findByExpirationDateAfter(LocalDateTime dateTime);

    List<Channel> findByExpirationDateBefore(LocalDateTime dateTime);
}