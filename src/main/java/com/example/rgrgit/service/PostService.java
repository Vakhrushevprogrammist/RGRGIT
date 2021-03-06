package com.example.rgrgit.service;

import com.example.rgrgit.model.Post;
import com.example.rgrgit.model.User;
import com.example.rgrgit.repository.PostRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private  UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private FriendService friendService;

    public List<Post> getMyAllPosts() {
        List<User> myfriends = friendService.getMyFriends();
        List<Post> myPosts = userService.getCurrentLoggedUser().getMyposts();
        List<List<Post>> lists = myfriends.stream().map(user -> user.getMyposts()).collect(Collectors.toList());
        List<Post> friendsPosts = lists.stream().flatMap(List::stream).collect(Collectors.toList());
        friendsPosts.addAll(myPosts);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        friendsPosts.sort(Comparator.comparing(message -> LocalDateTime.parse(message.getDate(), formatter)));
        List<Post> sortPosts = Lists.reverse(friendsPosts);
        return sortPosts;
    }

    @Transactional
    public void savePost(Post post) {
        User currentUser = userService.getCurrentLoggedUser();
        String currentUsername = currentUser.getUsername();
        List<Post> myPosts = currentUser.getMyposts();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dateTime.format(formatter);
        post.setDate(date);
        post.setAuthor(currentUsername);
        post.setUser(currentUser);
        postRepository.save(post);
        myPosts.add(post);
    }

    public Post getPostById(Integer id){
        return postRepository.getOne(id);
    }

    @Transactional
    public void updatePost(Post post){
        entityManager.merge(post);
    }

    public List<Post> getMyOwnPosts(){
        List<Post> myPosts = userService.getCurrentLoggedUser().getMyposts();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        myPosts.sort(Comparator.comparing(message -> LocalDateTime.parse(message.getDate(), formatter)));
        List<Post> sortPosts = Lists.reverse(myPosts);
        return sortPosts;
    }

    @Transactional
    public void changeLikedPost(Integer id) {
        User currentUser = userService.getCurrentLoggedUser();
        Post post = getPostById(id);
        post.setLiked(post.getLiked()-1);
        updatePost(post);
        List<Integer> likedPosts = currentUser.getLikedPosts();
        likedPosts.remove(id);
        currentUser.setLikedPosts(likedPosts);
        userService.updateUser(currentUser);;
    }

    @Transactional
    public void likePost(Integer id) {
        User currentUser = userService.getCurrentLoggedUser();
        User receiver = userService.getUserByName(getPostById(id).getAuthor());
        Post post = getPostById(id);
        post.setLiked(post.getLiked()+1);
        updatePost(post);
        List<Integer> likedPosts = currentUser.getLikedPosts();
        likedPosts.add(id);
        currentUser.setLikedPosts(likedPosts);
        userService.updateUser(currentUser);
        String text = " ???????????????????? ???????? ???????? ";
        notificationService.saveNotification(currentUser.getUsername(), post.getContent(), text, receiver);
    }

    @Transactional
    public void changeUnlikedPost(Integer id) {
        User currentUser = userService.getCurrentLoggedUser();
        Post post = getPostById(id);
        post.setUnliked(post.getUnliked()-1);
        updatePost(post);
        List<Integer> unlikedPosts = currentUser.getUnlikedPosts();
        unlikedPosts.remove(id);
        currentUser.setUnlikedPosts(unlikedPosts);
        userService.updateUser(currentUser);
    }

    @Transactional
    public void unlikePost(Integer id) {
        User currentUser = userService.getCurrentLoggedUser();
        User receiver = userService.getUserByName(getPostById(id).getAuthor());
        Post post = getPostById(id);
        post.setUnliked(post.getUnliked()+1);
        updatePost(post);
        List<Integer> unlikedPosts = currentUser.getUnlikedPosts();
        unlikedPosts.add(id);
        currentUser.setUnlikedPosts(unlikedPosts);
        userService.updateUser(currentUser);
        String text = " ???? ???????????? ?????? ???????? ";
        notificationService.saveNotification(currentUser.getUsername(), post.getContent(), text, receiver);
    }

    public List<Post> getPostsByUserId(Integer id) {
        User user = userService.getUserById(id);
        List<Post> posts = user.getMyposts();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        posts.sort(Comparator.comparing(message -> LocalDateTime.parse(message.getDate(), formatter)));
        List<Post> sortPosts = Lists.reverse(posts);
        return sortPosts;
    }
}

