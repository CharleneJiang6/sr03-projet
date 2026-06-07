package fr.utc.sr03.services;

import fr.utc.sr03.model.Message;
import fr.utc.sr03.repository.MessageRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Resource
    private MessageRepository messageRepository;

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByChannelId(Integer channelId) {
        return messageRepository.findByChannel_IdOrderByCreationDateAsc(channelId);
    }

    public void deleteMessageById(Integer id) {
        messageRepository.deleteById(id);
    }
}
