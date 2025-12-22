package dev.folomkin.springsecuritymaster.controllers;

import dev.folomkin.springsecuritymaster.entities.User;
import dev.folomkin.springsecuritymaster.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MainController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @GetMapping("/authenticated")
    public String pageForAuthenticatedUsers(Principal principal) {
        User user = userService.findByUserName(principal.getName());
        return "secured part of web service: " + user.getUsername() + " " + user.getEmail();
    }

    @GetMapping("/read_profile")
    public String pageForReadProfile() {
        return "read profile page";
    }

    @GetMapping("/only_for_admins")
    public String pageOnlyForAdmins() {
        return "admins page";
    }
}
