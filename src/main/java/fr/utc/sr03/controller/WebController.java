// Spring MVC controller calling Thymeleaf templates to render Admin interface.

package fr.utc.sr03.controller;


import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.utc.sr03.model.User;
import fr.utc.sr03.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Resource
    private UserService userService;

    @GetMapping(value = "/")
    public String root() {
        return "redirect:/admin/login";
    }


    @GetMapping(value = "/admin")
    public String index(HttpSession session) {

        if (!isAdminConnected(session)) {
            return "redirect:/admin/login";
        }

        return "admin/home";
    }


    // Show the user creation form
    @GetMapping(value = "/admin/users/create")
    public String createUserForm(Model model, HttpSession session) {

        if (!isAdminConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("user", new User());
        return "admin/create-user";
    }



    // Handle the form submission to create a new user
    @PostMapping(value = "/admin/users/create")
    public String createUser(Model model, @ModelAttribute User user) {

        if (userService.getUserByMail(user.getMail()).isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Cette adresse mail est déjà utilisée.");
            return "admin/create-user";
        }

        // Encrypt the password before saving
        String password = user.getPassword();
        String passwordEncrypted = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println(passwordEncrypted);
        user.setPassword(passwordEncrypted);

        User createdUser = userService.saveUser(user);
        model.addAttribute("user", createdUser);
        return "usercreated"; // Show the results of creating a new user
    }






    // Show the admin login form
    @GetMapping(value = "/admin/login") // URL path
    public String loginUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/login";
    }

    // Handle admin login flow
    @PostMapping(value = "/admin/login")
    public String loginUser(Model model, @ModelAttribute User user, HttpSession session) {

        User existingUser = userService.getUserByMail(user.getMail()).orElse(null);

        if (existingUser == null) {
            model.addAttribute("error", "User not found");
            return "admin/login";
        }

        if (!existingUser.getAdmin()) {
            model.addAttribute("error", "Access denied: admin only");
            return "admin/login";
        }

        if (!existingUser.getActivated()) {
            model.addAttribute("error", "Account disabled");
            return "admin/login";
        }

        String incomingPassword = user.getPassword();
        String storedPasswordHash = existingUser.getPassword();

        BCrypt.Result result = BCrypt.verifyer()
                .verify(incomingPassword.toCharArray(), storedPasswordHash);

        if (result.verified) {
            session.setAttribute("connectedAdmin", existingUser);
            model.addAttribute("user", existingUser);
            return "admin/home";
        }

        model.addAttribute("error", "Invalid password");
        return "admin/login";
    }

    @GetMapping(value = "/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    private boolean isAdminConnected(HttpSession session) {
        return session.getAttribute("connectedAdmin") != null;
    }




    @GetMapping(value = "/admin/users")
    public String users(Model model, HttpSession session) {

        if (!isAdminConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("myusers", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping(value = "/admin/users/disabled")
    public String disabledUsers(Model model, HttpSession session) {

        if (!isAdminConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("users", userService.getInactiveUsers());
        return "admin/disabled-users";
    }





    @GetMapping(value = "/admin/users/disable/{id}")
    public String disableUser(@PathVariable int id) {
        userService.setActivated(id, false);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/admin/users/enable/{id}")
    public String enableUser(@PathVariable int id) {
        userService.setActivated(id, true);
        return "redirect:/admin/users";
    }

    // we cannot delete last admin
    @GetMapping(value = "/admin/users/delete/{id}")
    public String deleteUser(@PathVariable int id) {

        User user = userService.getUserById(id);

        if (user != null && user.getAdmin()
                && userService.getAdminUsers().size() <= 1) {
            return "redirect:/admin/users";
        }

        userService.deleteUserById(id);

        return "redirect:/admin/users";
    }




}
