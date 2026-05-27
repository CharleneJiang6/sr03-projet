package fr.utc.sr03.services;

import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Message;
import fr.utc.sr03.repository.MessageRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Resource
    private MessageRepository messageRepository;

    // CREATE
    public void createMessage(Message message) {
        messageRepository.save(message);
    }

    // READ
    public Message getMessageById(int id) {
        return messageRepository.findById(id).orElse(null);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getMessagesByChannel(Channel channel) {
        return messageRepository.findByChannelOrderByCreationDateAsc(channel);
    }

    // DELETE
    public void deleteMessageById(int id) {
        messageRepository.deleteById(id);
    }
}