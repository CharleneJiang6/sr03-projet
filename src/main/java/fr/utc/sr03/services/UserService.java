package fr.utc.sr03.services;

import fr.utc.sr03.model.User;
import fr.utc.sr03.model.dto.UserDTO;
import fr.utc.sr03.model.dto.UserUpdateRequest;
import fr.utc.sr03.repository.UserRepository;
import jakarta.annotation.Resource;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordService passwordService;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(int id, UserUpdateRequest dto) {
        User user = getUserById(id);
        if (user == null) { return null; }

        if (dto.firstname() != null) user.setFirstname(dto.firstname());
        if (dto.lastname() != null) user.setLastname(dto.lastname());
        if (dto.mail() != null) user.setMail(dto.mail());
        if (dto.activated() != null) user.setActivated(dto.activated());
        if (dto.admin() != null) user.setAdmin(dto.admin());

        return userRepository.save(user);
    }

    public User setActivated(int id, boolean activated) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        user.setActivated(activated);
        return userRepository.save(user);
    }

    public User setAdmin(int id, boolean admin) {
        User user = getUserById(id);

        if (user == null) {
            return null;
        }

        user.setAdmin(admin);
        return userRepository.save(user);
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByMail(String emailAddress) {
        return userRepository.findByMail(emailAddress);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActivatedTrue();
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByActivatedFalse();
    }

    public List<User> getAdminUsers() {
        return userRepository.findByAdminTrue();
    }

    public List<User> getRegularUsers() {
        return userRepository.findByAdminFalse();
    }

    public List<User> searchUsers(
            Boolean admin,
            Boolean activated,
            String firstname,
            String lastname,
            String email
    ) {
        //  If the email is present, find by email first
        if (email != null && !email.isBlank()) {
            return userRepository.findByMail(email)
                    .stream() // Iterate over each element
                    .filter(u -> filterAdmin(u, admin)) // Filter to keep relevant element only
                    .filter(u -> filterActivated(u, activated))
                    .toList();
        }

        List<User> baseUsers;

        if (firstname != null && !firstname.isBlank()
                && lastname != null && !lastname.isBlank()) {
            baseUsers = userRepository
                    .findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(firstname, lastname);
        } else if (firstname != null && !firstname.isBlank()) {
            baseUsers = userRepository
                    .findByFirstnameContainingIgnoreCase(firstname);
        } else if (lastname != null && !lastname.isBlank()) {
            baseUsers = userRepository
                    .findByLastnameContainingIgnoreCase(lastname);
        } else {
            baseUsers = userRepository.findAll();
        }

        return baseUsers.stream()
                .filter(u -> filterAdmin(u, admin))
                .filter(u -> filterActivated(u, activated))
                .toList();
    }

    private boolean filterAdmin(User user, Boolean admin) {
        if (admin == null) {
            return true;
        }
        return admin.equals(user.getAdmin());
    }

    private boolean filterActivated(User user, Boolean activated) {
        if (activated == null) {
            return true;
        }
        return activated.equals(user.getActivated());
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public boolean deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            System.out.println("user doesn't exist");
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    // NOTE: For this project, we are not implementing the full email confirmation flow
    // (creation of a confirmation token, sending an email via JakartaEmail, and a confirmation endpoint
    // to activate the user). In a real production system, we would have created the user with activated = false.
    public Optional<UserDTO> createRegularUser(String firstname, String lastname, String email, String password) {
        // Verify email uniqueness
        if (userRepository.findByMail(email).isPresent()) {
            return Optional.empty();
        }

        // Verify password security
        String passwordValidation = passwordService.validatePasswordSecurity(password);
        if (!passwordValidation.isEmpty()) {
            throw new IllegalArgumentException(passwordValidation);
        }

        User user = new User(firstname, lastname, email, passwordService.encryptPassword(password));
        User savedUser = userRepository.save(user);
        return Optional.of(new UserDTO(savedUser));
    }

    // Connect a user to the application by checking if the email and password are correct
    public Optional<UserDTO> loginUser(String email, String password) {
        if (!isValidMail(email)) {
            log.warn("Tentative de connexion avec email invalide: {}", email);
            return Optional.empty();
        }

        return userRepository.findByMail(email)
                .filter(User::getActivated) // tu peux refuser les comptes désactivés par exemple
                .filter(user -> passwordService.verifyPassword(password, user.getPassword()))
                .map(user -> {
                    log.info("Utilisateur {} connecté avec succès", email);
                    return new UserDTO(user);
                });
    }

    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public User updatePassword(int id, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("Le mot de passe ne peut pas être vide.");
        }

        String validationMessage = passwordService.validatePasswordSecurity(rawPassword);
        if (!validationMessage.isEmpty()) {
            throw new InvalidPasswordException(validationMessage);
        }

        User user = getUserById(id);
        if (user == null) {
            return null;
        }

        String encryptedPassword = passwordService.encryptPassword(rawPassword);
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    private boolean isValidMail(String email) {
        if (email == null || email.isBlank()) {return false;}
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}
