package com.example.rgrgit.service;

import com.example.rgrgit.model.Role;
import com.example.rgrgit.model.User;
import com.example.rgrgit.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MailService mailService;

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public List<Object> getAllLogedUsers()
    {
        return sessionRegistry.getAllPrincipals();
    }

    public List<String> getAllUsersUsernames() {
        List<User> users =userRepository.findAll();
        List<String> allUsernames = users.stream().map(user1 -> user1.getUsername()).collect(Collectors.toList());
        return allUsernames;
    }

    public List<User> searchUsersByName(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        users.sort(Comparator.comparing(user1 -> user1.getUsername()));
        return users;
    }

    public User getUserByName(String name)
    {
        return userRepository.getUserByName(name);
    }

    public User getUserById(Integer id)
    {
        return userRepository.getOne(id);
    }

    public User getCurrentLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.getUserByName(username);
    }

    public boolean activateUser(String activationCode) {
        System.out.println("yo");
        Optional<User> u = userRepository.findByActivationCode(activationCode);
        if(u.isPresent()) {
            User user = u.get();
            user.setEnabled(true);
            user.setActivationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public void saveUser(User user) {
        Role role = new Role(user.getUsername(),"USER");
        user.setActivationCode(UUID.randomUUID().toString());
        mailService.sendGreetingMessage(user);
        roleRepository.save(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        entityManager.merge(user);
    }
}

