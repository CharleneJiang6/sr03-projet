// This program creates dummy data for demo/testing purpose.
// Command line to run:
// mvn spring-boot:run -Dspring-boot.run.profiles=testdata

package fr.utc.sr03.init;

import fr.utc.sr03.model.User;
import fr.utc.sr03.model.dto.UserDTO;
import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.Message;
import fr.utc.sr03.model.enums.ChannelType;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.ChannelService;
import fr.utc.sr03.services.InvitationService;
import fr.utc.sr03.services.MessageService;
import fr.utc.sr03.services.ParticipationService;
import fr.utc.sr03.repository.InvitationRepository;
import fr.utc.sr03.repository.MessageRepository;
import fr.utc.sr03.repository.ParticipationRepository;
import fr.utc.sr03.repository.ChannelRepository;
import fr.utc.sr03.repository.UserRepository;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Initialize a rich, deterministic dataset for local/demo/testing usage.
 * Runs only when the 'testdata' Spring profile is active.
 */
@Component
@Profile("testdata")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private ChannelRepository channelRepository;

    @Resource
    private ParticipationRepository participationRepository;

    @Resource
    private MessageRepository messageRepository;

    @Resource
    private InvitationRepository invitationRepository;

    @Resource
    private UserService userService;

    @Resource
    private ChannelService channelService;

    @Resource
    private InvitationService invitationService;

    @Resource
    private MessageService messageService;

    @Resource
    private ParticipationService participationService;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public void run(String... args) throws Exception {
        log.info("Using datasource URL: {}", datasourceUrl);
        log.info("=== Initializing dummy test data (profile 'testdata') ===");

        // 1) Clean database (order matters because of FK constraints)
        invitationRepository.deleteAll();
        messageRepository.deleteAll();
        participationRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();

        // 2) Create admin users (Thymeleaf side)
        Optional<UserDTO> jupiterOpt = userService.createAdminUser("Jupiter", "Planète", "jupiter@mail.fr", "Jupiter*2026");
        Optional<UserDTO> saturneOpt = userService.createAdminUser("Saturne", "Planète", "saturne@mail.fr", "Saturne*2026");
        Optional<UserDTO> neptuneOpt = userService.createAdminUser("Neptune", "Planète", "neptune@mail.fr", "Neptune*2026");

        // unwrap admin DTOs (we mostly operate on DTOs in this initializer)
        UserDTO jupiter = jupiterOpt.orElseThrow(() -> new IllegalStateException("Failed to create admin: jupiter"));
        UserDTO saturne = saturneOpt.orElseThrow(() -> new IllegalStateException("Failed to create admin: saturne"));
        UserDTO neptune = neptuneOpt.orElseThrow(() -> new IllegalStateException("Failed to create admin: neptune"));

        // Verify IDs were generated
        if (jupiter.getId() == null || saturne.getId() == null || neptune.getId() == null) {
            throw new IllegalStateException("Admin user IDs were not generated! jupiter=" + jupiter.getId() + ", saturne=" + saturne.getId() + ", neptune=" + neptune.getId());
        }
        log.info("Admin users created with IDs: jupiter={}, saturne={}, neptune={}", jupiter.getId(), saturne.getId(), neptune.getId());

        // 3) Create regular Greek users (React side)
        List<UserDTO> greeks = createGreekUsers();
        UserDTO alpha = getByFirstname(greeks, "Alpha");
        UserDTO beta = getByFirstname(greeks, "Beta");
        UserDTO gamma = getByFirstname(greeks, "Gamma");
        UserDTO delta = getByFirstname(greeks, "Delta");
        UserDTO epsilon = getByFirstname(greeks, "Epsilon");
        UserDTO zeta = getByFirstname(greeks, "Zeta");

        // Verify all Greek users have IDs
        if (alpha.getId() == null || beta.getId() == null || gamma.getId() == null ||
            delta.getId() == null || epsilon.getId() == null || zeta.getId() == null) {
            throw new IllegalStateException("Greek user IDs were not generated!");
        }
        log.info("Greek users created with IDs: alpha={}, beta={}, gamma={}, delta={}, epsilon={}, zeta={}",
                 alpha.getId(), beta.getId(), gamma.getId(), delta.getId(), epsilon.getId(), zeta.getId());

        LocalDateTime now = LocalDateTime.now();

        // 4) Channels for Alpha (PRIVATE + GROUP, active/expired)

        // 4.1 Two active private channels (MP) for Alpha
        Channel mpAlphaBeta = createChannel(
                "MP Alpha - Beta",
                "Discussion privée entre Alpha et Beta",
                now.minusDays(2),
                now.plusDays(5),
                alpha,
                "PRIVATE"
        );
        addParticipants(mpAlphaBeta, beta);

        Channel mpAlphaGamma = createChannel(
                "MP Alpha - Gamma",
                "Discussion privée entre Alpha et Gamma",
                now.minusDays(1),
                now.plusDays(3),
                alpha,
                "PRIVATE"
        );
        addParticipants(mpAlphaGamma, gamma);

        // 4.2 One expired private channel for Alpha
        Channel mpAlphaDeltaExpired = createChannel(
                "MP Alpha - Delta (expiré)",
                "Ancienne discussion privée entre Alpha et Delta",
                now.minusDays(10),
                now.minusDays(1), // expired
                alpha,
                "PRIVATE"
        );
        addParticipants(mpAlphaDeltaExpired, delta);

        // 4.3 Active group with everyone (admins + greek users)
        Channel alphaAllHands = createChannel(
                "All hands Alpha",
                "Salon de groupe avec tous les utilisateurs",
                now.minusDays(1),
                now.plusDays(30),
                alpha,
                "GROUP"
        );
        addParticipants(alphaAllHands, beta, gamma, delta, epsilon, zeta, jupiter, saturne, neptune);

        // 4.4 Active group with a subset (study group for SR03)
        Channel alphaStudyGroup = createChannel(
                "Groupe étude SR03",
                "Groupe de travail pour SR03",
                now.minusDays(3),
                now.plusDays(10),
                alpha,
                "GROUP"
        );
        addParticipants(alphaStudyGroup, beta, gamma, delta);

        // 4.5 Expired group for Alpha
        Channel alphaOldGroupExpired = createChannel(
                "Ancien groupe projet",
                "Groupe expiré du précédent projet",
                now.minusDays(40),
                now.minusDays(5), // expired
                alpha,
                "GROUP"
        );
        addParticipants(alphaOldGroupExpired, beta, gamma);

        // 5) Channels for other users (for richer tests)

        Channel betaCoffeeGroup = createChannel(
                "Café du matin",
                "Discussions informelles autour du café",
                now.minusDays(2),
                now.plusDays(60),
                beta,
                "GROUP"
        );
        addParticipants(betaCoffeeGroup, alpha, gamma, saturne);

        Channel gammaPrivateWithEpsilon = createChannel(
                "MP Gamma - Epsilon",
                "Discussion privée entre Gamma et Epsilon",
                now.minusDays(5),
                now.plusDays(1),
                gamma,
                "PRIVATE"
        );
        addParticipants(gammaPrivateWithEpsilon, epsilon);

        // 6) Messages examples

        Message m1 = createMessage(alpha, alphaAllHands, "Bienvenue à tous dans ce salon !");
        Message m2 = createMessage(beta, alphaAllHands, "Salut tout le monde 👋");
        Message m3 = createMessage(gamma, alphaAllHands, "On commence quand la réunion ?");
        Message m4 = createMessage(jupiter, alphaAllHands, "Pensez à tester la version admin aussi.");

        Message m5 = createMessage(alpha, mpAlphaBeta, "Hey Beta, tu peux regarder ma PR ?");
        Message m6 = createMessage(beta, mpAlphaBeta, "Oui, je regarde cet après-midi.");

        Message m7 = createMessage(alpha, mpAlphaGamma, "Gamma, tu t’occupes de la partie React ?");
        Message m8 = createMessage(gamma, mpAlphaGamma, "Oui, je fais la page de login.");

        Message m9  = createMessage(beta, betaCoffeeGroup, "Qui est chaud pour un café ?");
        Message m10 = createMessage(saturne, betaCoffeeGroup, "Toujours partant ☕");

        Message m11 = createMessage(gamma, gammaPrivateWithEpsilon, "Tu as vu le dernier sujet SR03 ?");
        Message m12 = createMessage(epsilon, gammaPrivateWithEpsilon, "Oui, ça a l’air costaud mais faisable.");

        // Debug optionnel pour vérifier les IDs
        log.debug("Messages IDs: {}, {}, {}, {}", m1.getId(), m2.getId(), m3.getId(), m4.getId());

        // 7) Invitations for Alpha (sent and received)

        // Sent by Alpha (to other Greeks) on alphaStudyGroup
        var inv1 = invitationService.createInvitation(alpha.getId(), delta.getId(), alphaStudyGroup.getId());
        var inv2 = invitationService.createInvitation(alpha.getId(), epsilon.getId(), alphaStudyGroup.getId());
        var inv3 = invitationService.createInvitation(alpha.getId(), zeta.getId(), alphaStudyGroup.getId());
        log.debug("Invitations Alpha sent IDs: {}, {}, {}", inv1.getId(), inv2.getId(), inv3.getId());

        // One invitation for Alpha: Group owned by Delta, Alpha receives invitations there
        Channel deltaSmallGroup = createChannel(
                "Groupe Delta",
                "Petit groupe piloté par Delta",
                now.minusDays(1),
                now.plusDays(20),
                delta,
                "GROUP"
        );
        addParticipants(deltaSmallGroup, beta, zeta);

        // One more invitation for Alpha
        Channel epsilonSmallGroup = createChannel(
                "Groupe Epsilon",
                "Petit groupe piloté par Epsilon",
                now.minusDays(1),
                now.plusDays(20),
                delta,
                "GROUP"
        );
        addParticipants(epsilonSmallGroup, beta, gamma);

        var inv4 = invitationService.createInvitation(delta.getId(), alpha.getId(), deltaSmallGroup.getId());
        var inv5 = invitationService.createInvitation(epsilon.getId(), alpha.getId(), epsilonSmallGroup.getId());
        log.debug("Invitations Alpha received IDs: {}, {}", inv4.getId(), inv5.getId());


        log.info("=== Dummy test data initialized successfully ===");
        log.info("Admins:");
        log.info(" - jupiter@mail.fr / Jupiter*2026");
        log.info(" - saturne@mail.fr / Saturne*2026");
        log.info(" - neptune@mail.fr / Neptune*2026");
        log.info("Regular users:");
        log.info(" - alpha@mail.fr / Alpha*2026");
        log.info(" - beta@mail.fr / Beta*2026");
        log.info(" - gamma@mail.fr / Gamma*2026");
        log.info(" - delta@mail.fr / Delta*2026");
        log.info(" - epsilon@mail.fr / Epsilon*2026");
        log.info(" - zeta@mail.fr / Zeta*2026");
    }


    // ---------- Helpers using services -  ---------

    // Create several users with a deterministic pattern (use DTOs)
    private List<UserDTO> createGreekUsers() {
        String[] greekLetters = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta"};
        List<UserDTO> created = new ArrayList<>();

        for (String letter : greekLetters) {
            String email = letter.toLowerCase() + "@mail.fr";
            String password = letter + "*2026";

            userService.createRegularUser(
                    letter,
                    "Grec",
                    email,
                    password
            ).ifPresent(created::add);
        }

        return created;
    }

    private UserDTO getByFirstname(List<UserDTO> users, String firstname) {
        return users.stream()
                .filter(u -> firstname.equals(u.getFirstname()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User not found: " + firstname));
    }

    private Channel createChannel(String title,
                                  String description,
                                  LocalDateTime creation,
                                  LocalDateTime expiration,
                                  UserDTO ownerDto,
                                  String type) {

        Channel channel = new Channel();
        channel.setTitle(title);
        channel.setDescription(description);
        channel.setCreationDate(creation);
        channel.setExpirationDate(expiration);

        // resolve owner entity only when necessary
        User owner = userService.getUserById(ownerDto.getId());
        if (owner == null) {
            throw new IllegalStateException("Owner user entity not found for id: " + ownerDto.getId());
        }
        channel.setOwner(owner);
        channel.setType(ChannelType.valueOf(type)); // "GROUP" or "PRIVATE"

        Channel savedChannel = channelService.saveChannel(channel);

        // Verify the channel got an ID after saving
        if (savedChannel == null || savedChannel.getId() == null) {
            throw new IllegalStateException("Channel '" + title + "' was not saved with an ID! Saved channel: " + savedChannel);
        }

        log.debug("Channel '{}' created with ID: {}", title, savedChannel.getId());
        return savedChannel;
    }

    private void addParticipants(Channel channel, UserDTO... users) {
        Integer ownerId = channel.getOwner().getId();

        for (UserDTO user : users) {
            if (user.getId().equals(ownerId)) {
                log.warn("Skipping owner {} as extra participant for channel {}", user.getMail(), channel.getTitle());
                continue;
            }
//            participationService.addParticipation(user.getId(), channel.getId());
            var participation = participationService.addParticipation(user.getId(), channel.getId());
            log.debug("Participation created with ID: {} (user={}, channel={})",
                    participation.getId(), user.getMail(), channel.getTitle());
        }
    }

    private Message createMessage(UserDTO senderDto, Channel channel, String content) {
        Message message = new Message();
        message.setContent(content);
        message.setCreationDate(LocalDateTime.now());

        User sender = userService.getUserById(senderDto.getId());
        if (sender == null) {
            throw new IllegalStateException("Sender user entity not found for id: " + senderDto.getId());
        }
        message.setSender(sender);
        message.setChannel(channel);

        Message savedMessage = messageService.createMessage(message);

        // Verify the message got an ID after saving
        if (savedMessage == null || savedMessage.getId() == null) {
            throw new IllegalStateException("Message was not saved with an ID! Content: '" + content + "'");
        }

        log.debug("Message created with ID: {} (sender: {}, channel: {})", savedMessage.getId(), sender.getFirstname(), channel.getTitle());
        return savedMessage;
    }
}
