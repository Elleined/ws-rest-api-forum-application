package com.forum.application.controller;

import com.forum.application.dto.ReplyDTO;
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
@RequestMapping("/forum/api/posts/comments/{commentId}/replies")
public class ReplyController {

    private final ForumService forumService;
    private final UserService userService;

    @GetMapping
    public List<ReplyDTO> getAllRepliesOf(@PathVariable("commentId") int commentId) {
        return forumService.getAllRepliesOf(commentId);
    }

    @GetMapping("/{replyId}")
    public ReplyDTO getById(@PathVariable("replyId") int replyId) {
        return forumService.getReplyById(replyId);
    }

    @PostMapping
    public ResponseEntity<?> saveReply(@PathVariable("commentId") int commentId,
                                       @RequestParam("body") String body,
                                       HttpSession session) {

        if (forumService.isEmpty(body)) return ResponseEntity.badRequest().body("Reply body cannot be empty!");

        String email = (String) session.getAttribute("email");
        int replierId = userService.getIdByEmail(email);

        int replyId = forumService.saveReply(replierId, commentId, body);
        ReplyDTO replyDTO = forumService.getReplyById(replyId);
        return ResponseEntity.ok(replyDTO);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyDTO> delete(@PathVariable("replyId") int replyId) {
        forumService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
}
