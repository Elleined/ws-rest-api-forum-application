package com.forum.application.controller;

import com.forum.application.dto.PostDTO;
import com.forum.application.service.ForumService;
import com.forum.application.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/forum/api/posts")
public class PostController {
    private final ForumService forumService;
    private final UserService userService;


    @GetMapping
    public List<PostDTO> getAllPost() {
        return forumService.getAllPost();
    }

    @PostMapping
    public ResponseEntity<?> savePost(@RequestParam String body,
                                      HttpSession session) {

        if (forumService.isEmpty(body)) return ResponseEntity.badRequest().body("Post body cannot be empty!");

        String loginEmailSession = (String) session.getAttribute("email");

        int authorId = userService.getIdByEmail(loginEmailSession);

        forumService.savePost(authorId, body);
        log.debug("Post saved successfully");
        return ResponseEntity.status(200).body("Post Created Successfully");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable int postId) {
        forumService.deletePost(postId);
        log.debug("Post deleted successfully");
        return ResponseEntity.status(204).body("Post Deleted Successfully");
    }
}
