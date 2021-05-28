package com.example.rgrgit.controller;

import com.example.rgrgit.model.Notification;
import com.example.rgrgit.model.Post;
import com.example.rgrgit.model.User;
import com.example.rgrgit.repository.FileUploadService;
import com.example.rgrgit.service.FriendService;
import com.example.rgrgit.service.NotificationService;
import com.example.rgrgit.service.PostService;
import com.example.rgrgit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
public class MainController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private FileUploadService fileUploadService;

    @RequestMapping(value = "/register")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "registerform";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveUser(@Valid User user, BindingResult bindingResult, Model model) {
        List<String> allUsernames = userService.getAllUsersUsernames();
        String username = user.getUsername();
        String password = user.getPassword();
        String repeatedPassword = user.getRepeatedPassword();

        if (bindingResult.hasErrors() || allUsernames.contains(username) || !password.equals(repeatedPassword)) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getObjectName() + " " + error.getDefaultMessage());
            });
            return "registerform"; }
        else {
            userService.saveUser(user);
            model.addAttribute("msg", "Успешная регистрация! Пройдите процедуру активации аккаунта через email");
            return "login"; }
    }

    @RequestMapping("/activate/{activationCode}")
    public String activateUser(Model model, @PathVariable String activationCode) {
        if(userService.activateUser(activationCode)) {
            model.addAttribute("msg", "Пользователь активирован");
            return "redirect:/";
        } else {
            model.addAttribute("msg", "Код активации не найден");
            return "redirect:/";
        }
    }


    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/")
    public String homeIdle(Model model) {
        User currentUser = userService.getCurrentLoggedUser();
        List<Post> myAllPosts = postService.getMyAllPosts();
        Integer currentUserId = currentUser.getId();
        String currentUsername = currentUser.getUsername();
        List<String> requestFriendsUsersnames = friendService.getAllFriendsRequests();
        Integer myNumberOfFriendsInvitations = currentUser.getInvitations();
        Integer myNumberOfNewMessages = currentUser.getNewmessage();
        Integer myNumberOfNotifications = currentUser.getNotification();
        model.addAttribute("invitations", myNumberOfFriendsInvitations);
        model.addAttribute("newmessage", myNumberOfNewMessages);
        model.addAttribute("id", currentUserId);
        model.addAttribute("posts", myAllPosts);
        model.addAttribute("post", new Post());
        model.addAttribute("username", currentUsername);
        model.addAttribute("users", requestFriendsUsersnames);
        model.addAttribute("notifications", myNumberOfNotifications);
        return "index";
    }

    @RequestMapping(value = "/home")
    public String home(Model model) {
        User currentUser = userService.getCurrentLoggedUser();
        List<Post> myAllPosts = postService.getMyAllPosts();
        Integer currentUserId = currentUser.getId();
        String currentUsername = currentUser.getUsername();
        List<String> requestFriendsUsersnames = friendService.getAllFriendsRequests();
        Integer myNumberOfFriendsInvitations = currentUser.getInvitations();
        Integer myNumberOfNewMessages = currentUser.getNewmessage();
        Integer myNumberOfNotifications = currentUser.getNotification();
        model.addAttribute("invitations", myNumberOfFriendsInvitations);
        model.addAttribute("newmessage", myNumberOfNewMessages);
        model.addAttribute("id", currentUserId);
        model.addAttribute("posts", myAllPosts);
        model.addAttribute("post", new Post());
        model.addAttribute("username", currentUsername);
        model.addAttribute("users", requestFriendsUsersnames);
        model.addAttribute("notifications", myNumberOfNotifications);
        return "index";
    }

    @RequestMapping(value = "/home", method = RequestMethod.POST)
    public String addPost(@Valid Post post, @RequestParam("imageFile") MultipartFile imageFile, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error ->{
                System.out.println(error.getObjectName() + " " + error.getDefaultMessage());
            });
            return "redirect:/home";}
        else {
            if (!imageFile.isEmpty()) {
                try {
                    String imagePath = fileUploadService.saveImage(imageFile);
                    post.setPhoto(imagePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            postService.savePost(post);
            return "redirect:/home";}
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model, @RequestParam(value = "search") String username) {
        String currentUsername = userService.getCurrentLoggedUser().getUsername();
        User currentUser = userService.getCurrentLoggedUser();
        Integer myNumberOfFriendsInvitations = currentUser.getInvitations();
        Integer myNumberOfNewMessages = currentUser.getNewmessage();
        Integer myNumberOfNotifications = currentUser.getNotification();
        List<User> searchedUsers = userService.searchUsersByName(username.trim());

        model.addAttribute("name", username);
        model.addAttribute("user", searchedUsers);
        model.addAttribute("username", currentUsername);
        model.addAttribute("invitations", myNumberOfFriendsInvitations);
        model.addAttribute("newmessage", myNumberOfNewMessages);
        model.addAttribute("notifications", myNumberOfNotifications);
        return "searchresult";
    }

    @RequestMapping(value = "/invitations")
    public String getInvitations(Model model) {
        User currentUser = userService.getCurrentLoggedUser();
        String currentUserUsername = currentUser.getUsername();
        List<String> myFriendsRequests = friendService.getAllFriendsRequests();
        Integer myNumberOfFriendsInvitations = currentUser.getInvitations();
        Integer myNumberOfNewMessages = currentUser.getNewmessage();
        Integer myNumberOfNotifications = currentUser.getNotification();

        model.addAttribute("size", myFriendsRequests.size());
        model.addAttribute("users", myFriendsRequests);
        model.addAttribute("username", currentUserUsername);
        model.addAttribute("invitations", myNumberOfFriendsInvitations);
        model.addAttribute("newmessage", myNumberOfNewMessages);
        model.addAttribute("notifications", myNumberOfNotifications);
        return "invitations";
    }

    @RequestMapping(value = "/notifications")
    public String getNotifications(Model model) {
        User currentUser = userService.getCurrentLoggedUser();
        String currentUserUsername = currentUser.getUsername();
        currentUser.setNotification(0);
        userService.updateUser(currentUser);
        Integer myNumberOfFriendsInvitations = currentUser.getInvitations();
        Integer myNumberOfNewMessages = currentUser.getNewmessage();
        Integer myNumberOfNotifications = currentUser.getNotification();
        List<Notification> myNotifications = notificationService.getMyNotifications();

        model.addAttribute("size", notificationService.getMyNotifications().size());
        model.addAttribute("username", currentUserUsername);
        model.addAttribute("invitations", myNumberOfFriendsInvitations);
        model.addAttribute("newmessage", myNumberOfNewMessages);
        model.addAttribute("notification", myNumberOfNotifications);
        model.addAttribute("notifications", myNotifications);
        return "notifications";
    }

}