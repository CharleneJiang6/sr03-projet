package fr.utc.sr03.controller;


import fr.utc.sr03.model.Channel;
import fr.utc.sr03.model.User;
import fr.utc.sr03.services.ChannelService;
import fr.utc.sr03.services.UserService;
import fr.utc.sr03.services.JakartaEmail;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

}
