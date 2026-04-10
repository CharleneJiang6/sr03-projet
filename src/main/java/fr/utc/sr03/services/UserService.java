package fr.utc.sr03.services;


import fr.utc.sr03.model.User;
import fr.utc.sr03.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    // CREATE or UPDATE
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // READ
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public Optional<User> getUserByMail(String emailAddress) {return userRepository.findByMail(emailAddress);}

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // DELETE
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

}
